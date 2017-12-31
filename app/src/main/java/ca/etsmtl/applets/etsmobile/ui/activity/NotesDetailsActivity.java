package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ca.etsmtl.applets.etsmobile.ui.fragment.NotesDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class NotesDetailsActivity extends AppCompatActivity {

    private static final String NOTES_DETAILS_FRAGMENT_TAG = "NotesDetailsFragment";

    private Toolbar toolbar;

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

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(NOTES_DETAILS_FRAGMENT_TAG);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }

            fragment = NotesDetailsFragment.newInstance(sigle, sessionName, abrege, cote, groupe, titreCours);

            fragmentTransaction.add(R.id.container, fragment, NOTES_DETAILS_FRAGMENT_TAG).commit();

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setTitle(titreCours);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setTitle(String.format(getString(R.string.notes_details_sigle_groupe), sigle, groupe));

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
