package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.AppletsApiSponsorRequest;
import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.ui.activity.MainActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.SponsorAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.SponsorManager;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

public class SponsorsFragment extends HttpFragment implements Observer {

    private GridView sponsorGridView;
    private SponsorAdapter adapter;
    private ArrayList<Sponsor> sponsorList;
    private SponsorManager mSponsorManager;
    private LoadingView loadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_sponsors, container, false);


        loadingView = (LoadingView) v.findViewById(R.id.loadingView_sponsor);
        sponsorGridView = (GridView) v.findViewById(R.id.gridView_sponsor);
        sponsorList = new ArrayList<Sponsor>();
        mSponsorManager = new SponsorManager(getActivity());
        mSponsorManager.addObserver(this);
        loadingView.showLoadingView();
        refreshList();
        dataManager.sendRequest(new AppletsApiSponsorRequest(getActivity()), this);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_3_sponsors);
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingView.hideLoadingView(loadingView);
                }
            });
        }
        Toast.makeText(getActivity(), getString(R.string.toast_Sync_Fail), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(Object o) {
        super.onRequestSuccess(o);
        mSponsorManager.onRequestSuccess(o);
        if (loadingView != null && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingView.hideLoadingView(loadingView);
                }
            });
        }
    }

    public void refreshList() {

        sponsorList = mSponsorManager.getSponsorList();
        adapter = new SponsorAdapter(getActivity(), R.layout.row_sponsor, sponsorList, this);
        sponsorGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        sponsorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Sponsor item = sponsorList.get(position);
                String url = item.getUrl();
                if (URLUtil.isValidUrl(url)) {
                    Intent internetIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(internetIntent);
                }
            }
        });
    }

    @Override
    void updateUI() {
    }

    @Override
    public void update(Observable observable, Object o) {
        refreshList();
    }
}
