package ca.etsmtl.applets.etsmobile.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile2.R;


public class UrgenceActivity extends AppCompatActivity {

    public static final String APPEL_D_URGENCE = "Appel d'urgence";
    private int id;

    private String[] urgence;
    private WebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urgence);
        id = getIntent().getExtras().getInt("id");
        webView = (WebView) findViewById(R.id.web_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        urgence = getResources().getStringArray(R.array.secu_urgence);

        id--;
        getSupportActionBar().setTitle(urgence[id]);
        String url = "";

        switch (id) {
            case 0:
                url = "file:///android_asset/urgence_resum_bombe.html";
                break;
            case 1:
                url = "file:///android_asset/urgence_resum_colis.html";
                break;
            case 2:
                url = "file:///android_asset/urgence_resum_feu.html";
                break;
            case 3:
                url = "file:///android_asset/urgence_resum_odeur.html";
                break;
            case 4:
                url = "file:///android_asset/urgence_resum_pane_asc.html";
                break;
            case 5:
                url = "file:///android_asset/urgence_resum_panne_elec.html";
                break;
            case 6:
                url = "file:///android_asset/urgence_resum_pers_arm.html";
                break;
            case 7:
                url = "file:///android_asset/urgence_resum_medic.html";
                break;
            default:
                break;
        }

        webView.loadUrl(url);

        webView.requestFocus();

        findViewById(R.id.urgence_appel_urgence).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        final String uri = "tel:"
                                + getString(R.string.secu_phone_lbl);
                        final Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));

                        AnalyticsHelper.getInstance(getApplication())
                                .sendActionEvent(getClass().getSimpleName(), APPEL_D_URGENCE);
                        startActivity(intent);
                    }
                });
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
