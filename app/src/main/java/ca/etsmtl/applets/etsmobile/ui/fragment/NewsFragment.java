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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
import ca.etsmtl.applets.etsmobile2.R;


public class NewsFragment extends HttpFragment {

    private static String TAG = NewsFragment.class.getName();
    private final long DAY_IN_MS = 1000 * 60 * 60 * 24;
    private ListView newsListView;
    private NewsAdapter adapter;
    private ArrayList<Nouvelle> nouvellesList;

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

        /*Date currentDate = new Date();
        Date dateStart = new Date(currentDate.getTime() - (14 * DAY_IN_MS));

        String dateDebut = DateFormatUtils.format(dateStart, "yyyy-MM-dd");
        String dateFin = DateFormatUtils.format(currentDate, "yyyy-MM-dd");

        nouvellesList = new ArrayList<Nouvelle>();


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> selection = sharedPrefs.getStringSet("multi_pref", null);

        String[] selectedItems = {"ets"};

        if(selection != null) {
            selectedItems = selection.toArray(new String[] {});
        }


        String sources = "";

        for(int i = 0 ; i < selectedItems.length; i++) {
            sources += selectedItems[i] + (i == (selectedItems.length-1)?"":",");
        }*/

        //dataManager.sendRequest( new AppletsApiNewsRequest(getActivity(),sources,dateDebut,dateFin), NewsFragment.this);

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

        if(o instanceof Nouvelles) {

            Nouvelles nouvelles = (Nouvelles) o;

            Collections.sort(nouvelles, new Comparator<Nouvelle>() {
                String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
                DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);

                @Override
                public int compare(Nouvelle nouvelle1, Nouvelle nouvelle2) {

                    String updatetime = nouvelle1.getUpdated_time();

                    DateTime date1 = dtf.parseDateTime(nouvelle1.getUpdated_time());

                    //DateTime date2 = dateTimeFormatter.parseDateTime(nouvelle2.getUpdated_time());
                    DateTime date2 = dtf.parseDateTime(nouvelle2.getUpdated_time());

                    if (date1.isAfter(date2)) {
                        return -1;
                    } else if (date1.isBefore(date2)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });



            adapter = new NewsAdapter(getActivity(),R.layout.row_news, nouvelles,this);
            newsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Nouvelle nouvelle = (Nouvelle) parent.getItemAtPosition(position);
                    Intent i = new Intent(getActivity(), NewsDetailsActivity.class);

                    i.putExtra("from", nouvelle.getFrom());
                    i.putExtra("image", nouvelle.getImage());
                    i.putExtra("title", nouvelle.getTitle());
                    i.putExtra("created_time", nouvelle.getCreated_time());
                    i.putExtra("facebook_link", nouvelle.getFacebook_link());
                    i.putExtra("updated_time", nouvelle.getUpdated_time());
                    i.putExtra("message", nouvelle.getMessage());
                    i.putExtra("id", nouvelle.getId());
                    i.putExtra("icon_link", nouvelle.getIcon_link());

                    getActivity().startActivity(i);

                }
            });
        }

	}

	@Override
	void updateUI() {
	}

    private class NewsSourceAsyncTask extends AsyncTask<String, Void, ArrayList<NewsSource>> {

        @Override
        protected ArrayList<NewsSource> doInBackground(String... param) {
            ArrayList<NewsSource> newsSourceList;
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(param[0])
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();
                Response response = client.newCall(request).execute();
                if(response.code() == 200) {
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    newsSourceList = new ArrayList<>();
                    Object source = Jobject.get("source");
                    if(source instanceof JSONArray){
                        JSONArray consommationsArray = (JSONArray) source;

                        for (int i = 0; i < consommationsArray.length(); i++) {
                            JSONObject object = consommationsArray.getJSONObject(i);
                            NewsSource newsSource = new NewsSource(object);
                            newsSourceList.add(newsSource);
                        }
                    }else{
                        JSONObject object = (JSONObject) source;
                        NewsSource newsSource = new NewsSource(object);
                        newsSourceList.add(newsSource);
                    }
                }else{
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return newsSourceList;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsSource> list) {
            if(list !=null) {
                //loadProgressBar.setVisibility(View.GONE);
                NewsSourceAdapter adapter = new NewsSourceAdapter(getActivity(), R.layout.row_news_source, list);
                newsListView.setAdapter(adapter);
            }else{
                Toast.makeText(getActivity(), "Problem loading news", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(list);
        }
    }
}

