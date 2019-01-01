package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.moodle.MoodleCourse;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.ui.activity.MoodleAssignmentsActivity;
import ca.etsmtl.applets.etsmobile.ui.activity.MoodleCourseActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.MoodleCoursesAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.CourseComparator;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModel;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModelFactory;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Interacts with Moodle API
 * 
 * @author Thibaut
 * 
 */
public class MoodleFragment extends BaseFragment {

    ListView moodleCoursesListView;
    private MoodleCoursesAdapter moodleCoursesAdapter;
    private String firstSemesterInserted;
    private String lastSemesterInserted;
    private List<MoodleCourse> firstSemesterInsertedCourses;
    private Menu menu;
    private LoadingView loadingView;
    private Observer<RemoteResource<List<MoodleCourse>>> coursesObserver;
    private MoodleViewModel moodleViewModel;
    @Inject
    MoodleViewModelFactory moodleViewModelFactory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        firstSemesterInsertedCourses = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_moodle, container, false);


        super.onCreateView(inflater, v, savedInstanceState);
        moodleCoursesListView = (ListView) v.findViewById(R.id.listView_moodle_courses);
        Log.d("MoodleFragment", "Moodle course list fragment is instantiated");

        loadingView = v.findViewById(R.id.loading_view);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

		return v;
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the view model factory
        ApplicationManager.getAppComponent().inject(this);

        moodleViewModel = ViewModelProviders.of(this, moodleViewModelFactory).get(MoodleViewModel.class);

        coursesObserver = new Observer<RemoteResource<List<MoodleCourse>>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<List<MoodleCourse>> moodleCoursesRemoteResource) {
                if (moodleCoursesRemoteResource != null) {
                    if (moodleCoursesRemoteResource.status == RemoteResource.SUCCESS) {
                        loadingView.hideProgessBar();
                        updateUI(moodleCoursesRemoteResource.data);
                    } else if (moodleCoursesRemoteResource.status == RemoteResource.ERROR) {
                        loadingView.hideProgessBar();
                        if (loadingView.isShown()) {
                            String errorMsg = getString(R.string.toast_Sync_Fail);
                            Toast t = Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG);
                            t.show();;
                        }
                    } else if (moodleCoursesRemoteResource.status == RemoteResource.LOADING) {
                        loadingView.showLoadingView();
                        if (moodleCoursesRemoteResource.data != null) {
                            updateUI(moodleCoursesRemoteResource.data);
                        }
                    }
                }
            }
        };
        moodleViewModel.getCourses().observe(this, coursesObserver);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_2_moodle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        toolbar.inflateMenu(R.menu.menu_moodle);
        this.menu = toolbar.getMenu();

        super.onCreateOptionsMenu(this.menu, inflater);

        if (menu.findItem(R.id.menu_item_moodle_assignments) != null && !moodleViewModel.isShowCaseHasBeenDisplayed()) {
            new Handler().post(() -> {
                try {
                    TapTargetView.showFor(getActivity(), TapTarget.forToolbarMenuItem(toolbar,
                            R.id.menu_item_moodle_assignments,
                            getString(R.string.moodle_assignments_title),
                            getString(R.string.moodle_assignments_description)));
                    moodleViewModel.setShowCaseHasBeenDisplayed(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_moodle_assignments:
                Intent intent = new Intent(getActivity(), MoodleAssignmentsActivity.class);
                getActivity().startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(List<MoodleCourse> moodleCourses) {
        moodleCoursesAdapter = new MoodleCoursesAdapter(getActivity(), R.layout.row_moodle_course);
        Collections.sort(moodleCourses, new CourseComparator());
        Collections.reverse(moodleCourses); // To get the most current semester first
        String semesterString;
        List<String> semesterList = new ArrayList<>();
        boolean insertingFirstSemester = true;
        for (int i = 0; i < moodleCourses.size(); i++) {
            MoodleCourse moodleCourse = moodleCourses.get(i);

            if(moodleCourse.getFullname().matches("(.*)([AÉH](\\d){4})(.*)")) {
                semesterString = moodleCourse.getFullname().replace("(", "{").split("\\{")[1].replace(")", "");
            }
            else
                semesterString = null;
            semesterString = convertSemesterString(semesterString);
            if (i == 0)
                firstSemesterInserted = semesterString;
            if (!semesterList.contains(semesterString)) {
                semesterList.add(semesterString);
                MoodleCourse courseSemesterSeparator = new MoodleCourse();
                courseSemesterSeparator.setCourseSemester(semesterString);
                moodleCoursesAdapter.addSectionHeader(courseSemesterSeparator);
                moodleCoursesAdapter.addCourse(moodleCourse);
                        /*if (semesterString.equals(firstSemesterInserted))
                            firstSemesterInsertedCourses.add(moodleCourse);
                        else
                            insertingFirstSemester = false;*/
                firstSemesterInsertedCourses.add(moodleCourse);
            } else {
                moodleCoursesAdapter.addCourse(moodleCourse);
                if (insertingFirstSemester)
                    firstSemesterInsertedCourses.add(moodleCourse);
            }
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
            return lastSemesterInserted;
        else
            switch(semester.charAt(0)) {
                case 'A':
                    lastSemesterInserted = "Automne " + semester.replace("A", "");
                    return "Automne " + semester.replace("A", "");

                case 'E':
                case 'É':
                    lastSemesterInserted = "Été " + semester.replace("É", "");
                    return "Été " + semester.replace("É", "");

                case 'H':
                    lastSemesterInserted = "Hiver " + semester.replace("H", "");
                    return "Hiver " + semester.replace("H", "");

                default:
                    return lastSemesterInserted;

            }
    }
}
