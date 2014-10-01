package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;

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
public class TodayFragment extends HttpFragment implements Observer {

	private ListView list;
    private HoraireManager horaireManager;
	private TextView todaysTv;
    private DateTime dateTime;
    private DatabaseHelper databaseHelper;
    private TodaySeancesAdapter todaySeancesAdapter;
    private TextView tvNoCourses;
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
        tvNoCourses = (TextView) v.findViewById(R.id.tv_todays_no_courses);

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

    @Override
    public void onRequestFailure(SpiceException e) {}

	@Override
	void updateUI() {

        dateTime = new DateTime();

        DateTime.Property pDoW = dateTime.dayOfWeek();
        DateTime.Property pDoM = dateTime.dayOfMonth();
        DateTime.Property pMoY = dateTime.monthOfYear();

        todaysTv.setText(String.format("Horaire du %s %d %s", pDoW.getAsText(Locale.FRENCH), pDoM.get(),pMoY.getAsText(Locale.FRENCH)));

        databaseHelper = new DatabaseHelper(getActivity());
        listSeances = new ArrayList<Seances>();
        try {
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CANADA_FRENCH);

            listSeances = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        todaySeancesAdapter = new TodaySeancesAdapter(getActivity(),R.layout.row_today_courses, listSeances);
        list.setAdapter(todaySeancesAdapter);

        if(listSeances.isEmpty()) {
            tvNoCourses.setVisibility(View.VISIBLE);
        } else {
            tvNoCourses.setVisibility(View.GONE);
        }

	}

    @Override
    public void update(Observable observable, Object data) {
        dateTime = new DateTime();
        databaseHelper = new DatabaseHelper(getActivity());
        try {
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.CANADA_FRENCH);
            listSeances.clear();
            listSeances.addAll((ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query());
            todaySeancesAdapter.notifyDataSetChanged();

            if(listSeances.isEmpty()) {
                tvNoCourses.setVisibility(View.VISIBLE);
            } else {
                tvNoCourses.setVisibility(View.GONE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
