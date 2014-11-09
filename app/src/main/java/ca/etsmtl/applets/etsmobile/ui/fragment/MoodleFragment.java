package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleProfile;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleToken;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.ui.activity.MoodleCourseActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.MoodleCoursesAdapter;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_moodle, container, false);

        moodleCoursesListView = (ListView) v.findViewById(R.id.listView_moodle_courses);
        queryMoodleToken();

		return v;
	}



    @Override
    void updateUI() {

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

                moodleCoursesAdapter = new MoodleCoursesAdapter(getActivity(), R.layout.row_moodle_course, moodleCourses, this);
                moodleCoursesListView.setAdapter(moodleCoursesAdapter);


                moodleCoursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MoodleCourse moodleCourse = (MoodleCourse) parent.getItemAtPosition(position);
                        Intent i = new Intent(getActivity(), MoodleCourseActivity.class);
                        i.putExtra("idCours", moodleCourse.getId());
                        i.putExtra("nameCours", moodleCourse.getShortname());
                        getActivity().startActivity(i);

                    }
                });

            }
        }catch (Exception e) {
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
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleToken loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_get_token, ApplicationManager.userCredentials.getUsername(), ApplicationManager.userCredentials.getPassword());

                return getRestTemplate().getForObject(url, MoodleToken.class);
            }
        };

        dataManager.sendRequest(request, MoodleFragment.this);

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

}
