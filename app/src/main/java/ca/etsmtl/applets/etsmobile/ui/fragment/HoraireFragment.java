package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.octo.android.robospice.persistence.exception.SpiceException;

import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.AppletsApiCalendarRequest;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.SeanceAdapter;
import ca.etsmtl.applets.etsmobile.ui.adapter.TodayDataRowItem;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;
import ca.etsmtl.applets.etsmobile.views.LoadingView;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Thibaut on 30/08/14.
 */
public class HoraireFragment extends HttpFragment implements Observer {

    private HoraireManager horaireManager;
    private CustomProgressDialog customProgressDialog;
    private ListView horaireListView;
    private ArrayList<TodayDataRowItem> listSeances;
    private SeanceAdapter seanceAdapter;
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBarSyncHoraire;

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
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        databaseHelper = new DatabaseHelper(getActivity());

        horaireListView = (ListView) v.findViewById(R.id.listView_horaire);
        horaireListView.setEmptyView(v.findViewById(R.id.txt_empty_calendar));

        seanceAdapter = new SeanceAdapter(getActivity());
        horaireListView.setAdapter(seanceAdapter);

        try{
            seanceAdapter.setItemList((ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryForAll(), (ArrayList<Event>) databaseHelper.getDao(Event.class).queryForAll());
            seanceAdapter.notifyDataSetChanged();
        } catch(SQLException e) {
            e.printStackTrace();
        }

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
    public void onRequestFailure(SpiceException arg0) {
        progressBarSyncHoraire.setVisibility(ProgressBar.GONE);
//        customProgressDialog.dismiss();
        if(getActivity() != null)
            Toast.makeText(getActivity(), getString(R.string.toast_Sync_Fail), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(final Object o) {

        if (o instanceof ListeDeSessions) {

            ListeDeSessions listeDeSessions = (ListeDeSessions) o;
            DateTime dateDebut = new DateTime();
            DateTime dateEnd = new DateTime();

            for (int i = 0; i < listeDeSessions.liste.size() - 1; i++) {
                dateEnd = new DateTime(listeDeSessions.liste.get(i).dateFin);

                if(dateDebut.isBefore(dateEnd.plusDays(1))) {
                    break;
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateToday = formatter.format(dateDebut.toDate());
            String dateSessionEnd = formatter.format(dateEnd.toDate());
            dataManager.sendRequest(new AppletsApiCalendarRequest(getActivity(), dateToday, dateSessionEnd), this);
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

        try{
            seanceAdapter.setItemList((ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryForAll(), (ArrayList<Event>) databaseHelper.getDao(Event.class).queryForAll());
            seanceAdapter.notifyDataSetChanged();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}