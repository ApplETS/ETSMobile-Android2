package ca.etsmtl.applets.etsmobile.ui.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignment;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleAssignmentSubmission;
import ca.etsmtl.applets.etsmobile.model.RemoteResource;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleAssignmentsAdapter;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModel;
import ca.etsmtl.applets.etsmobile.view_model.MoodleViewModelFactory;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;
import ca.etsmtl.applets.etsmobile2.databinding.ActivityMoodleAssignmentsBinding;

/**
 * Created by Sonphil on 2017-08-12.
 */

public class MoodleAssignmentsActivity extends AppCompatActivity {

    private static final String TAG = "MoodleAssignments";
    private static final float BS_MIN_OFFSET_HIDE_FAB = -0.8f;
    private static final String SHOW_BS_KEY = "ShowBS";

    private ExpandableListView assignmentsElv;
    private LoadingView loadingView;
    private View bottomSheet;
    private Menu menu;
    private List<MoodleAssignmentCourse> filteredAssignmentsCourses;
    private boolean requestInProgress;
    private Observer<RemoteResource<List<MoodleAssignmentCourse>>> assignmentsCoursesObserver;
    private Observer<MoodleAssignment> selectedAssignmentObserver;
    private MoodleViewModel moodleViewModel;
    private BottomSheetBehavior bottomSheetBehavior;
    private float bottomSheetOffset;
    private ActivityMoodleAssignmentsBinding binding;
    private FloatingActionButton openAssignmentFab;
    private View emptyView;
    @Inject
    MoodleViewModelFactory moodleViewModelFactory;

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
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.ets_red), android.graphics.PorterDuff.Mode.SRC_IN);

        subscribeUIList();
        subscribeUISelectedAssignment();

        setUpBottomSheet();

        if (savedInstanceState != null && savedInstanceState.getBoolean(SHOW_BS_KEY)) {
            MoodleAssignment selectedAssignment = moodleViewModel.getSelectedAssignment().getValue();

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
    protected void onResume() {
        super.onResume();

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN)
            openAssignmentFab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        openAssignmentFab.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        moodleViewModel.getAssignmentCourses().removeObservers(this);
        assignmentsCoursesObserver = null;
    }

    public void subscribeUIList() {
        assignmentsCoursesObserver = listRemoteResource -> {
            if (listRemoteResource != null) {
                if (listRemoteResource.status == RemoteResource.SUCCESS) {
                    requestInProgress = false;
                    refreshUI();
                } else if (listRemoteResource.status == RemoteResource.ERROR) {
                    requestInProgress = false;
                    loadingView.hideProgessBar();
                    loadingView.setMessageError(getString(R.string.error_JSON_PARSING)
                            + "\n" + listRemoteResource.message);
                } else if (listRemoteResource.status == RemoteResource.LOADING) {
                    requestInProgress = true;
                }
            }

        };

        // Get the view model factory
        ApplicationManager.getAppComponent().inject(this);

        moodleViewModel = ViewModelProviders.of(this, moodleViewModelFactory).get(MoodleViewModel.class);
        moodleViewModel.getAssignmentCourses().observe(this, assignmentsCoursesObserver);
    }

    private void subscribeUISelectedAssignment() {
        selectedAssignmentObserver = moodleAssignment -> {
            if (moodleAssignment != null) {
                displaySelectedAssignment(moodleAssignment);
            }
        };

        moodleViewModel.getSelectedAssignment().observe(this, selectedAssignmentObserver);
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

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;

        menu.findItem(R.id.menu_item_moodle_previous_assignments).setChecked(moodleViewModel.isDisplayPastAssignments());

        SubMenu sortSubMenu = menu.getItem(0).getSubMenu();
        sortSubMenu.getItem(moodleViewModel.getAssignmentsSortIndex()).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int itemId = item.getItemId();

        if (itemId == R.id.menu_item_moodle_previous_assignments) {
            boolean checked = item.isChecked();
            item.setChecked(!checked);
            moodleViewModel.setDisplayPastAssignments(!checked);
        } else { // Option de tri sélectionné
            if (itemId == R.id.menu_item_moodle_sort_assignments_date) {
                moodleViewModel.setAssignmentsSortIndex(MoodleViewModel.SORT_BY_DATE);
            } else if (itemId == R.id.menu_item_moodle_sort_assignments_alpha) {
                moodleViewModel.setAssignmentsSortIndex(MoodleViewModel.SORT_ALPHA);
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        refreshUI();

        return true;
    }

    private void setUpTitleBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.moodle_assignments_title));
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void refreshUI() {
        if (!requestInProgress) {
            List<String> headers = new ArrayList<>();
            HashMap<String, List<MoodleAssignment>> childs = new HashMap<>();

            filteredAssignmentsCourses = moodleViewModel.filterAssignmentCourses().getValue();

            Comparator<MoodleAssignment> currentComparator = moodleViewModel.getAssignmentsSortComparator();

            for (int i = filteredAssignmentsCourses.size() - 1; i >= 0; i--) {
                MoodleAssignmentCourse course = filteredAssignmentsCourses.get(i);
                headers.add(course.getFullName());

                Collections.sort(course.getAssignments(), currentComparator);

                List<MoodleAssignment> assignments = new ArrayList<>();
                for (MoodleAssignment assignment : course.getAssignments()) {
                    if (!moodleViewModel.isDisplayPastAssignments()) {
                        Date dueDate = assignment.getDueDateObj();
                        Date currentDate = new Date();
                        if (dueDate.after(currentDate))
                            assignments.add(assignment);
                    } else
                        assignments.add(assignment);
                }

                childs.put(course.getFullName(), assignments);
            }

            ExpandableListMoodleAssignmentsAdapter adapter = new ExpandableListMoodleAssignmentsAdapter(this, moodleViewModel);
            adapter.setData(headers, childs);
            assignmentsElv.setEmptyView(emptyView);
            assignmentsElv.setAdapter(adapter);
            for (int i = 0; i < headers.size(); i++)
                assignmentsElv.expandGroup(i);

            binding.setLoading(false);
        }
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

                    bottomSheet.requestLayout();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                    String errorMsg = moodleAssignmentFeedbackRemoteResource == null ? getString(R.string.error_JSON_PARSING) : moodleAssignmentFeedbackRemoteResource.message;
                    Toast toast = Toast.makeText(MoodleAssignmentsActivity.this, errorMsg, Toast.LENGTH_SHORT);
                    toast.show();
                } else if (moodleAssignmentFeedbackRemoteResource.status == RemoteResource.SUCCESS) {
                    MoodleAssignmentSubmission submission = moodleAssignmentFeedbackRemoteResource.data;

                    if (submission != null) {
                        binding.setSelectedAssignmentFeedback(submission.getFeedback());
                        binding.setSelectedAssignmentLastAttempt(submission.getLastAttempt());
                    } else {
                        binding.setSelectedAssignmentFeedback(null);
                        binding.setSelectedAssignmentLastAttempt(null);
                    }

                    binding.setLoadingSelectedAssignmentSubmission(false);
                } else if (moodleAssignmentFeedbackRemoteResource.status == RemoteResource.LOADING) {
                    binding.setLoadingSelectedAssignmentSubmission(true);
                    noLongerNeedtoObserve = false;

                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheet.requestLayout();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                if (noLongerNeedtoObserve)
                    submissionLiveData.removeObserver(this);
            }
        });
    }

    public void openInBrowser(View v) {
        MoodleAssignment selectedAssignment = moodleViewModel.getSelectedAssignment().getValue();

        if (selectedAssignment != null) {
            Utility.openChromeCustomTabs(this,
                    String.format(getString(R.string.moodle_view_assignment),
                            String.valueOf(selectedAssignment.getCmid())));
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            super.onBackPressed();
    }
}
