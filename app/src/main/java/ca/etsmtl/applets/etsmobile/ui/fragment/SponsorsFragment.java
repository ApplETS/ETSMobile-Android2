package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ca.etsmtl.applets.etsmobile.ui.adapter.SponsorAdapter;
import ca.etsmtl.applets.etsmobile.util.SponsorManager;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 18/11/13.
 * Modified by Steven
 */
public class SponsorsFragment extends HttpFragment implements Observer {

	private ListView sponsorListView;
	private GridView sponsorGridView;
	private SponsorAdapter adapter;
	private ArrayList<Sponsor> sponsorList;
	private DatabaseHelper databaseHelper = null;
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


		return v;
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
		Toast.makeText(getActivity(), "La synchronisation a échoué...", Toast.LENGTH_SHORT).show();
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
				Intent internetIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(url));
				startActivity(internetIntent);
			}
		});
		}

	@Override
	void updateUI() {
	}

	private boolean isNetworkAvailable(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNI = cm.getActiveNetworkInfo();
		return activeNI != null && activeNI.isConnected();
	}

	@Override
	public void update(Observable observable, Object o) {
		refreshList();
	}
}
