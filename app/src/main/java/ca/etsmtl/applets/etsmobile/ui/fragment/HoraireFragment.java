package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.SeanceAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayDataRowItem;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.CourseDecorator;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Thibaut on 30/08/14.
 */
public class HoraireFragment extends HttpFragment implements Observer, OnDateSelectedListener {

    private HoraireManager horaireManager;
    private CustomProgressDialog customProgressDialog;

    private ArrayList<TodayDataRowItem> listSeances;
    private SeanceAdapter seanceAdapter;
    private DateTime dateTime = new DateTime();
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBarSyncHoraire;
    @Bind(R.id.calendarView)
    MaterialCalendarView mCalendarView;

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
        View v = inflater.inflate(R.layout.calendar_horraire_layout, container, false);
        ButterKnife.bind(this, v);

        databaseHelper = new DatabaseHelper(getActivity());

        seanceAdapter = new SeanceAdapter(getActivity());

        listSeances = new ArrayList<TodayDataRowItem>();

        fillSeancesList(dateTime.toDate());


        mCalendarView.setSelectedDate(new Date());
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.addDecorator(new CourseDecorator(R.color.SupportKit_accentLight, getCourseDate()));


        horaireManager = new HoraireManager(this, getActivity());
        horaireManager.addObserver(this);

        progressBarSyncHoraire = (ProgressBar) v.findViewById(R.id.progressBar_sync_horaire);
        progressBarSyncHoraire.setVisibility(ProgressBar.VISIBLE);

//        customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner, "Synchronisation en cours");
//        customProgressDialog.show();

        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);

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
            Date currentDate = new Date();
            Date dateStart;
            Date dateEnd;
            for (int i = listeDeSessions.liste.size() - 1; i > 0; i--) {
                dateStart = Utility.getDateFromString(listeDeSessions.liste.get(i).dateDebut);
                dateEnd = Utility.getDateFromString(listeDeSessions.liste.get(i).dateFin);
                if (currentDate.getTime() >= dateStart.getTime() && currentDate.getTime() <= dateEnd.getTime()) {
                    String dateStartString = Utility.getStringForApplETSApiFromDate(dateStart);
                    String dateEndString = Utility.getStringForApplETSApiFromDate(dateEnd);
                    //todo dataManager.sendRequest(new AppletsApiCalendarRequest(getActivity(), dateStartString, dateEndString), HoraireFragment.this);
                    break;
                }
            }
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
        try {
            seanceAdapter.setItemList((ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(date).toString() + "%").query());
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
    public Collection<CalendarDay> getCourseDate() {

        ArrayList<Seances> itemList = null;
        ArrayList<CalendarDay> dates = new ArrayList<>();
        try {
            itemList = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryForAll();
            for (Seances seances : itemList) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date seanceDay = formatter.parse(seances.dateDebut.substring(0, 10), new ParsePosition(0));
                Log.i("CourseList","Date:"+seances.dateDebut.substring(0, 10)+"  -   Cours: " + seances.libelleCours + "  -   NomACtivity: " + seances.nomActivite +"  -   DescriptionActivite: " + seances.descriptionActivite );
                dates.add(new CalendarDay(seanceDay));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dates;
    }

    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        widget.setSelectedDate(date);
        fillSeancesList(date.getDate());

        openCourseListDialog();
    }
}
