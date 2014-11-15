package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
public class MoodleCourseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle_course);

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            int idCours =  extras.getInt("idCours");
            String nameCours =  extras.getString("nameCours");
            if (idCours != 0) {

                Pattern pattern = Pattern.compile("([A-Z]{3,3}\\d{3,3})");

                Matcher matcher = pattern.matcher(nameCours);
                if(matcher.find()) {
                    setTitle(matcher.group());
                } else {
                    setTitle(nameCours);
                }

                Fragment fragment = MoodleCourseDetailsFragment.newInstance(idCours);
                getFragmentManager().beginTransaction().add(R.id.container, fragment, "MoodleCourseDetailsFragment").commit();
            }

        }
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
