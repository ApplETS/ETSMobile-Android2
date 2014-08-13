package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.http.soap.WebServiceSoap;
import ca.etsmtl.applets.etsmobile.model.HoraireActivite;
import ca.etsmtl.applets.etsmobile.model.coursHoraire;
import ca.etsmtl.applets.etsmobile.model.listeCoursHoraire;
import ca.etsmtl.applets.etsmobile.model.listeDesActivitesEtProf;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile2.R;

import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.octo.android.robospice.persistence.exception.SpiceException;

public class HoraireFragment extends HttpFragment {

	private TextView message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_calendar, container, false);

		message = (TextView) v.findViewById(R.id.tv_calendar);
		
		Button viderTableButton = (Button) v.findViewById(R.id.btn_vider_table_horaireActivite);
		viderTableButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//* Vider la table
				try {
					DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
					
					List<HoraireActivite> list = null;
					try {
						list = dbHelper.getDao(HoraireActivite.class).queryForAll();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					Log.e("vider table", "vider table");
					dbHelper.getDao(HoraireActivite.class).delete(list);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				//*/
				
			}
		});
				
		
		/* Lancement de l'application calendrier du cellulaire
		long startMillis = java.lang.System.currentTimeMillis();
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		ContentUris.appendId(builder, startMillis);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(builder.build());

		startActivity(intent);
		
		//*/
		

		
//		datamanager.getDataFromSignet(
//				SignetMethods.LIST_HORAIRE_PROF,
//				ApplicationManager.userCredentials,this,"É2014");
		
		
		
		
		return v;
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {}

	@Override
	public void onRequestSuccess(Object o) {}

	@Override
	void updateUI() {
		
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		
		HoraireManager horaireManager = new HoraireManager(this, getActivity());
		
		
		// Affichage des entrées
		message.setText("");
		try {
			List<HoraireActivite> list = null;
			list = dbHelper.getDao(HoraireActivite.class).queryForAll();
		
			if(list.isEmpty()) {
				Log.e("liste vide", "liste vide");
			}
			
			for(HoraireActivite horaireActivite : list){
				message.setText(message.getText() +horaireActivite.id+" "+ horaireActivite.titreCours + " "
						+ horaireActivite.jour + " " + horaireActivite.journee + " "
						+ horaireActivite.heureDebut+"\n");
				
				Log.d("cours", horaireActivite.titreCours + " "
				+ horaireActivite.jour + " " + horaireActivite.journee + " "
				+ horaireActivite.heureDebut);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		message.setText(message.getText() +"\n\n");
		
		
		/*
		//Specific query
		List<HoraireActivite> list = null;
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		try {
			QueryBuilder<HoraireActivite, Integer> queryBuilder = (QueryBuilder<HoraireActivite, Integer>) dbHelper.getDao(HoraireActivite.class).queryBuilder();
			
			queryBuilder.where().eq("journee", "Jeudi").and().eq("heureDebut", "09:00");
			
			PreparedQuery<HoraireActivite> preparedQuery = queryBuilder.prepare();
			
			list = dbHelper.getDao(HoraireActivite.class).query(preparedQuery);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		
		for(HoraireActivite horaireActivite : list){
			message.setText(message.getText() + horaireActivite.titreCours + " "
					+ horaireActivite.jour + " " + horaireActivite.journee + " "
					+ horaireActivite.heureDebut+"\n");
		}
		
		 */
		

	}

}
