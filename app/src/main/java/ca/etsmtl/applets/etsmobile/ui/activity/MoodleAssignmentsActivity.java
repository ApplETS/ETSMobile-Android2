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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    public static final String COURSES_KEY = "CurrentSemesterCourses";
    private static final String TAG = "MoodleAssignments";

    private DataManager dataManager;
    private ExpandableListView assignmentsElv;
    private LoadingView loadingView;
    private int[] coursesIds;
    private Menu menu;
    private List<MoodleAssignmentCourse> courses;
    private boolean displayPastAssignments;
    private boolean requestInProgress;
    private Comparator<MoodleAssignment> dateComparator;
    private Comparator<MoodleAssignment> alphaComparator;
    private Comparator<MoodleAssignment> currentComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_assignments);
        setUpTitleBar();

        assignmentsElv = (ExpandableListView) findViewById(R.id.assignments_elv);
        loadingView = (LoadingView) findViewById(R.id.loading_view);

        coursesIds = getIntent().getIntArrayExtra(COURSES_KEY);

        dateComparator = new Comparator<MoodleAssignment>() {
            @Override
            public int compare(MoodleAssignment a1, MoodleAssignment a2) {
                return a1.getDueDateObj().compareTo(a2.getDueDateObj());
            }
        };
        alphaComparator = new Comparator<MoodleAssignment>() {
            @Override
            public int compare(MoodleAssignment a1, MoodleAssignment a2) {
                return a1.getName().compareTo(a2.getName());
            }
        };
        currentComparator = dateComparator;

        dataManager = DataManager.getInstance(this);
        quueryAssignments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_moodle_assignments, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_moodle_previous_assignments:
                displayPastAssignments = !item.isChecked();
                break;
            case R.id.menu_item_moodle_sort_assignments_date:
                menu.getItem(1).setChecked(false);
                if (currentComparator != dateComparator)
                    currentComparator = dateComparator;
                break;
            case R.id.menu_item_moodle_sort_assignments_alpha:
                menu.getItem(0).setChecked(false);
                if (currentComparator != alphaComparator)
                    currentComparator = alphaComparator;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        item.setChecked(!item.isChecked());
        refreshUI();

        return true;
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
        requestInProgress = true;
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
        requestInProgress = false;
        loadingView.hideProgessBar();
        if (loadingView.isShown()) {
            loadingView.setMessageError(getString(R.string.error_JSON_PARSING));
        }
    }

    @Override
    public void onRequestSuccess(Object o) {
        requestInProgress = false;
        if (o instanceof MoodleAssignmentCourses) {
            courses = ((MoodleAssignmentCourses) o).getCourses();
            refreshUI();
        }
    }

    private void refreshUI() {
        if (!requestInProgress) {
            List<String> headers = new ArrayList<>();
            HashMap<String, List<MoodleAssignment>> childs = new HashMap<>();

            for (MoodleAssignmentCourse course : courses) {
                headers.add(course.getFullName());
                List<MoodleAssignment> assignments = new ArrayList<>();
                for (MoodleAssignment assignment : course.getAssignments()) {
                    if (!displayPastAssignments) {
                        Date dueDate = assignment.getDueDateObj();
                        Date currentDate = new Date();
                        if (dueDate.after(currentDate))
                            assignments.add(assignment);
                    } else
                        assignments.add(assignment);
                }

                Collections.sort(assignments, currentComparator);

                childs.put(course.getFullName(), assignments);
            }

            ExpandableListMoodleAssignmentsAdapter adapter = new ExpandableListMoodleAssignmentsAdapter(this);
            adapter.setData(headers, childs);
            assignmentsElv.setAdapter(adapter);
            for (int i = 0; i < headers.size(); i++)
                assignmentsElv.expandGroup(0);

            LoadingView.hideLoadingView(loadingView);
        }
    }
}
