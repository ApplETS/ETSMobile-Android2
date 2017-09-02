package ca.etsmtl.applets.etsmobile.ui.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCourses;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleAssignmentsAdapter;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModel;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 2017-08-12.
 */

public class MoodleAssignmentsActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private static final String TAG = "MoodleAssignments";

    private ExpandableListView assignmentsElv;
    private LoadingView loadingView;
    private Menu menu;
    private List<MoodleAssignmentCourse> assignmentsCourses;
    private MoodleCourses courses;
    private boolean displayPastAssignments;
    private boolean requestInProgress;
    private Comparator<MoodleAssignment> dateComparator;
    private Comparator<MoodleAssignment> alphaComparator;
    private Comparator<MoodleAssignment> currentComparator;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private Observer<RemoteResource<List<MoodleAssignmentCourse>>> assignmentsCoursesObserver;
    private MoodleViewModel moodleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_assignments);
        setUpTitleBar();

        assignmentsElv = findViewById(R.id.assignments_elv);
        loadingView = findViewById(R.id.loading_view);

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

        assignmentsCoursesObserver = new Observer<RemoteResource<List<MoodleAssignmentCourse>>>() {

            @Override
            public void onChanged(@Nullable RemoteResource<List<MoodleAssignmentCourse>> listRemoteResource) {
                if (listRemoteResource != null) {
                    if (listRemoteResource.status == RemoteResource.SUCCESS) {
                        requestInProgress = false;
                        assignmentsCourses = listRemoteResource.data;
                        refreshUI();
                    } else if (listRemoteResource.status == RemoteResource.ERROR) {
                        requestInProgress = false;
                        loadingView.hideProgessBar();
                        if (loadingView.isShown()) {
                            loadingView.setMessageError(getString(R.string.error_JSON_PARSING));
                        }
                    } else if (listRemoteResource.status == RemoteResource.LOADING) {
                        requestInProgress = true;
                    }
                }
            }
        };

        moodleViewModel = ViewModelProviders.of(this).get(MoodleViewModel.class);
        moodleViewModel.getAssignmentCourses().observe(this, assignmentsCoursesObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        moodleViewModel.getAssignmentCourses().removeObserver(assignmentsCoursesObserver);
        assignmentsCoursesObserver = null;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    private void refreshUI() {
        if (!requestInProgress) {
            List<String> headers = new ArrayList<>();
            HashMap<String, List<MoodleAssignment>> childs = new HashMap<>();

            for (MoodleAssignmentCourse course : assignmentsCourses) {
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
            // TODO: Set empty view
            for (int i = 0; i < headers.size(); i++)
                assignmentsElv.expandGroup(i);

            LoadingView.hideLoadingView(loadingView);
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
