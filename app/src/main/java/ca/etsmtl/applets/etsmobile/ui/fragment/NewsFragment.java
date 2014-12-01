package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.octo.android.robospice.persistence.exception.SpiceException;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

import ca.etsmtl.applets.etsmobile.http.AppletsApiNewsRequest;
import ca.etsmtl.applets.etsmobile.model.Nouvelles;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public class NewsFragment extends HttpFragment {

    private final long DAY_IN_MS = 1000 * 60 * 60 * 24;
    private ListView newsListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_news, container, false);

        newsListView = (ListView) v.findViewById(R.id.listView_news);

        Date currentDate = new Date();
        Date dateStart = new Date(currentDate.getTime() - (14 * DAY_IN_MS));

        String dateDebut = DateFormatUtils.format(dateStart, "yyyy-MM-dd");
        String dateFin = DateFormatUtils.format(currentDate, "yyyy-MM-dd");


        dataManager.sendRequest( new AppletsApiNewsRequest(getActivity(),"ets",dateDebut,dateFin), NewsFragment.this);

		return v;
	}
	
	@Override
	public void onRequestFailure(SpiceException e) {

	}

	@Override
	public void onRequestSuccess(Object o) {

        if(o instanceof Nouvelles) {
            Nouvelles nouvelles = (Nouvelles)o;

            Log.e("NEWSFRAGMENT","nombre d'articles : "+nouvelles.getNouvelles().size() );

        }

	}

	@Override
	void updateUI() {
	}
}
