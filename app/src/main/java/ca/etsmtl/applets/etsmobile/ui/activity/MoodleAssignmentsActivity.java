package ca.etsmtl.applets.etsmobile.ui.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.LiveData;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
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
    private static final float BS_MIN_OFFSET_HIDE_FAB = -0.8f;
    private static final String SHOW_BS_KEY = "ShowBS";

    private ExpandableListView assignmentsElv;
    private LoadingView loadingView;
    private View bottomSheet;
    private Menu menu;
    private List<MoodleAssignmentCourse> filteredAssignmentsCourses;
    private boolean requestInProgress;
    private Comparator<MoodleAssignment> dateComparator;
    private Comparator<MoodleAssignment> alphaComparator;
    private Comparator<MoodleAssignment> currentComparator;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private Observer<RemoteResource<List<MoodleAssignmentCourse>>> assignmentsCoursesObserver;
    private MoodleViewModel moodleViewModel;
    private BottomSheetBehavior bottomSheetBehavior;
    private float bottomSheetOffset;
    private ActivityMoodleAssignmentsBinding binding;
    private FloatingActionButton openAssignmentFab;
    private View emptyView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_moodle_assignments);
        binding.setLoading(true);

        setUpTitleBar();

        assignmentsElv = findViewById(R.id.assignments_elv);
        loadingView = findViewById(R.id.loading_view);
        openAssignmentFab = findViewById(R.id.open_assignment_fab);
        emptyView = findViewById(R.id.empty_view);

        setUpSortComparators();

        subscribeUIList();

        setUpBottomSheet();

        if (savedInstanceState != null && savedInstanceState.getBoolean(SHOW_BS_KEY)) {
            MoodleAssignment selectedAssignment = moodleViewModel.getSelectedAssignment();
            if (selectedAssignment != null) {
                displaySelectedAssignment(selectedAssignment);
                openAssignmentFab.show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        boolean bSShown = bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
        outState.putBoolean(SHOW_BS_KEY, bSShown);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        moodleViewModel.getAssignmentCourses().removeObservers(this);
        assignmentsCoursesObserver = null;
    }

    private void setUpSortComparators() {
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
    }

    public void subscribeUIList() {
        assignmentsCoursesObserver = new Observer<RemoteResource<List<MoodleAssignmentCourse>>>() {

            @Override
            public void onChanged(@Nullable RemoteResource<List<MoodleAssignmentCourse>> listRemoteResource) {
                if (listRemoteResource != null) {
                    if (listRemoteResource.status == RemoteResource.SUCCESS) {
                        requestInProgress = false;
                        refreshUI();
                    } else if (listRemoteResource.status == RemoteResource.ERROR) {
                        requestInProgress = false;
                        if (loadingView.isShown()) {
                            loadingView.hideProgessBar();
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

    private void setUpBottomSheet() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > bottomSheetOffset)
                    openAssignmentFab.show();
                else if (slideOffset <= BS_MIN_OFFSET_HIDE_FAB)
                    //openAssignmentFab.hide();
                    openAssignmentFab.setVisibility(View.GONE);

                bottomSheetOffset = slideOffset;
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_moodle_assignments, menu);

        this.menu = menu;

        menu.findItem(R.id.menu_item_moodle_previous_assignments).setChecked(moodleViewModel.isDisplayPastAssingments());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_moodle_previous_assignments:
                moodleViewModel.setDisplayPastAssignments(!item.isChecked());
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

            filteredAssignmentsCourses = moodleViewModel.filterCourses().getValue();

            for (MoodleAssignmentCourse course : filteredAssignmentsCourses) {
                headers.add(course.getFullName());

                Collections.sort(course.getAssignments(), currentComparator);

                List<MoodleAssignment> assignments = new ArrayList<>();
                for (MoodleAssignment assignment : course.getAssignments()) {
                    if (!moodleViewModel.isDisplayPastAssingments()) {
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
            assignmentsElv.setEmptyView(emptyView);
            assignmentsElv.setAdapter(adapter);
            assignmentsElv.setOnChildClickListener(this);
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
        displaySelectedAssignment(moodleViewModel.selectAssignment(groupPosition, childPosition));

        return true;
    }

    private void displaySelectedAssignment(final MoodleAssignment selectedAssignment) {
        binding.setSelectedAssignment(selectedAssignment);

        final LiveData<RemoteResource<MoodleAssignmentSubmission>> submissionLiveData = moodleViewModel.getAssignmentSubmission(selectedAssignment.getId());
        submissionLiveData.observe(MoodleAssignmentsActivity.this, new Observer<RemoteResource<MoodleAssignmentSubmission>>() {
            @Override
            public void onChanged(@Nullable RemoteResource<MoodleAssignmentSubmission> moodleAssignmentFeedbackRemoteResource) {
                boolean noLongerNeedtoObserve = true;

                if (moodleAssignmentFeedbackRemoteResource == null || moodleAssignmentFeedbackRemoteResource.status == RemoteResource.ERROR) {
                    binding.setSelectedAssignmentFeedback(null);
                    binding.setSelectedAssignmentLastAttempt(null);
                    binding.setLoadingSelectedAssignmentSubmission(false);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                    Toast toast = Toast.makeText(MoodleAssignmentsActivity.this, getString(R.string.error_JSON_PARSING), Toast.LENGTH_SHORT);
                    toast.show();
                } else if (moodleAssignmentFeedbackRemoteResource.status == RemoteResource.SUCCESS) {
                    MoodleAssignmentSubmission submission = moodleAssignmentFeedbackRemoteResource.data;

                    if (submission != null) {
                        MoodleAssignmentSubmission.MoodleAssignmentFeedback feedback = submission.getFeedback();

                        if (feedback != null) {
                            if (submission.getFeedback().getGrade().getAssignment() == selectedAssignment.getId()) {

                                binding.setSelectedAssignmentFeedback(moodleAssignmentFeedbackRemoteResource.data.getFeedback());
                                binding.setSelectedAssignmentLastAttempt(moodleAssignmentFeedbackRemoteResource.data.getLastAttempt());
                            }
                        } else {
                            binding.setSelectedAssignmentLastAttempt(null);
                        }
                    } else {
                        binding.setSelectedAssignmentFeedback(null);
                        binding.setSelectedAssignmentLastAttempt(null);
                    }

                    binding.setLoadingSelectedAssignmentSubmission(false);
                } else if (moodleAssignmentFeedbackRemoteResource.status == RemoteResource.LOADING) {
                    binding.setLoadingSelectedAssignmentSubmission(true);
                    noLongerNeedtoObserve = false;
                }

                if (noLongerNeedtoObserve)
                    submissionLiveData.removeObserver(this);
            }
        });

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void openInBrowser(View v) {
        Utility.openChromeCustomTabs(this, String.format(getString(R.string.moodle_view_assignment), String.valueOf(moodleViewModel.getSelectedAssignment().getCmid())));
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            super.onBackPressed();
    }
}
