package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import ca.etsmtl.applets.etsmobile.ui.fragment.NotesDetailsFragment;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class NotesDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_details);

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            String sigle = extras.getString("sigle");
            String sessionName = extras.getString("sessionName");
            String abrege = extras.getString("abrege");
            String cote = extras.getString("cote");
            String groupe = extras.getString("groupe");
            String titreCours = extras.getString("titreCours");

            setTitle(sigle);

            Fragment fragment = NotesDetailsFragment.newInstance(sigle, sessionName, abrege, cote, groupe, titreCours);
            getFragmentManager().beginTransaction().add(R.id.container, fragment, NotesDetailsFragment.class.getName()).commit();
        }

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
