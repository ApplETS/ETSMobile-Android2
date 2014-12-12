package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.octo.android.robospice.persistence.exception.SpiceException;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;

import ca.etsmtl.applets.etsmobile.http.AppletsApiNewsRequest;
import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile.model.Nouvelles;
import ca.etsmtl.applets.etsmobile.ui.adapter.NewsAdapter;
import ca.etsmtl.applets.etsmobile2.R;


public class NewsFragment extends HttpFragment {

    private final long DAY_IN_MS = 1000 * 60 * 60 * 24;
    private ListView newsListView;
    private NewsAdapter adapter;
    private ArrayList<Nouvelle> nouvellesList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_news, container, false);

        newsListView = (ListView) v.findViewById(R.id.listView_news);

        Date currentDate = new Date();
        Date dateStart = new Date(currentDate.getTime() - (14 * DAY_IN_MS));

        String dateDebut = DateFormatUtils.format(dateStart, "yyyy-MM-dd");
        String dateFin = DateFormatUtils.format(currentDate, "yyyy-MM-dd");

        nouvellesList = new ArrayList<Nouvelle>();

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
            adapter = new NewsAdapter(getActivity(),R.layout.row_news, nouvelles,this);
            newsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

	}

	@Override
	void updateUI() {


	}
}
