package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.Locale;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.TodaysRequest;
import ca.etsmtl.applets.etsmobile.model.TodaysCourses;
import ca.etsmtl.applets.etsmobile.model.TodaysCourses.Seance;
import ca.etsmtl.applets.etsmobile.ui.adapter.SceanceAdapter;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class TodaysFragment extends HttpFragment {

	@InjectView(R.id.todays_list)
	ListView list;

	@InjectView(R.id.todays_name)
	TextView todaysTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutId = R.layout.fragment_today;
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		progressBar.setVisibility(View.GONE);
		errorMessageTv.setVisibility(View.VISIBLE);
		errorMessageTv.setText(getString(R.string.error_JSON_PARSING));
	}

	@Override
	public void onRequestSuccess(Object parsedJson) {

		progressBar.setVisibility(View.GONE);

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
		dataManager.sendRequest(request, this);

		DateTime dt = new DateTime();
		DateTime.Property pDoW = dt.dayOfWeek();
		DateTime.Property pMoY = dt.monthOfYear();
		
		todaysTv.setText(String.format("Horaire du %s le %d %s", pDoW.getAsText(Locale.FRENCH), pDoW.get(),pMoY.getAsText(Locale.FRENCH)));
	}
}
