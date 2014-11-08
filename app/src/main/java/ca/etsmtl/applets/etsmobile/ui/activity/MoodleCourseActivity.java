package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.ui.fragment.MoodleCourseDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

public class MoodleCourseActivity extends Activity {

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

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
                }
//                setTitle(nameCours);

                Fragment fragment = MoodleCourseDetailsFragment.newInstance(idCours);
                getFragmentManager().beginTransaction().add(R.id.container, fragment, "MoodleCourseDetailsFragment").commit();
//                        .addToBackStack(null).commit();

//                getFragmentManager().beginTransaction()
//                        .add(R.id.container, new PlaceholderFragment())
//                        .commit();
            }




        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_moodle_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_moodle_course, container, false);
            return rootView;
        }
    }
}
