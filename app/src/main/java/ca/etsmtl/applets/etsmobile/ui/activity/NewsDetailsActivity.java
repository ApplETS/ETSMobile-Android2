package ca.etsmtl.applets.etsmobile.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile.ui.adapter.NewsAdapter;
import ca.etsmtl.applets.etsmobile.util.NewsComparator;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 12/19/14.
 */
public class NewsDetailsActivity extends Activity {
    ListView listView;
    String key;
    String name;
    String type;
    String urlImage;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }
            key = extras.getString("key");
            name = extras.getString("name");
            type = extras.getString("type");
            urlImage = extras.getString("urlImage");
            value = extras.getString("value");
            setTitle(name);
        }
        listView = (ListView) findViewById(R.id.list_news_details);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Nouvelle item = (Nouvelle) parent.getItemAtPosition(position);
                String url = item.getLink();
                if (URLUtil.isValidUrl(url)) {
                    Intent internetIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(internetIntent);
                } else {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.erreur_lien), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new NewsDetailAsyncTask().execute("https://api3.clubapplets.ca/news/list/" + key);
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

    private class NewsDetailAsyncTask extends AsyncTask<String, Void, ArrayList<Nouvelle>> {

        @Override
        protected ArrayList<Nouvelle> doInBackground(String... param) {
            List<Nouvelle> nouvelles = null;
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(param[0])
                        .get()
                        .build();

                Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    String jsonData = response.body().string();

                    nouvelles = new Gson().fromJson(jsonData, new TypeToken<List<Nouvelle>>() {
                    }.getType());
                    return new ArrayList<>(nouvelles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Nouvelle> list) {
            if (list != null) {
                //loadProgressBar.setVisibility(View.GONE);

                Collections.sort(list, new NewsComparator());

                NewsAdapter adapter = new NewsAdapter(NewsDetailsActivity.this, R.layout.row_news, list);
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(NewsDetailsActivity.this, getString(R.string.erreur_chargement), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(list);
        }
    }


}
