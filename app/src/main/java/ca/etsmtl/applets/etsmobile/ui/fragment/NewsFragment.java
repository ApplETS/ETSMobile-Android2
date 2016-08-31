package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ca.etsmtl.applets.etsmobile.http.AppletsApiNewsRequest;
import ca.etsmtl.applets.etsmobile.model.ConsommationBandePassante;
import ca.etsmtl.applets.etsmobile.model.NewsSource;
import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile.model.Nouvelles;
import ca.etsmtl.applets.etsmobile.ui.activity.NewsDetailsActivity;
import ca.etsmtl.applets.etsmobile.ui.activity.PrefsActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.NewsAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.NewsSourceAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.NewsSourceComparator;
import ca.etsmtl.applets.etsmobile2.R;


public class NewsFragment extends HttpFragment {

    private ListView newsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sélection des sources
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_news, container, false);

        newsListView = (ListView) v.findViewById(R.id.listView_news);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NewsSource nouvelle = (NewsSource) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), NewsDetailsActivity.class);

                i.putExtra("key", nouvelle.getKey());
                i.putExtra("name", nouvelle.getName());
                i.putExtra("type", nouvelle.getType());
                i.putExtra("urlImage", nouvelle.getUrlImage());
                i.putExtra("value", nouvelle.getValue());

                getActivity().startActivity(i);

            }
        });

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new NewsSourceAsyncTask().execute("https://api3.clubapplets.ca/news/sources");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Sélection des sources
        inflater.inflate(R.menu.menu_news, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Sélection des sources
        switch (item.getItemId()) {
            case R.id.menu_item_sources_news:

                // Display the fragment as the main content.
                Intent i = new Intent(getActivity(), PrefsActivity.class);

                getActivity().startActivity(i);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestFailure(SpiceException e) {
    }

    @Override
    public void onRequestSuccess(Object o) {
    }

    @Override
    void updateUI() {
    }

    private class NewsSourceAsyncTask extends AsyncTask<String, Void, ArrayList<NewsSource>> {

        @Override
        protected ArrayList<NewsSource> doInBackground(String... param) {
            List<NewsSource> sources = null;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(param[0])
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    String jsonData = response.body().string();

                    sources = new Gson().fromJson(jsonData, new TypeToken<List<NewsSource>>() {
                    }.getType());

                }
                return new ArrayList<>(sources);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsSource> list) {
            if (list != null) {
                Collections.sort(list,new NewsSourceComparator());
                //loadProgressBar.setVisibility(View.GONE);
                NewsSourceAdapter adapter = new NewsSourceAdapter(getActivity(), R.layout.row_news_source, list);
                newsListView.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), getString(R.string.erreur_chargement), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(list);
        }
    }
}

