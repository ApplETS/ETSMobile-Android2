package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_course);


        if (savedInstanceState == null) {
            Log.w("MoodleActivity", "savedInstance is null");
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            idCours = extras.getInt("idCours");
            nameCours = extras.getString("nameCours");

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(nameCours);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        outState.putString("nameCours", nameCours);
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
