package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Sonphil on 2017-08-12.
 */

public class MoodleAssignmentsActivity extends AppCompatActivity {

    public static final String SEMESTER_KEY = "CurrentSemester";
    public static final String COURSES_KEY = "CurrentSemesterCourses";

    private TextView currentSemesterTv;
    private TextView coursesTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_assignments);
        setUpTitleBar();

        currentSemesterTv = (TextView) findViewById(R.id.current_semester_tv);
        currentSemesterTv.setText(getIntent().getStringExtra(SEMESTER_KEY));

        coursesTv = (TextView) findViewById(R.id.courses_tv);
        coursesTv.setText(Arrays.toString(getIntent().getIntArrayExtra(COURSES_KEY)));
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
}
