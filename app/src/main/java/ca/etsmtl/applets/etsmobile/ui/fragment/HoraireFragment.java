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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import ca.etsmtl.applets.etsmobile.model.TodaysCourses;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.ui.adapter.SeanceAdapter;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.CourseDecorator;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.CourseTodayDecorator;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.EventDecorator;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.FinalExamDecorator;
import ca.etsmtl.applets.etsmobile.ui.calendar_decorator.TodayDecorator;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.TrimestreComparator;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;
import ca.etsmtl.applets.etsmobile2.R;

public class HoraireFragment extends HttpFragment implements Observer, OnDateSelectedListener {

    public static final String TAG = "HoraireFragment";
    private HoraireManager horaireManager;
    private CustomProgressDialog customProgressDialog;
    private DateTime dateTime = new DateTime();
    private SeanceAdapter seanceAdapter;//Seances d'une journee
    private SeanceAdapter allseanceAdapter;//Seances du semestre
    private SeanceAdapter upcomingseanceAdapter;//Seances du semestre
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBarSyncHoraire;
    @Bind(R.id.calendarView)
    MaterialCalendarView mCalendarView;
    @Bind(R.id.horraireViewSwitcher)
    ViewSwitcher horraireViewSwitcher;
    @Bind(R.id.calendar_listview)
    ListView calendar_listview;
    private ArrayList<CalendarDay> courseDays;
    private ArrayList<CalendarDay> eventDays;
    private ArrayList<CalendarDay> finalExamDays;
    private  boolean listDisplay = true;

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
            case R.id.calendar_display_toggle:
                if(listDisplay){

                    item.setIcon(R.drawable.list_icon);
                    listDisplay = false;
                }else{
                    item.setIcon(R.drawable.icon_calendar);
                    listDisplay = true;
                }
                horraireViewSwitcher.showNext();
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
        allseanceAdapter = new SeanceAdapter(getActivity());
        upcomingseanceAdapter = new SeanceAdapter(getActivity());

        fillSeancesList(dateTime.toDate());
        fillListView();
        setDaysList();

        calendar_listview.setAdapter(upcomingseanceAdapter);

        mCalendarView.setCurrentDate(new Date());
        mCalendarView.setSelectedDate(new Date());
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.addDecorators(
                new CourseDecorator(getActivity(),courseDays),
                new FinalExamDecorator(getActivity(),finalExamDays),
                new EventDecorator(eventDays,  ContextCompat.getColor(getActivity(),R.color.black)),
                new TodayDecorator(getActivity()),
                new CourseTodayDecorator(getActivity(),courseDays)
                );


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

        return v;

    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.menu_section_1_horaire);
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

        if (o instanceof ListeDeSessions && !((ListeDeSessions) o).liste.isEmpty()) {

            ListeDeSessions listeDeSessions = (ListeDeSessions) o;

            Trimestre derniereSession = Collections.max(listeDeSessions.liste, new TrimestreComparator());

            DateTime dateDebut = new DateTime(derniereSession.dateDebut);

            if(DateTime.now().isBefore(dateDebut)) {
                dateDebut = DateTime.now();
            }

            DateTime dateEnd = new DateTime(derniereSession.dateFin);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateDebutFormatted = formatter.format(dateDebut.toDate());
            String dateFinFormatted = formatter.format(dateEnd.toDate());
            dataManager.sendRequest(
                    new AppletsApiCalendarRequest(getActivity(),
                            dateDebutFormatted,
                            dateFinFormatted
                    ),
                    this);
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

    public void fillListView(){

        try {
            List<Seances> seances = databaseHelper.getDao(Seances.class).queryForAll();
            List<Event> events = databaseHelper.getDao(Event.class).queryForAll();
            allseanceAdapter.setItemList(seances, events);


            List<Seances> upcomingSeances = new ArrayList<>();
            List<Event> upcomingEvents = new ArrayList<>();

            DateTime now = new DateTime();
            for(Seances sc : seances){
                DateTime scDate = DateTime.parse(sc.getDateDebut());
                if( DateTimeComparator.getDateOnlyInstance().compare(now, scDate) <= 0 ){
                    upcomingSeances.add(sc);
                }
            }
            for(Event ev : events){
                DateTime evDate = DateTime.parse(ev.getDateDebut());
                if( DateTimeComparator.getDateOnlyInstance().compare(now, evDate) <= 0 ){
                    upcomingEvents.add(ev);
                }
            }

            upcomingseanceAdapter.setItemList(upcomingSeances,upcomingEvents);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        allseanceAdapter.notifyDataSetChanged();
        upcomingseanceAdapter.notifyDataSetChanged();
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

    public void setDaysList() {

        courseDays = new ArrayList<>();
        finalExamDays = new ArrayList<>();
        eventDays = new ArrayList<>();

        try {
            ArrayList<Seances> seancesList = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryForAll();
            for (Seances seance : seancesList) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date seanceDay = formatter.parse(seance.dateDebut.substring(0, 10), new ParsePosition(0));
                if (seance.descriptionActivite.contains("final"))
                    finalExamDays.add(CalendarDay.from(seanceDay));
                else
                    courseDays.add(CalendarDay.from(seanceDay));
            }
            ArrayList<Event> eventList = (ArrayList<Event>) databaseHelper.getDao(Event.class).queryForAll();
            for (Event event : eventList) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date eventDay = formatter.parse(event.getDateDebut().substring(0, 10), new ParsePosition(0));
                eventDays.add(CalendarDay.from(eventDay));
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
