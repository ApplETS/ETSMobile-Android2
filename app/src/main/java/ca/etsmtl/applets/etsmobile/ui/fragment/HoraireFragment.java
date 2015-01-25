package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
import ca.etsmtl.applets.etsmobile.http.AppletsApiCalendarRequest;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.ui.adapter.SeanceAdapter;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Thibaut on 30/08/14.
 */
public class HoraireFragment extends HttpFragment implements Observer {

    private HoraireManager horaireManager;
    private CustomProgressDialog customProgressDialog;
    private ListView horaireListView;
    private ArrayList<Seances> listSeances;
    private SeanceAdapter seanceAdapter;
    private DateTime dateTime = new DateTime();
    private DatabaseHelper databaseHelper;

    private  SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        new AsyncListViewLoader().execute();








        horaireListView = (ListView) v.findViewById(R.id.listView_horaire);

        horaireManager = new HoraireManager(this, getActivity());
        horaireManager.addObserver(this);


//
//        final HoraireFragment activity = this;
//
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground( Void... voids ) {
//
//                return null;
//            }
//        }.execute();

        customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner, "Synchronisation en cours");
        customProgressDialog.show();




        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);




        return v;
    }

    @Override
    public void onRequestFailure(SpiceException arg0) {
        customProgressDialog.dismiss();
        Toast.makeText(getActivity(), "La synchronisation a échoué.", Toast.LENGTH_SHORT).show();
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
                    dataManager.sendRequest(new AppletsApiCalendarRequest(getActivity(), dateStartString, dateEndString), HoraireFragment.this);
                    break;
                }
            }
        }

        horaireManager.onRequestSuccess(o);


    }

    @Override
    void updateUI() {


        dateTime = new DateTime();

        DateTime.Property pDoW = dateTime.dayOfWeek();
        DateTime.Property pDoM = dateTime.dayOfMonth();
        DateTime.Property pMoY = dateTime.monthOfYear();


        databaseHelper = new DatabaseHelper(getActivity());
        listSeances = new ArrayList<Seances>();

        seanceAdapter = new SeanceAdapter(getActivity(), listSeances);




        if (listSeances.isEmpty()) {
//            tvNoCourses.setVisibility(View.VISIBLE);
        } else {
//            for (Seances seances : listSeances) {
//                dataRowItems.add(new TodayDataRowItem(TodayDataRowItem.viewType.VIEW_TYPE_SEANCE, seances));
//            }
//            tvNoCourses.setVisibility(View.GONE);
        }
//        seanceAdapter = new SeanceAdapter(getActivity(), listSeances);

        horaireListView.setAdapter(seanceAdapter);



    }

    private class AsyncListViewLoader extends AsyncTask<String,Void,ArrayList<Seances>> {

        @Override
        protected void onPostExecute(ArrayList<Seances> result) {
            super.onPostExecute(result);

            seanceAdapter.setItemList(result);
            seanceAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Seances> doInBackground(String... params) {
            ArrayList<Seances> result = new ArrayList<Seances>();

            try {
                try {
                    Log.e("TEST",new DateTime().toString());

//                    SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH);
//                    result = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().where().like("dateDebut", seancesFormatter.format(dateTime.toDate()).toString() + "%").query();
                    result = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryForAll();

//                    result = (ArrayList<Seances>) databaseHelper.getDao(Seances.class).queryBuilder().query();
                    Log.e("TEST",new DateTime().toString());

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return result;
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }


    }

    @Override
    public void update(Observable observable, Object data) {

        customProgressDialog.dismiss();

        new AsyncTask<Object, Void, Object>() {
            private Exception exception = null;

            protected void onPreExecute() {
                customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner, "Mise à jour du calendrier en cours");
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
                    Toast.makeText(getActivity(), "Une erreur est survenue lors de la mise à jour du calendrier.", Toast.LENGTH_SHORT).show();
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


    }

}
