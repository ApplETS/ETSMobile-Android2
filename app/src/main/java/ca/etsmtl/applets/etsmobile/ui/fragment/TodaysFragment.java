package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.Locale;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.TodaysRequest;
import ca.etsmtl.applets.etsmobile.model.TodaysCourses;
import ca.etsmtl.applets.etsmobile.model.TodaysCourses.Seance;
import ca.etsmtl.applets.etsmobile.ui.adapter.SceanceAdapter;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public class TodaysFragment extends HttpFragment {


	ListView list;

	TextView todaysTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_today, container, false);
		super.onCreateView(inflater, v , savedInstanceState);
		list = (ListView) v.findViewById(R.id.todays_list) ;
		todaysTv = (TextView) v.findViewById(R.id.todays_name);
		return v;
	}

	@Override
	public void onRequestSuccess(Object parsedJson) {
		super.onRequestSuccess(parsedJson);
		if (parsedJson instanceof TodaysCourses) {
			TodaysCourses today = (TodaysCourses) parsedJson;
			ArrayList<Seance> s = today.horaire;
			list.setAdapter(new SceanceAdapter(getActivity(), s));
		}
	}

	@Override
	void updateUI() {
		String url = getActivity().getString(R.string.today_url_format);

		TodaysRequest request = new TodaysRequest(url, ApplicationManager.userCredentials);
        loadingView.showLoadingView();
		dataManager.sendRequest(request, this);

		DateTime dt = new DateTime();
		DateTime.Property pDoW = dt.dayOfWeek();
		DateTime.Property pMoY = dt.monthOfYear();
		
		todaysTv.setText(String.format("Horaire du %s le %d %s", pDoW.getAsText(Locale.FRENCH), pDoW.get(),pMoY.getAsText(Locale.FRENCH)));
	}
}
