package ca.etsmtl.applets.etsmobile.ui.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
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
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleAssignmentsAdapter;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModel;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;
import ca.etsmtl.applets.etsmobile2.databinding.ActivityMoodleAssignmentsBinding;

/**
 * Created by Sonphil on 2017-08-12.
 */

public class MoodleAssignmentsActivity extends AppCompatActivity implements LifecycleRegistryOwner, ExpandableListView.OnChildClickListener {

    private static final String TAG = "MoodleAssignments";

    private ExpandableListView assignmentsElv;
    private LoadingView loadingView;
    private View bottomSheet;
    private Menu menu;
    private List<MoodleAssignmentCourse> assignmentsCourses;
    private boolean displayPastAssignments;
    private boolean requestInProgress;
    private Comparator<MoodleAssignment> dateComparator;
    private Comparator<MoodleAssignment> alphaComparator;
    private Comparator<MoodleAssignment> currentComparator;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private Observer<RemoteResource<List<MoodleAssignmentCourse>>> assignmentsCoursesObserver;
    private MoodleViewModel moodleViewModel;
    private BottomSheetBehavior bottomSheetBehavior;
    private ActivityMoodleAssignmentsBinding binding;
    private MoodleAssignment selectedAssignment;
    private FloatingActionButton openAssignmentFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_moodle_assignments);
        binding.setLoading(true);

        setUpTitleBar();

        assignmentsElv = findViewById(R.id.assignments_elv);
        loadingView = findViewById(R.id.loading_view);
        openAssignmentFab = findViewById(R.id.open_assignment_fab);

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
                        binding.setLoading(false);
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

        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    openAssignmentFab.setVisibility(View.VISIBLE);
                else
                    openAssignmentFab.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                openAssignmentFab.setVisibility(View.VISIBLE);
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

                Collections.sort(course.getAssignments(), currentComparator);

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

                childs.put(course.getFullName(), assignments);
            }

            ExpandableListMoodleAssignmentsAdapter adapter = new ExpandableListMoodleAssignmentsAdapter(this);
            adapter.setData(headers, childs);
            assignmentsElv.setAdapter(adapter);
            assignmentsElv.setOnChildClickListener(this);
            // TODO: Set empty view
            for (int i = 0; i < headers.size(); i++)
                assignmentsElv.expandGroup(i);

            binding.setLoading(false);
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        selectedAssignment = assignmentsCourses.get(groupPosition).getAssignments().get(childPosition);

        binding.setSelectedAssignment(selectedAssignment);

        bottomSheet.requestLayout();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        return true;
    }

    @SuppressLint("StringFormatMatches")
    public void openInBrowser(View v) {
        Utility.openChromeCustomTabs(this, String.format(getString(R.string.moodle_view_assignment), selectedAssignment.getCmid()));
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            super.onBackPressed();
    }
}
