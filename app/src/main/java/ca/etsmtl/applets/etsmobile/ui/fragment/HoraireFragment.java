package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.AppletsApiCalendarRequest;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.ui.adapter.SeanceAdapter;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.CourseDecorator;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.EventDecorator;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.FinalExamDecorator;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Thibaut on 30/08/14.
 */
public class HoraireFragment extends HttpFragment implements Observer, OnDateSelectedListener {

    private HoraireManager horaireManager;
    private CustomProgressDialog customProgressDialog;
    private DateTime dateTime = new DateTime();
    private SeanceAdapter seanceAdapter;
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBarSyncHoraire;
    @Bind(R.id.calendarView)
    MaterialCalendarView mCalendarView;

    private ArrayList<CalendarDay> courseDays;
    private ArrayList<CalendarDay> eventDays;
    private ArrayList<CalendarDay> finalExamDays;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_horaire, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_save_in_calendar:

                new AsyncTask<Object, Void, Object>() {
                    private Exception exception = null;

                    protected void onPreExecute() {
                        customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner, getString(R.string.dialog_Updating_Calendar));
                        customProgressDialog.show();
                    }

                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            horaireManager.updateCalendar();
                        } catch (Exception e) {
                            exception = e;
                        }
                        return null;
                    }

                    protected void onPostExecute(Object result) {

                        customProgressDialog.dismiss();
                        if (exception != null) {
                            Toast.makeText(getActivity(), getString(R.string.toast_Calendar_Update_Error), Toast.LENGTH_SHORT).show();
                        } else {

                            //Launch native calendar app
                            long startMillis = java.lang.System.currentTimeMillis();
                            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                            builder.appendPath("time");
                            ContentUris.appendId(builder, startMillis);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(builder.build());

                            startActivity(intent);

                        }
                    }

                }.execute();


                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_horaire_layout, container, false);
        ButterKnife.bind(this, v);

        databaseHelper = new DatabaseHelper(getActivity());

        seanceAdapter = new SeanceAdapter(getActivity());

        fillSeancesList(dateTime.toDate());
        setEventsList();

        mCalendarView.setSelectedDate(new Date());
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.addDecorators(
                new CourseDecorator( courseDays, ContextCompat.getColor(getActivity(),R.color.ets_red_darker)),
                new FinalExamDecorator(getActivity(), finalExamDays),
                new EventDecorator(getActivity(), eventDays));


        horaireManager = new HoraireManager(this, getActivity());
        horaireManager.addObserver(this);

        progressBarSyncHoraire = (ProgressBar) v.findViewById(R.id.progressBar_sync_horaire);
        progressBarSyncHoraire.setVisibility(ProgressBar.VISIBLE);

//        customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner, "Synchronisation en cours");
//        customProgressDialog.show();

        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        // @TODO Eventually, we might want to make the call for ETS Events here instead of in the onRequestSuccess.
        // The problem right now is getting the endDate without using the ListeDeSessions
        /*String dateStart = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dataManager.sendRequest(new AppletsApiCalendarRequest(getActivity(), dateStart, "2016-04-30"), this);*/

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        openCourseListDialog();
        return v;

    }

    @Override
    public void onRequestFailure(SpiceException arg0) {
        progressBarSyncHoraire.setVisibility(ProgressBar.GONE);
//        customProgressDialog.dismiss();
        if (getActivity() != null)
            Toast.makeText(getActivity(), getString(R.string.toast_Sync_Fail), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(final Object o) {

        if (o instanceof ListeDeSessions) {

            ListeDeSessions listeDeSessions = (ListeDeSessions) o;

            DateTime dateDebut = new DateTime();
            DateTime dateEnd = new DateTime();
            Trimestre trimestre;

            for (int i = 0; i < listeDeSessions.liste.size() - 1; i++) {
                trimestre = listeDeSessions.liste.get(i);
                dateEnd = new DateTime(trimestre.dateFin);

                if (dateDebut.isBefore(dateEnd.plusDays(1))) {
                    dateDebut = new DateTime(trimestre.dateDebut);
                    break;
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateSessionStart = formatter.format(dateDebut.toDate());
            String dateSessionEnd = formatter.format(dateEnd.toDate());
            dataManager.sendRequest(new AppletsApiCalendarRequest(getActivity(), dateSessionStart, dateSessionEnd), this);
        }

        horaireManager.onRequestSuccess(o);
    }

    @Override
    void updateUI() {

    }

    @Override
    public void update(Observable observable, Object data) {

//        customProgressDialog.dismiss();
        progressBarSyncHoraire.setVisibility(ProgressBar.GONE);

        fillSeancesList(dateTime.toDate());
    }

    public void fillSeancesList(Date date) {
        SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
        String today = seancesFormatter.format(date).toString();

        try {
            List<Seances> seances = databaseHelper.getDao(Seances.class)
                    .queryBuilder()
                    .where()
                    .like("dateDebut", today + "%")
                    .query();
            List<Event> events = databaseHelper.getDao(Event.class)
                    .queryBuilder()
                    .where()
                    .like("startDate", today + "%")
                    .query();
            seanceAdapter.setItemList(seances, events);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        seanceAdapter.notifyDataSetChanged();
    }

    public void openCourseListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.dialogStyle);
        if (seanceAdapter.getCount() > 0) {
            builder.setAdapter(seanceAdapter, null);
            builder.setTitle(R.string.today_course);
        } else {
            builder.setTitle(R.string.empty_calendar);
        }
        builder.setNeutralButton(R.string.drawer_close, null);
        builder.create().show();
    }

    public void setEventsList() {

        courseDays = new ArrayList<>();
        finalExamDays = new ArrayList<>();
        eventDays = new ArrayList<>();

        try {
            ArrayList<Seances> seancesList = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryForAll();
            for (Seances seance : seancesList) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date seanceDay = formatter.parse(seance.dateDebut.substring(0, 10), new ParsePosition(0));
                if (seance.descriptionActivite.contains("final"))
                    finalExamDays.add(new CalendarDay(seanceDay));
                else
                    courseDays.add(new CalendarDay(seanceDay));
            }

            ArrayList<Event> eventList = (ArrayList<Event>) databaseHelper.getDao(Event.class).queryForAll();
            for (Event event : eventList) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date eventDay = formatter.parse(event.getDateDebut().substring(0, 10), new ParsePosition(0));
                eventDays.add(new CalendarDay(eventDay));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        widget.setSelectedDate(date);
        fillSeancesList(date.getDate());

        openCourseListDialog();
    }
}
