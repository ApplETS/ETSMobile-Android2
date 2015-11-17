package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import ca.etsmtl.applets.etsmobile.ui.fragment.NewsDetailsFragment;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class NewsDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }

            setTitle("Détails");

            String from = extras.getString("from");
            String image = extras.getString("image");
            String title = extras.getString("title");
            String created_time = extras.getString("created_time");
            String facebook_link = extras.getString("facebook_link");
            String updated_time = extras.getString("updated_time");
            String message = extras.getString("message");
            String id = extras.getString("id");
            String icon_link = extras.getString("icon_link");

            Fragment fragment = NewsDetailsFragment.newInstance(from,image,title,created_time,facebook_link,updated_time,message,id,icon_link);
            getFragmentManager().beginTransaction().add(R.id.container, fragment, "NewsDetailsFragment").commit();
            

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
