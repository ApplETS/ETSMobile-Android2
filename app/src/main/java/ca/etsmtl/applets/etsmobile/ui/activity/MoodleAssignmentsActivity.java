package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourses;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 2017-08-12.
 */

public class MoodleAssignmentsActivity extends AppCompatActivity implements RequestListener<Object> {

    public static final String SEMESTER_KEY = "CurrentSemester";
    public static final String COURSES_KEY = "CurrentSemesterCourses";
    private static final String TAG = "MoodleAssignments";

    private DataManager dataManager;
    private TextView currentSemesterTv;
    private ExpandableListView assignmentsElv;
    private int[] coursesIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_assignments);
        setUpTitleBar();

        currentSemesterTv = (TextView) findViewById(R.id.current_semester_tv);
        currentSemesterTv.setText(getIntent().getStringExtra(SEMESTER_KEY));

        assignmentsElv = (ExpandableListView) findViewById(R.id.assignments_elv);

        coursesIds = getIntent().getIntArrayExtra(COURSES_KEY);

        dataManager = DataManager.getInstance(this);
        quueryAssignments();
    }

    private void setUpTitleBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    }

    @Override
    public void onRequestSuccess(Object o) {
        List<String> headers = new ArrayList<>();

        if (o instanceof MoodleAssignmentCourses) {
            for (MoodleAssignmentCourse course : ((MoodleAssignmentCourses) o).getCourses()) {
                headers.add(course.getFullName());
            }
        }
    }
}
