package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleCourseDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Displays content of a Moodle course
 *
 * @author Thibaut
 */
public class MoodleCourseActivity extends AppCompatActivity {

    int idCours;
    String nameCours;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MoodleActivity", "entered onCreate");
        setContentView(R.layout.activity_moodle_course);

        if (savedInstanceState == null) {
            Log.w("MoodleActivity", "savedInstance is null");
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            idCours = extras.getInt("idCours");
            nameCours = extras.getString("nameCours");

            fragment = MoodleCourseDetailsFragment.newInstance(idCours);
            Log.d("MoodleActivity", "fragment created when saved instance = null with idCours :" + idCours);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();   Log.d("moodleActivity", "fragment shown and created");
        } else {
            idCours = savedInstanceState.getInt("idCours");
            nameCours = savedInstanceState.getString("nameCours");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("moodleActivity", "entered onResume");
        if (idCours != 0) {

            Pattern pattern = Pattern.compile("([A-Z]{3,3}\\d{3,3})");

            Matcher matcher = pattern.matcher(nameCours);
            if (matcher.find()) {
                setTitle(matcher.group());
            } else {
                setTitle(nameCours);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("idCours", idCours);
        Log.d("onSaveInstanceState", "registering idCours value = " + idCours);
        outState.putString("nameCours", nameCours);
        Log.d("onSaveInstanceState", "registering nameCours = " + nameCours);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_moodle_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
