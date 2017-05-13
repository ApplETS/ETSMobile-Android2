package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleToken;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.ui.activity.MoodleCourseActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.MoodleCoursesAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.CourseComparator;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Interacts with Moodle API
 * 
 * @author Thibaut
 * 
 */
public class MoodleFragment extends HttpFragment {


    ListView moodleCoursesListView;
    private MoodleCoursesAdapter moodleCoursesAdapter;
    private String lastInserted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_moodle, container, false);


        super.onCreateView(inflater, v, savedInstanceState);
        moodleCoursesListView = (ListView) v.findViewById(R.id.listView_moodle_courses);
        Log.d("MoodleFragment", "Moodle course list fragment is instantiated");


        queryMoodleToken();

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

		return v;
	}

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_2_moodle);
    }

    @Override
    void updateUI() {
        loadingView.showLoadingView();
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        super.onRequestFailure(e);
    }

    @Override
    public void onRequestSuccess(Object o) {
        try {
            if (o instanceof MoodleToken) {

                MoodleToken moodleToken = (MoodleToken) o;

                SecurePreferences securePreferences = new SecurePreferences(getActivity());
                securePreferences.edit().putString(UserCredentials.MOODLE_TOKEN, moodleToken.getToken()).commit();

                ApplicationManager.userCredentials.setMoodleToken(moodleToken.getToken());

                if (moodleToken.getToken().equals("")) {
                    throw new Exception("Impossible de se connecter");
                }

                queryMoodleProfile(moodleToken);

            }

            if (o instanceof MoodleProfile) {
                MoodleProfile moodleProfile = (MoodleProfile) o;

                queryMoodleCourses(moodleProfile);
            }

            if (o instanceof MoodleCourses) {
                MoodleCourses moodleCourses = (MoodleCourses) o;
                moodleCoursesAdapter = new MoodleCoursesAdapter(getActivity(), R.layout.row_moodle_course, this);
                Collections.sort(moodleCourses, new CourseComparator());
                Collections.reverse(moodleCourses); // To get the most current semester first
                String semesterString;
                List<String> semesterList = new ArrayList<>();
                for(MoodleCourse moodleCourse : moodleCourses) {

                    if(moodleCourse.getFullname().matches("(.*)([AÉH](\\d){4})(.*)"))
                        semesterString = moodleCourse.getFullname().replace("(", "{").split("\\{")[1].replace(")", "");
                    else
                        semesterString = null;
                    semesterString = convertSemesterString(semesterString);
                    if(!semesterList.contains(semesterString)) {
                        semesterList.add(semesterString);
                        MoodleCourse courseSemesterSeparator = new MoodleCourse();
                        courseSemesterSeparator.setCourseSemester(semesterString);
                        moodleCoursesAdapter.addSectionHeader(courseSemesterSeparator);
                        moodleCoursesAdapter.addCourse(moodleCourse);
                    }
                    else
                        moodleCoursesAdapter.addCourse(moodleCourse);
                }

                moodleCoursesListView.setAdapter(moodleCoursesAdapter);


                moodleCoursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MoodleCourse moodleCourse = (MoodleCourse) parent.getItemAtPosition(position);
                        if(moodleCourse.getId() != MoodleCourse.IS_SEMESTER) {
                            Intent i = new Intent(getActivity(), MoodleCourseActivity.class);
                            i.putExtra("idCours", moodleCourse.getId());
                            i.putExtra("nameCours", moodleCourse.getShortname());
                            getActivity().startActivity(i);
                        }

                    }
                });

                super.onRequestSuccess(null);
            }
        }catch (Exception e) {
            Log.w("MoodleFragment", "Exception caught in onRequestSuccess: " + e);
            if(getActivity()!=null)
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    /**
     * Query for moodle courses
     * @param moodleProfile
     */
    private void queryMoodleCourses(final MoodleProfile moodleProfile) {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleCourses loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_enrol_get_users_courses, ApplicationManager.userCredentials.getMoodleToken(),moodleProfile.getUserId());
                Log.d("loadDataFromNetwork", "getting url for moodle server class list");
                return getRestTemplate().getForObject(url, MoodleCourses.class);
            }
        };

        dataManager.sendRequest(request, MoodleFragment.this);


    }

    /**
     * Query for Moodle profile
     * @param moodleToken
     */
    private void queryMoodleProfile(final MoodleToken moodleToken) {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleProfile loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_get_siteinfo, moodleToken.getToken());

                return getRestTemplate().getForObject(url, MoodleProfile.class);
            }
        };

        dataManager.sendRequest(request, MoodleFragment.this);
    }

    /**
     * Query for Moodle token
     */
    private void queryMoodleToken() {
        if(getActivity()!=null) {
            SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

                @Override
                public MoodleToken loadDataFromNetwork() throws Exception {
                    String url = getActivity().getString(R.string.moodle_api_get_token, ApplicationManager.userCredentials.getUsername(), ApplicationManager.userCredentials.getPassword());

                    return getRestTemplate().getForObject(url, MoodleToken.class);
                }
            };

            dataManager.sendRequest(request, MoodleFragment.this);
        }

    }

    /**
     * @deprecated Opens Moodle's official application
     */
    private void openMoodle() {
		Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getString(R.string.moodle));
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			// bring user to the market
			// or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id=" + getString(R.string.moodle)));
			startActivity(intent);
		}
	}

    private String convertSemesterString(String semester) {

        if(semester == null)
            return lastInserted;
        else
            switch(semester.charAt(0)) {
                case 'A':
                    lastInserted = "Automne " + semester.replace("A", "");
                    return "Automne " + semester.replace("A", "");

                case 'E':
                case 'É':
                    lastInserted = "Été " + semester.replace("É", "");
                    return "Été " + semester.replace("É", "");

                case 'H':
                    lastInserted = "Hiver " + semester.replace("H", "");
                    return "Hiver " + semester.replace("H", "");

                default:
                    return lastInserted;

            }


    }

}
