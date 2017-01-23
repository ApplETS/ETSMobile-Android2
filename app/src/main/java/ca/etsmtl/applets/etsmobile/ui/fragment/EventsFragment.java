package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.http.AppletsApiEvenementsRequest;
import ca.etsmtl.applets.etsmobile.http.AppletsApiSourcesRequest;
import ca.etsmtl.applets.etsmobile.http.MyJackSpringAndroidSpiceService;
import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunaute;
import ca.etsmtl.applets.etsmobile.model.applets_events.EvenementCommunauteList;
import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenement;
import ca.etsmtl.applets.etsmobile.model.applets_events.SourceEvenementList;
import ca.etsmtl.applets.etsmobile.ui.adapter.EvenementCommunauteAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.views.AnimatedExpandableListView;
import ca.etsmtl.applets.etsmobile2.R;


public class EventsFragment extends BaseFragment implements RequestListener<EvenementCommunauteList> {

    private AnimatedExpandableListView expandableListView;
    private SpiceManager spiceManager = new SpiceManager(MyJackSpringAndroidSpiceService.class);
    private ArrayList<EvenementCommunaute> events = new ArrayList<>();
    private EvenementCommunauteAdapter expandableListAdapter;
    private ProgressBar progressBar;
    private int nbSources = 0;
    private int countSourcesLoaded = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_events, container, false);

        expandableListView = (AnimatedExpandableListView) v.findViewById(R.id.expandable_list_view);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
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

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        expandableListAdapter.clearEvents();
        progressBar.setVisibility(View.VISIBLE);
        countSourcesLoaded = 0;
        nbSources = 0;
        progressBar.setProgress(0);

        AppletsApiSourcesRequest requestSources = new AppletsApiSourcesRequest(getActivity());
        String cacheKey = requestSources.createCacheKey();
        spiceManager.execute(requestSources, cacheKey, DurationInMillis.ONE_MINUTE * 10, new RequestListener<SourceEvenementList>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.SupportKit_errorCouldNotConnect), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestSuccess(SourceEvenementList sourceEvenements) {
                progressBar.setMax(sourceEvenements.size());
                EventsFragment.this.nbSources = sourceEvenements.size();

                for (SourceEvenement source : sourceEvenements) {
                    AppletsApiEvenementsRequest requestEvents = new AppletsApiEvenementsRequest(getActivity(), source);
                    String cacheKey = requestEvents.createCacheKey();
                    spiceManager.execute(requestEvents, cacheKey, DurationInMillis.ONE_MINUTE * 10, EventsFragment.this);
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        if (!(e instanceof RequestCancelledException)) {
            Toast.makeText(getActivity(), getString(R.string.SupportKit_errorCouldNotConnect), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestSuccess(EvenementCommunauteList events) {

        expandableListAdapter.addEvents(events);
        progressBar.setProgress(countSourcesLoaded++);
        if (countSourcesLoaded >= nbSources) {
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(0);
        }
    }

    @Override
    public String getFragmentTitle() {
        return "EventsFragment";
    }
}

