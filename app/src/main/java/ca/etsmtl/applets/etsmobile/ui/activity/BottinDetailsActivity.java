package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile.ui.fragment.BottinDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class BottinDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottin_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            setTitle("Details");
            String nom = extras.getString("nom");
            String prenom = extras.getString("prenom");
            String telBureau = extras.getString("telBureau");
            String emplacement = extras.getString("emplacement");
            String courriel = extras.getString("courriel");
            String service = extras.getString("service");
            String titre = extras.getString("titre");


            Fragment fragment = BottinDetailsFragment.newInstance(nom, prenom, telBureau, emplacement, courriel, service, titre);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();

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
