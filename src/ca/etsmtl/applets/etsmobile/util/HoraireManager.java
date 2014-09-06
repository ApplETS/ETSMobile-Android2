package ca.etsmtl.applets.etsmobile.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class HoraireManager implements RequestListener<Object> {

	private Activity activity;
	
	public HoraireManager(final RequestListener<Object> listener, Activity activity) {
		this.activity = activity;
		DataManager dataManager = DataManager.getInstance(activity);
		dataManager.getDataFromSignet(
				SignetMethods.LIST_HORAIRE_PROF,
				ApplicationManager.userCredentials,this,"É2014");
		
		dataManager.getDataFromSignet(
				SignetMethods.LIST_EXAMENS_FINAUX,
				ApplicationManager.userCredentials,this,"É2014");
		
//		dataManager.getDataFromSignet(SignetMethods.LIST_SEANCES, ApplicationManager.userCredentials, this, "");
	}
	
	@Override
	public void onRequestFailure(SpiceException e) {
		e.printStackTrace();
	}

	@Override
	public void onRequestSuccess(Object o) {
		
		//listeHoraireEtProf
		if (o instanceof listeDesActivitesEtProf) {
			
			listeDesActivitesEtProf listeDesActivitesEtProf = (listeDesActivitesEtProf) o;
			
			deleteExpiredHoraireActivite(listeDesActivitesEtProf);
			createOrUpdateHoraireActiviteInDB(listeDesActivitesEtProf);
		} else {
			Log.e("ELSE",""+o.toString());
		}
		
		//lireJoursRemplaces
		if(o instanceof listeJoursRemplaces) {
			listeJoursRemplaces listeJoursRemplaces = (listeJoursRemplaces) o;
			
//			deleteExpiredJoursRemplaces(listeJoursRemplaces);
//			createOrUpdateJoursRemplacesInDB(listeJoursRemplaces);
		}
		
		
		//listeHoraireExamensFin
		if(o instanceof listeHoraireExamensFinaux ) {
			listeHoraireExamensFinaux listeHoraireExamensFinaux = (listeHoraireExamensFinaux) o;
			
			for(HoraireExamenFinal horaireExamenFinal : listeHoraireExamensFinaux.listeHoraire) {
				Log.e("HoraireExamenFinal",	horaireExamenFinal.sigle+" "+
											horaireExamenFinal.dateExamen+" "+
											horaireExamenFinal.heureDebut+" "+
											horaireExamenFinal.heureFin);
			}
			
		}
		
		if(o instanceof listeSeances) {
			
			
			listeSeances listeSeances = (listeSeances) o;
			Log.e("Seance","o instanceof listeSeances "+listeSeances.ListeDesSeances.size());
			
			for(Seances seances : listeSeances.ListeDesSeances) {
				Log.e("Seance",seances.libelleCours+"");
			}
			
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
