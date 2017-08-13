package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourses;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleAssignmentsAdapter;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 2017-08-12.
 */

public class MoodleAssignmentsActivity extends AppCompatActivity implements RequestListener<Object> {

    public static final String SEMESTER_KEY = "CurrentSemester";
    public static final String COURSES_KEY = "CurrentSemesterCourses";
    private static final String TAG = "MoodleAssignments";

    private DataManager dataManager;
    private ExpandableListView assignmentsElv;
    private LoadingView loadingView;
    private int[] coursesIds;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_assignments);
        setUpTitleBar();

        assignmentsElv = (ExpandableListView) findViewById(R.id.assignments_elv);
        loadingView = (LoadingView) findViewById(R.id.loading_view);

        coursesIds = getIntent().getIntArrayExtra(COURSES_KEY);

        dataManager = DataManager.getInstance(this);
        quueryAssignments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_moodle_assignments, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_moodle_assignments_submitted:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;
            case R.id.menu_item_moodle_assignments_graded:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void setUpTitleBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.moodle_assignments_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void quueryAssignments() {
        loadingView.showLoadingView();
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {
            @Override
            public MoodleAssignmentCourses loadDataFromNetwork() throws Exception {
                String coursesIdsStr = "";

                for (int id : coursesIds) {
                    coursesIdsStr += "&courseids[]=" + id;
                }
                String url = getString(R.string.moodle_api_assignments,
                        ApplicationManager.userCredentials.getMoodleToken(), coursesIdsStr);

                return getRestTemplate().getForObject(url, MoodleAssignmentCourses.class);
            }
        };

        dataManager.sendRequest(request, MoodleAssignmentsActivity.this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        loadingView.hideProgessBar();
        if (loadingView.isShown()) {
            loadingView.setMessageError(getString(R.string.error_JSON_PARSING));
        }
    }

    @Override
    public void onRequestSuccess(Object o) {
        if (o instanceof MoodleAssignmentCourses) {
            List<String> headers = new ArrayList<>();
            HashMap<String, List<MoodleAssignment>> childs = new HashMap<>();

            for (MoodleAssignmentCourse course : ((MoodleAssignmentCourses) o).getCourses()) {
                headers.add(course.getFullName());
                List<MoodleAssignment> assignments = new ArrayList<>();
                for (MoodleAssignment assignment : course.getAssignments())
                    assignments.add(assignment);

                childs.put(course.getFullName(), assignments);
            }

            ExpandableListMoodleAssignmentsAdapter adapter = new ExpandableListMoodleAssignmentsAdapter(this);
            adapter.setData(headers, childs);
            assignmentsElv.setAdapter(adapter);

            adapter.setData(headers, childs);
            LoadingView.hideLoadingView(loadingView);
        }
    }
}
