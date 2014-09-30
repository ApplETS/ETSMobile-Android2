package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodaySeancesAdapter;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Phil on 17/11/13.
 */
public class TodaysFragment extends HttpFragment implements Observer {


	private ListView list;
    private HoraireManager horaireManager;
	private TextView todaysTv;
    private DateTime dt;
    private DatabaseHelper db;
    private TodaySeancesAdapter todaySeancesAdapter;

    private  ArrayList<Seances> listSeances;

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

        horaireManager = new HoraireManager(this, getActivity());
        horaireManager.addObserver(this);


        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);

		return v;
	}


    @Override
    public void onRequestSuccess(Object o) {
        horaireManager.onRequestSuccess(o);

    }

//	@Override
//	public void onRequestSuccess(Object parsedJson) {
//		super.onRequestSuccess(parsedJson);
//		if (parsedJson instanceof TodaysCourses) {
//			TodaysCourses today = (TodaysCourses) parsedJson;
//			ArrayList<Seance> s = today.horaire;
//			list.setAdapter(new SceanceAdapter(getActivity(), s));
//		}
//	}

	@Override
	void updateUI() {
//		String url = getActivity().getString(R.string.today_url_format);
//		TodaysRequest request = new TodaysRequest(url, ApplicationManager.userCredentials);
//        loadingView.showLoadingView();

        dt = new DateTime();
        dt = dt.withDate(2014,04,28);
        DateTime.Property pDoW = dt.dayOfWeek();
        DateTime.Property pDoM = dt.dayOfMonth();
        DateTime.Property pMoY = dt.monthOfYear();

        todaysTv.setText(String.format("Horaire du %s le %d %s", pDoW.getAsText(Locale.FRENCH), pDoM.get(),pMoY.getAsText(Locale.FRENCH)));





        db = new DatabaseHelper(getActivity());
        listSeances = new ArrayList<Seances>();
        try {
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CANADA_FRENCH);
            Log.e("TEST",""+seancesFormatter.format(dt.toDate()).toString());
            listSeances = (ArrayList<Seances>) db.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dt.toDate()).toString() + "%").query();

            Seances seance = new Seances();
            seance.coursGroupe = "test";
            seance.dateDebut = "2014-04-08";
            seance.dateFin = "2014-04-08";
            seance.nomActivite ="courss";
            seance.local = "DTC";

            listSeances = new ArrayList<Seances>();
            listSeances.add(seance);

            Log.e("for","for");
            for(Seances s : listSeances)
            {
                Log.e(" ",s.coursGroupe);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        todaySeancesAdapter = new TodaySeancesAdapter(getActivity(),R.layout.row_today_courses, listSeances);
        list.setAdapter(todaySeancesAdapter);



	}

    @Override
    public void update(Observable observable, Object data) {
        Toast.makeText(getActivity(),"update",Toast.LENGTH_LONG).show();
        dt = new DateTime();
        dt = dt.withDate(2014,04,28);
        db = new DatabaseHelper(getActivity());
        try {
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CANADA_FRENCH);
            listSeances.clear();
            listSeances.addAll((ArrayList<Seances>) db.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dt.toDate()).toString() + "%").query());
            todaySeancesAdapter.notifyDataSetChanged();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
