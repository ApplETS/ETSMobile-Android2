package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import ca.etsmtl.applets.etsmobile.ui.fragment.BottinDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class BottinDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottin_details);

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            setTitle("Détails");

            String nom = extras.getString("nom");
            String prenom = extras.getString("prenom");
            String telBureau = extras.getString("telBureau");
            String emplacement = extras.getString("emplacement");
            String courriel = extras.getString("courriel");
            String service = extras.getString("service");
            String titre = extras.getString("titre");


            Fragment fragment = BottinDetailsFragment.newInstance(nom, prenom, telBureau, emplacement, courriel, service, titre);
            getFragmentManager().beginTransaction().add(R.id.container, fragment, BottinDetailsFragment.class.getName()).commit();


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
