package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.etsmtl.applets.etsmobile.http.AppletsApiEvenementsRequest;
import ca.etsmtl.applets.etsmobile.http.AppletsApiSourcesRequest;
import ca.etsmtl.applets.etsmobile.http.MyJackSpringAndroidSpiceService;
import ca.etsmtl.applets.etsmobile.model.NewsSource;
import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunaute;
import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunauteList;
import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenement;
import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenementList;
import ca.etsmtl.applets.etsmobile.ui.activity.NewsDetailsActivity;
import ca.etsmtl.applets.etsmobile.ui.activity.PrefsActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.EvenementCommunauteAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.NewsSourceAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.NewsSourceComparator;
import ca.etsmtl.applets.etsmobile.views.AnimatedExpandableListView;
import ca.etsmtl.applets.etsmobile2.R;


public class EventsFragment extends BaseFragment implements RequestListener<EvenementCommunauteList> {

    private AnimatedExpandableListView expandableListView;
    private SpiceManager spiceManager;
    private ArrayList<EvenementCommunaute> events = new ArrayList<>();
    private EvenementCommunauteAdapter expandableListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_events, container, false);

        expandableListView = (AnimatedExpandableListView) v.findViewById(R.id.expandable_list_view);

        expandableListAdapter = new EvenementCommunauteAdapter(getActivity(), events, expandableListView);
        expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    expandableListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        spiceManager = new SpiceManager(MyJackSpringAndroidSpiceService.class);


        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        spiceManager.start(getActivity());
        spiceManager.execute(new AppletsApiSourcesRequest(getActivity()), new RequestListener<SourceEvenementList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(getActivity(), getString(R.string.SupportKit_errorCouldNotConnect), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestSuccess(SourceEvenementList sourceEvenements) {
                for (SourceEvenement source : sourceEvenements) {
                    spiceManager.execute(new AppletsApiEvenementsRequest(getActivity(), source), EventsFragment.this);
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        spiceManager.cancelAllRequests();
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
        if (!(e instanceof RequestCancelledException)) {
            Toast.makeText(getActivity(), getString(R.string.SupportKit_errorCouldNotConnect), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestSuccess(EvenementCommunauteList events) {
        expandableListAdapter.addEvents(events);
    }

}

