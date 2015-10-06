package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.http.AppletsApiSponsorRequest;
import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.model.SponsorList;
import ca.etsmtl.applets.etsmobile.ui.adapter.SponsorAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 18/11/13.
 * Modified by Steven
 */
public class SponsorsFragment extends HttpFragment {

	private ListView sponsorListView;
	private SponsorAdapter adapter;
	private ArrayList<Sponsor> sponsorList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_sponsors, container, false);
		sponsorListView = (ListView) v.findViewById(R.id.listView_sponsor);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		sponsorList = new ArrayList<Sponsor>();

		dataManager.sendRequest(new AppletsApiSponsorRequest(getActivity()), SponsorsFragment.this);
	}

	@Override
	public void onRequestFailure(SpiceException e) {
	}

	@Override
	public void onRequestSuccess(Object o) {

		if (o instanceof SponsorList) {

			SponsorList sponsorList = (SponsorList) o;

			adapter = new SponsorAdapter(getActivity(), R.layout.row_sponsor, sponsorList, this);
			sponsorListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();

		}
	}

	@Override
	void updateUI() {

	}
}
