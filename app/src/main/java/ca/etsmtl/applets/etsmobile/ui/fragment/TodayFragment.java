package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.activity.NotificationActivity;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayDataRowItem;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.SeanceComparator;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

public class TodayFragment extends HttpFragment implements Observer {

    private static final long JOUR_EN_MS =  86400000;
    private static final String TODAY_PREFS = "TodayPrefs";
    private static final String DATE_DEBUT_PREF = "DateDebutPref";
    private static final String DATE_FIN_PREF = "DateFinPref";

    private ListView todaysList;
    private HoraireManager horaireManager;
    private TextView todaysTv;
    private DateTime dateTime;
    private DatabaseHelper databaseHelper;
    private TextView tvNoCourses;
    private ArrayList<Seances> listSeances;
    private ArrayList<Event> events;
    private TodayAdapter adapter;
    private ProgressBar semesterProgressBar;
    private TextView semesterProgressBarText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_today, container, false);
        super.onCreateView(inflater, v, savedInstanceState);


        semesterProgressBar = (ProgressBar) v.findViewById(R.id.semester_progress_bar);
        semesterProgressBarText = (TextView) v.findViewById(R.id.semester_progress_bar_text);

        restoreSemesterProgressBarDatesFromPrefs();

        todaysList = (ListView) v.findViewById(R.id.todays_list);

        todaysTv = (TextView) v.findViewById(R.id.todays_name);
        tvNoCourses = (TextView) v.findViewById(R.id.tv_todays_no_courses);

        horaireManager = new HoraireManager(this, getActivity());
        horaireManager.addObserver(this);

        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_1_ajd);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_today, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_notifications:


                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);


                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestSuccess(Object o) {

        if (o instanceof ListeDeSessions) {

            ListeDeSessions listeDeSessions = (ListeDeSessions) o;
            Date currentDate = new Date();
            Date dateStart;
            Date dateEnd;
            for (int i = listeDeSessions.liste.size() - 1; i > 0; i-- ) {
                dateStart = Utility.getDateFromString(listeDeSessions.liste.get(i).dateDebut);
                dateEnd = Utility.getDateFromString(listeDeSessions.liste.get(i).dateFin);
                if (isAdded()) {
                    saveSemesterProgressBarDatesToPrefs(listeDeSessions.liste.get(i).dateDebut, listeDeSessions.liste.get(i).dateFin);
                    setSemesterProgressBarText(dateStart, dateEnd);
                }
                if (currentDate.getTime() >= dateStart.getTime() && currentDate.getTime() <= dateEnd.getTime()) {
                    String dateStartString = Utility.getStringForApplETSApiFromDate(dateStart);
                    String dateEndString = Utility.getStringForApplETSApiFromDate(dateEnd);

                    //todo dataManager.sendRequest(new AppletsApiCalendarRequest(getActivity(), dateStartString, dateEndString), TodayFragment.this);
                    break;
                }
            }
        } else {
            horaireManager.onRequestSuccess(o);
        }
    }

    private void saveSemesterProgressBarDatesToPrefs(String dateStartStr, String dateEndStr) {
        if (isAdded()) {
            SharedPreferences settings = getContext().getSharedPreferences(TODAY_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(DATE_DEBUT_PREF, dateStartStr);
            editor.putString(DATE_FIN_PREF, dateEndStr);
            editor.apply();
        }
    }

    private void restoreSemesterProgressBarDatesFromPrefs() {
        SharedPreferences settings = getContext().getSharedPreferences(TODAY_PREFS, Context.MODE_PRIVATE);
        String dateStartStr = settings.getString(DATE_DEBUT_PREF, null);
        String dateEndStr = settings.getString(DATE_FIN_PREF, null);
        if (dateStartStr != null && dateEndStr != null) {
            Date dateStart = Utility.getDateFromString(dateStartStr);
            Date dateEnd = Utility.getDateFromString(dateEndStr);

            if (dateStart != null && dateEnd != null)
                setSemesterProgressBarText(dateStart, dateEnd);
        }
    }

    private void setSemesterProgressBarText(Date dateDebut, Date dateFin) {
        Date dateActuelle = new Date();
        long dureeTotaleMs;
        long nbJoursTotal;
        long progressionMs;
        long progressionJour;

        if (dateActuelle.after(dateDebut) && dateActuelle.before(dateFin)) {
            dureeTotaleMs = dateFin.getTime() - dateDebut.getTime() + JOUR_EN_MS;
            nbJoursTotal = TimeUnit.MILLISECONDS.toDays(dureeTotaleMs);
            progressionMs = dateActuelle.getTime() - dateDebut.getTime();
            progressionJour = TimeUnit.MILLISECONDS.toDays(progressionMs);

            semesterProgressBar.setMax((int) nbJoursTotal);
            semesterProgressBar.setProgress((int) progressionJour);
            semesterProgressBarText.setText(getString(R.string.semester_progression)
                    + getString(R.string.deux_points) + String.valueOf(progressionJour) + "/"
                    + String.valueOf(nbJoursTotal) + " " + getString(R.string.days));
            semesterProgressBarText.setVisibility(View.VISIBLE);
        } else if (dateActuelle.before(dateDebut)) {
            progressionMs = dateDebut.getTime() - dateActuelle.getTime() + JOUR_EN_MS;
            progressionJour = TimeUnit.MILLISECONDS.toDays(progressionMs);

            semesterProgressBarText.setText(String.format(getString(R.string.days_before_session_start), String.valueOf(progressionJour)));
            semesterProgressBarText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestFailure(SpiceException e) {

    }

    @Override
    void updateUI() {
        if (isAdded()) {
            dateTime = new DateTime();

            DateTime.Property pDoW = dateTime.dayOfWeek();
            DateTime.Property pDoM = dateTime.dayOfMonth();
            DateTime.Property pMoY = dateTime.monthOfYear();

            todaysTv.setText(getActivity().getString(R.string.horaire, pDoW.getAsText(getResources().getConfiguration().locale), pDoM.get(), pMoY.getAsText(getResources().getConfiguration().locale)));
            databaseHelper = new DatabaseHelper(getActivity());
            listSeances = new ArrayList<Seances>();
            events = new ArrayList<Event>();
            try {
                SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                listSeances = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();
                Collections.sort(listSeances, new SeanceComparator());
                events = (ArrayList<Event>) databaseHelper.getDao(Event.class).queryBuilder().where().like("startDate", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();

            } catch (Exception e) {
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
    }

    @Override
    public void update(Observable observable, Object data) {
        dateTime = new DateTime();
        databaseHelper = new DatabaseHelper(getActivity());
        updateUI();
    }

}
