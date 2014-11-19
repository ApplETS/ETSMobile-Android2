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
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.AppletsApiRequest;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayDataRowItem;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

public class TodayFragment extends HttpFragment implements Observer {

    private ListView todaysList;
    private HoraireManager horaireManager;
    private TextView todaysTv;
    private DateTime dateTime;
    private DatabaseHelper databaseHelper;
    private TextView tvNoCourses;
    private ArrayList<Seances> listSeances;
    private ArrayList<Event> events;
    private TodayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_today, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
        todaysList = (ListView) v.findViewById(R.id.todays_list);

        todaysTv = (TextView) v.findViewById(R.id.todays_name);
        tvNoCourses = (TextView) v.findViewById(R.id.tv_todays_no_courses);

        horaireManager = new HoraireManager(this, getActivity());
        horaireManager.addObserver(this);

        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        return v;
    }

    @Override
    public void onRequestSuccess(Object o) {

        if (o instanceof ListeDeSessions) {

            ListeDeSessions listeDeSessions = (ListeDeSessions) o;
            Date currentDate = new Date();
            Date dateStart;
            Date dateEnd;
            for (int i = listeDeSessions.liste.size() - 1; i > 0; i--) {
                dateStart = Utility.getDateFromString(listeDeSessions.liste.get(i).dateDebut);
                dateEnd = Utility.getDateFromString(listeDeSessions.liste.get(i).dateFin);
                if (currentDate.getTime() >= dateStart.getTime() && currentDate.getTime() <= dateEnd.getTime()) {
                    String dateStartString = Utility.getStringForApplETSApiFromDate(dateStart);
                    String dateEndString = Utility.getStringForApplETSApiFromDate(dateEnd);
                    dataManager.sendRequest(new AppletsApiRequest(getActivity(), dateStartString, dateEndString), TodayFragment.this);
                    break;
                }
            }
        } else {
            horaireManager.onRequestSuccess(o);
        }
    }

    @Override
    public void onRequestFailure(SpiceException e) {
    }

    @Override
    void updateUI() {

        dateTime = new DateTime();

        DateTime.Property pDoW = dateTime.dayOfWeek();
        DateTime.Property pDoM = dateTime.dayOfMonth();
        DateTime.Property pMoY = dateTime.monthOfYear();

        todaysTv.setText(getActivity().getString(R.string.horaire, pDoW.getAsText(Locale.FRENCH), pDoM.get(), pMoY.getAsText(Locale.FRENCH)));
        databaseHelper = new DatabaseHelper(getActivity());
        listSeances = new ArrayList<Seances>();
        events = new ArrayList<Event>();
        try {
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH);
            listSeances = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();
            events = (ArrayList<Event>) databaseHelper.getDao(Event.class).queryBuilder().where().like("startDate", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<TodayDataRowItem> dataRowItems = new ArrayList<TodayDataRowItem>();
        if (!events.isEmpty()) {
            dataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_TITLE_EVENT));
            for (Event event : events) {
                dataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_EVENT, event));
            }
        }

        dataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_TITLE_SEANCE));
        if (listSeances.isEmpty()) {
            tvNoCourses.setVisibility(View.VISIBLE);
        } else {
            for (Seances seances : listSeances) {
                dataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_SEANCE, seances));
            }
            tvNoCourses.setVisibility(View.GONE);
        }
        adapter = new TodayAdapter(getActivity(), dataRowItems);
        todaysList.setAdapter(adapter);
    }

    @Override
    public void update(Observable observable, Object data) {
        dateTime = new DateTime();
        databaseHelper = new DatabaseHelper(getActivity());
        updateUI();
    }

}
