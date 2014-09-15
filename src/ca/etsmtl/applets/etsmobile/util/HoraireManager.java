package ca.etsmtl.applets.etsmobile.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.HoraireActivite;
import ca.etsmtl.applets.etsmobile.model.HoraireExamenFinal;
import ca.etsmtl.applets.etsmobile.model.JoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.model.listeDesActivitesEtProf;
import ca.etsmtl.applets.etsmobile.model.listeHoraireExamensFinaux;
import ca.etsmtl.applets.etsmobile.model.listeJoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.listeSeances;
import ca.etsmtl.applets.etsmobile2.R;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class HoraireManager extends Observable implements RequestListener<Object> {

	private Activity activity;
	private boolean syncSeancesEnded = false;
	private boolean syncJoursRemplacesEnded = false;
	private boolean syncExamensEnded = false;
	
	public enum Synchronized {DB_CALENDAR, ANDROID_CALENDAR}; 
	
	public HoraireManager(final RequestListener<Object> listener, Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onRequestFailure(SpiceException e) {
		e.printStackTrace();
	}

	@Override
	public void onRequestSuccess(Object o) {
		
		Log.e("object",""+o.getClass());
		
		//listeHoraireEtProf
		if (o instanceof listeDesActivitesEtProf) {
			listeDesActivitesEtProf listeDesActivitesEtProf = (listeDesActivitesEtProf) o;
			
			deleteExpiredHoraireActivite(listeDesActivitesEtProf);
			createOrUpdateHoraireActiviteInDB(listeDesActivitesEtProf);
		}
		
		//lireJoursRemplaces
		if(o instanceof listeJoursRemplaces) {
			listeJoursRemplaces listeJoursRemplaces = (listeJoursRemplaces) o;
			
			deleteExpiredJoursRemplaces(listeJoursRemplaces);
			createOrUpdateJoursRemplacesInDB(listeJoursRemplaces);
			syncJoursRemplacesEnded = true;
			
//			for(JoursRemplaces joursRemplaces : listeJoursRemplaces.listeJours) {
//				Log.e("JoursRemplaces",joursRemplaces.dateOrigine+ " : "+joursRemplaces.description);
//			}
			
			
		}
		
		//listeHoraireExamensFin
		if(o instanceof listeHoraireExamensFinaux ) {
			listeHoraireExamensFinaux listeHoraireExamensFinaux = (listeHoraireExamensFinaux) o;
			
			deleteExpiredExamensFinaux(listeHoraireExamensFinaux);
			createOrUpdateExamensFinauxInDB(listeHoraireExamensFinaux);
			syncExamensEnded = true;
			
//			for(HoraireExamenFinal horaireExamenFinal : listeHoraireExamensFinaux.listeHoraire) {
//				Log.e("HoraireExamenFinal",	horaireExamenFinal.sigle+" "+
//											horaireExamenFinal.dateExamen+" "+
//											horaireExamenFinal.heureDebut+" "+
//											horaireExamenFinal.heureFin);
//			}
			
		}
		
		//listeSeances
		if(o instanceof listeSeances) {
			
			listeSeances listeSeances = (listeSeances) o;
			
			deleteExpiredSeances(listeSeances);
			createOrUpdateSeancesInDB(listeSeances);
			syncSeancesEnded = true;
			
//			for(Seances seances : listeSeances.ListeDesSeances) {
//				Log.e("Seance",seances.dateDebut+ " : "+seances.libelleCours);
//			}
		}
		
		if(syncExamensEnded && syncJoursRemplacesEnded && syncSeancesEnded) {
			this.setChanged();
			this.notifyObservers(Synchronized.DB_CALENDAR);
		}

	}
	
	/**
	 * Deletes entries in DB that doesn't exist on API
	 * @param listeJoursRemplaces
	 */
	private void deleteExpiredJoursRemplaces(listeJoursRemplaces listeJoursRemplaces) {
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		HashMap<String, JoursRemplaces> listeJoursRemplacesInAPI = new HashMap<String, JoursRemplaces>();
		
		//Building the list of entries in API
		for (JoursRemplaces JoursRemplacesInAPI : listeJoursRemplaces.listeJours) {
			listeJoursRemplacesInAPI.put(JoursRemplacesInAPI.dateOrigine,JoursRemplacesInAPI);
		}
		
		ArrayList<JoursRemplaces> listeJoursRemplacesInDB = new ArrayList<JoursRemplaces>();
		
		//Comparing entries on DB and API
		try {
			listeJoursRemplacesInDB = (ArrayList<JoursRemplaces>) dbHelper.getDao(JoursRemplaces.class).queryForAll();
	
			for (JoursRemplaces JoursRemplacesInDB : listeJoursRemplacesInDB) {
	
				if (!listeJoursRemplacesInAPI.containsKey((String) JoursRemplacesInDB.dateOrigine)) {
					Dao<JoursRemplaces, String> JoursRemplacesDao = dbHelper.getDao(JoursRemplaces.class);
	
					JoursRemplacesDao.deleteById(JoursRemplacesInDB.dateOrigine);
					Log.v("Supression", JoursRemplacesInDB.dateOrigine+" supprimé");
				}
	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds new API entries on DB or updates existing ones
	 * @param listeJoursRemplaces
	 */
	private void createOrUpdateJoursRemplacesInDB(listeJoursRemplaces listeJoursRemplaces) {
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		
		try {
			for(JoursRemplaces JoursRemplaces : listeJoursRemplaces.listeJours) {
				dbHelper.getDao(JoursRemplaces.class).createOrUpdate(JoursRemplaces);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes entries in DB that doesn't exist on API
	 * @param listeSeances
	 */
	private void deleteExpiredSeances(listeSeances listeSeances) {
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		
		HashMap<String, Seances> listeSeancesInAPI = new HashMap<String, Seances>();
		
		//Building the list of entries in API
		for (Seances SeancesInAPI : listeSeances.ListeDesSeances) {
			
			SeancesInAPI.id = 	SeancesInAPI.coursGroupe +
											SeancesInAPI.dateDebut +
											SeancesInAPI.dateFin;
			
			listeSeancesInAPI.put(SeancesInAPI.id,SeancesInAPI);
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
	 * @param listeSeances
	 */
	private void createOrUpdateSeancesInDB(listeSeances listeSeances) {
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		
		try {
			for(Seances Seances : listeSeances.ListeDesSeances) {
				dbHelper.getDao(Seances.class).createOrUpdate(Seances);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes entries in DB that doesn't exist on API
	 * @param listeHoraireExamensFinaux
	 */
	private void deleteExpiredExamensFinaux(listeHoraireExamensFinaux listeHoraireExamensFinaux) {
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
	
		HashMap<String, HoraireExamenFinal> listeHoraireExamenFinalInAPI = new HashMap<String, HoraireExamenFinal>();
		
		//Building the list of entries in API
		for (HoraireExamenFinal horaireExamenFinalInAPI : listeHoraireExamensFinaux.listeHoraire) {
			
			horaireExamenFinalInAPI.id = 	horaireExamenFinalInAPI.sigle + "-" +
											horaireExamenFinalInAPI.groupe +
											horaireExamenFinalInAPI.dateExamen +
											horaireExamenFinalInAPI.heureDebut +
											horaireExamenFinalInAPI.heureFin;
			
			listeHoraireExamenFinalInAPI.put(horaireExamenFinalInAPI.id,horaireExamenFinalInAPI);
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
	 * @param listeHoraireExamensFinaux
	 */
	private void createOrUpdateExamensFinauxInDB(listeHoraireExamensFinaux listeHoraireExamensFinaux) {
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		
		try {
			for(HoraireExamenFinal horaireExamenFinal : listeHoraireExamensFinaux.listeHoraire) {
				dbHelper.getDao(HoraireExamenFinal.class).createOrUpdate(horaireExamenFinal);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes entries in DB that doesn't exist on API
	 * @param listeDesActivitesEtProf API list
	 */
	private void deleteExpiredHoraireActivite(listeDesActivitesEtProf listeDesActivitesEtProf){
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		
		HashMap<String, HoraireActivite> listeHoraireActiviteInAPI = new HashMap<String, HoraireActivite>();
		
		//Building the list of entries in API
		for (HoraireActivite horaireActiviteInAPI : listeDesActivitesEtProf.listeActivites) {
			horaireActiviteInAPI.id = ""+horaireActiviteInAPI.sigle + 
									horaireActiviteInAPI.groupe + 
									horaireActiviteInAPI.jour + 
									horaireActiviteInAPI.heureDebut + 
									horaireActiviteInAPI.heureFin;
			
			listeHoraireActiviteInAPI.put(horaireActiviteInAPI.id,horaireActiviteInAPI);
		}
		
		ArrayList<HoraireActivite> listeHoraireActiviteInDB = new ArrayList<HoraireActivite>();
		
		//Comparing entries on DB and API
		try {
			listeHoraireActiviteInDB = (ArrayList<HoraireActivite>) dbHelper.getDao(HoraireActivite.class).queryForAll();

			for (HoraireActivite horaireActiviteInDB : listeHoraireActiviteInDB) {

				if (!listeHoraireActiviteInAPI.containsKey((String) horaireActiviteInDB.id)) {
					Dao<HoraireActivite, String> horaireActiviteDao = dbHelper.getDao(HoraireActivite.class);

					horaireActiviteDao.deleteById(horaireActiviteInDB.id);
					Log.v("Supression", horaireActiviteInDB.id+" supprimé");
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Adds new API entries on DB or updates existing ones
	 * @param listeDesActivitesEtProf API list
	 */
	private void createOrUpdateHoraireActiviteInDB(listeDesActivitesEtProf listeDesActivitesEtProf){
		DatabaseHelper dbHelper = new DatabaseHelper(activity);
		
		try {
			for(HoraireActivite horaireActivite : listeDesActivitesEtProf.listeActivites) {
				dbHelper.getDao(HoraireActivite.class).createOrUpdate(horaireActivite);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
