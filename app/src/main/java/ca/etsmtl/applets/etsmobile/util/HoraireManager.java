package ca.etsmtl.applets.etsmobile.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Observable;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.EventList;
import ca.etsmtl.applets.etsmobile.model.HoraireActivite;
import ca.etsmtl.applets.etsmobile.model.HoraireExamenFinal;
import ca.etsmtl.applets.etsmobile.model.JoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.model.listeDesActivitesEtProf;
import ca.etsmtl.applets.etsmobile.model.listeHoraireExamensFinaux;
import ca.etsmtl.applets.etsmobile.model.listeJoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.listeSeances;

public class HoraireManager extends Observable implements RequestListener<Object> {

    private Activity activity;
    private boolean syncSeancesEnded = false;
    private boolean syncJoursRemplacesEnded = false;
    private boolean syncEventListEnded = false;

    private String calendarName = "Mes cours";


    public HoraireManager(final RequestListener<Object> listener, Activity activity) {
        this.activity = activity;

    }

    @Override
    public void onRequestFailure(SpiceException e) {
        e.printStackTrace();
    }

    @Override
    public void onRequestSuccess(final Object o) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground( Void... voids ) {
                //listeHoraireEtProf
                if (o instanceof listeDesActivitesEtProf) {
                    listeDesActivitesEtProf listeDesActivitesEtProf = (listeDesActivitesEtProf) o;

                    deleteExpiredHoraireActivite(listeDesActivitesEtProf);
                    createOrUpdateHoraireActiviteInDB(listeDesActivitesEtProf);
                }

                //lireJoursRemplaces
                if (o instanceof listeJoursRemplaces) {
                    listeJoursRemplaces listeJoursRemplaces = (listeJoursRemplaces) o;

                    deleteExpiredJoursRemplaces(listeJoursRemplaces);
                    createOrUpdateJoursRemplacesInDB(listeJoursRemplaces);
                    syncJoursRemplacesEnded = true;
                }

                //listeSeances
                if (o instanceof listeSeances) {
                    listeSeances listeSeancesObj = (listeSeances) o;

                    deleteExpiredSeances(listeSeancesObj);
                    createOrUpdateSeancesInDB(listeSeancesObj);
                    syncSeancesEnded = true;
                }

                // ETS Calendar Events
                if (o instanceof EventList) {
                    deleteExpiredEvent((EventList) o);
                    createOrUpdateEventListInBD((EventList) o);
                    syncEventListEnded = true;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (syncJoursRemplacesEnded && syncSeancesEnded && syncEventListEnded) {
                    HoraireManager.this.setChanged();
                    HoraireManager.this.notifyObservers();
                }
            }
        }.execute();

    }

    /**
     * Deletes entries in DB that doesn't exist on API
     *
     * @param
     */
    private void deleteExpiredEvent(EventList envEventList) {

        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        ArrayList<Event> dbEvents = new ArrayList<Event>();
        try {
            dbEvents = (ArrayList<Event>) dbHelper.getDao(Event.class).queryForAll();
            for (Event eventsNew : dbEvents) {

                if (!dbEvents.contains(eventsNew.getId())) {
                    Dao<Event, String> eventDao = dbHelper.getDao(Event.class);
                    eventDao.deleteById(eventsNew.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds new API entries on DB or updates existing ones
     *
     * @param eventList
     */
    private void createOrUpdateEventListInBD(EventList eventList) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            for (Event event : eventList) {
                dbHelper.getDao(Event.class).createOrUpdate(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes entries in DB that doesn't exist on API
     *
     * @param listeJoursRemplaces
     */
    private void deleteExpiredJoursRemplaces(listeJoursRemplaces listeJoursRemplaces) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        HashMap<String, JoursRemplaces> listeJoursRemplacesInAPI = new HashMap<String, JoursRemplaces>();

        //Building the list of entries in API
        for (JoursRemplaces JoursRemplacesInAPI : listeJoursRemplaces.listeJours) {
            listeJoursRemplacesInAPI.put(JoursRemplacesInAPI.dateOrigine, JoursRemplacesInAPI);
        }

        ArrayList<JoursRemplaces> listeJoursRemplacesInDB = new ArrayList<JoursRemplaces>();

        //Comparing entries on DB and API
        try {
            listeJoursRemplacesInDB = (ArrayList<JoursRemplaces>) dbHelper.getDao(JoursRemplaces.class).queryForAll();

            for (JoursRemplaces JoursRemplacesInDB : listeJoursRemplacesInDB) {

                if (!listeJoursRemplacesInAPI.containsKey((String) JoursRemplacesInDB.dateOrigine)) {
                    Dao<JoursRemplaces, String> JoursRemplacesDao = dbHelper.getDao(JoursRemplaces.class);

                    JoursRemplacesDao.deleteById(JoursRemplacesInDB.dateOrigine);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds new API entries on DB or updates existing ones
     *
     * @param listeJoursRemplaces
     */
    private void createOrUpdateJoursRemplacesInDB(listeJoursRemplaces listeJoursRemplaces) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            for (JoursRemplaces JoursRemplaces : listeJoursRemplaces.listeJours) {
                dbHelper.getDao(JoursRemplaces.class).createOrUpdate(JoursRemplaces);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes entries in DB that doesn't exist on API
     *
     * @param listeSeances
     */
    private void deleteExpiredSeances(listeSeances listeSeances) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        HashMap<String, Seances> listeSeancesInAPI = new HashMap<String, Seances>();

        //Building the list of entries in API
        for (Seances SeancesInAPI : listeSeances.ListeDesSeances) {

            SeancesInAPI.id = SeancesInAPI.coursGroupe +
                    SeancesInAPI.dateDebut +
                    SeancesInAPI.dateFin +
                    SeancesInAPI.local;

            listeSeancesInAPI.put(SeancesInAPI.id, SeancesInAPI);
        }

        ArrayList<Seances> listeSeancesInDB = new ArrayList<Seances>();

        //Comparing entries on DB and API
        try {
            listeSeancesInDB = (ArrayList<Seances>) dbHelper.getDao(Seances.class).queryForAll();

            for (Seances SeancesInDB : listeSeancesInDB) {

                if (!listeSeancesInAPI.containsKey((String) SeancesInDB.id)) {
                    Dao<Seances, String> SeancesDao = dbHelper.getDao(Seances.class);

                    SeancesDao.deleteById(SeancesInDB.id);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds new API entries on DB or updates existing ones
     *
     * @param listeSeances
     */
    private void createOrUpdateSeancesInDB(listeSeances listeSeances) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            for (Seances Seances : listeSeances.ListeDesSeances) {
                dbHelper.getDao(Seances.class).createOrUpdate(Seances);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes entries in DB that doesn't exist on API
     *
     * @param listeHoraireExamensFinaux
     */
    private void deleteExpiredExamensFinaux(listeHoraireExamensFinaux listeHoraireExamensFinaux) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        HashMap<String, HoraireExamenFinal> listeHoraireExamenFinalInAPI = new HashMap<String, HoraireExamenFinal>();

        //Building the list of entries in API
        for (HoraireExamenFinal horaireExamenFinalInAPI : listeHoraireExamensFinaux.listeHoraire) {

            horaireExamenFinalInAPI.id = horaireExamenFinalInAPI.sigle + "-" +
                    horaireExamenFinalInAPI.groupe +
                    horaireExamenFinalInAPI.dateExamen +
                    horaireExamenFinalInAPI.heureDebut +
                    horaireExamenFinalInAPI.heureFin;

            listeHoraireExamenFinalInAPI.put(horaireExamenFinalInAPI.id, horaireExamenFinalInAPI);
        }

        ArrayList<HoraireExamenFinal> listeHoraireExamenFinalInDB = new ArrayList<HoraireExamenFinal>();

        //Comparing entries on DB and API
        try {
            listeHoraireExamenFinalInDB = (ArrayList<HoraireExamenFinal>) dbHelper.getDao(HoraireExamenFinal.class).queryForAll();

            for (HoraireExamenFinal horaireExamenFinalInDB : listeHoraireExamenFinalInDB) {

                if (!listeHoraireExamenFinalInAPI.containsKey((String) horaireExamenFinalInDB.id)) {
                    Dao<HoraireExamenFinal, String> horaireExamenFinalDao = dbHelper.getDao(HoraireExamenFinal.class);

                    horaireExamenFinalDao.deleteById(horaireExamenFinalInDB.id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds new API entries on DB or updates existing ones
     *
     * @param listeHoraireExamensFinaux
     */
    private void createOrUpdateExamensFinauxInDB(listeHoraireExamensFinaux listeHoraireExamensFinaux) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            for (HoraireExamenFinal horaireExamenFinal : listeHoraireExamensFinaux.listeHoraire) {
                dbHelper.getDao(HoraireExamenFinal.class).createOrUpdate(horaireExamenFinal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes entries in DB that doesn't exist on API
     *
     * @param listeDesActivitesEtProf API list
     */
    private void deleteExpiredHoraireActivite(listeDesActivitesEtProf listeDesActivitesEtProf) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        HashMap<String, HoraireActivite> listeHoraireActiviteInAPI = new HashMap<String, HoraireActivite>();

        //Building the list of entries in API
        for (HoraireActivite horaireActiviteInAPI : listeDesActivitesEtProf.listeActivites) {
            horaireActiviteInAPI.id = "" + horaireActiviteInAPI.sigle +
                    horaireActiviteInAPI.groupe +
                    horaireActiviteInAPI.jour +
                    horaireActiviteInAPI.heureDebut +
                    horaireActiviteInAPI.heureFin;

            listeHoraireActiviteInAPI.put(horaireActiviteInAPI.id, horaireActiviteInAPI);
        }

        ArrayList<HoraireActivite> listeHoraireActiviteInDB = new ArrayList<HoraireActivite>();

        //Comparing entries on DB and API
        try {
            listeHoraireActiviteInDB = (ArrayList<HoraireActivite>) dbHelper.getDao(HoraireActivite.class).queryForAll();

            for (HoraireActivite horaireActiviteInDB : listeHoraireActiviteInDB) {

                if (!listeHoraireActiviteInAPI.containsKey((String) horaireActiviteInDB.id)) {
                    Dao<HoraireActivite, String> horaireActiviteDao = dbHelper.getDao(HoraireActivite.class);

                    horaireActiviteDao.deleteById(horaireActiviteInDB.id);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Adds new API entries on DB or updates existing ones
     *
     * @param listeDesActivitesEtProf API list
     */
    private void createOrUpdateHoraireActiviteInDB(listeDesActivitesEtProf listeDesActivitesEtProf) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            for (HoraireActivite horaireActivite : listeDesActivitesEtProf.listeActivites) {
                dbHelper.getDao(HoraireActivite.class).createOrUpdate(horaireActivite);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates/Updates a new calendar on the user's device
     *
     * @param joursRemplacesSelected true if the "Jours remplacés" calendar was selected
     * @param seancesSelected true if the "Séances" calendar was selected
     * @param calPublicSelected true if the "Calendrier public ÉTS" was selected
     * @throws Exception if there is an SQL when checking the replaced days (Jours remplacés)
     */
    public void updateCalendar(boolean joursRemplacesSelected, boolean seancesSelected, boolean calPublicSelected) throws Exception {

        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        AndroidCalendarManager androidCalendarManager = new AndroidCalendarManager(activity);

        androidCalendarManager.deleteCalendar(calendarName);
        androidCalendarManager.createCalendar(calendarName);

        SimpleDateFormat joursRemplacesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH);

        if (joursRemplacesSelected) {
            //Inserting JoursRemplaces in local calendar
            ArrayList<JoursRemplaces> listeJoursRemplaces = (ArrayList<JoursRemplaces>) dbHelper.getDao(JoursRemplaces.class).queryForAll();


            for (JoursRemplaces joursRemplaces : listeJoursRemplaces) {
                androidCalendarManager.insertEventInCalendar(calendarName,
                        joursRemplaces.description,
                        joursRemplaces.description,
                        "",
                        joursRemplacesFormatter.parse(joursRemplaces.dateOrigine),
                        joursRemplacesFormatter.parse(joursRemplaces.dateOrigine));
            }
        }

        if (seancesSelected) {
            //Inserting Seances in local calendar
            SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA_FRENCH);
            ArrayList<Seances> seances = (ArrayList<Seances>) dbHelper.getDao(Seances.class).queryForAll();


            for (Seances seance : seances) {

                androidCalendarManager.insertEventInCalendar(calendarName,
                        seance.descriptionActivite.equals("Examen final") ? "Examen final " + seance.coursGroupe : seance.coursGroupe,
                        seance.libelleCours + " - " + seance.descriptionActivite,
                        seance.local,
                        seancesFormatter.parse(seance.dateDebut),
                        seancesFormatter.parse(seance.dateFin));
            }
        }

        if (calPublicSelected) {
            //Inserting public calendar ETS
            ArrayList<Event> events = (ArrayList<Event>) dbHelper.getDao(Event.class).queryForAll();
            for (Event event : events) {
                androidCalendarManager.insertEventInCalendar(calendarName,
                        event.getTitle(),
                        "",
                        ""
                        ,
                        joursRemplacesFormatter.parse(event.getDateDebut()),
                        joursRemplacesFormatter.parse(event.getDateFin()));
            }
        }
    }
}