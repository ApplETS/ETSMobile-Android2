package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
import android.widget.Toast;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.HoraireActivite;
import ca.etsmtl.applets.etsmobile.model.HoraireExamenFinal;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile2.R;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaut on 30/08/14.
 */
public class HoraireFragment extends HttpFragment implements Observer {

	private TextView message;
	private HoraireManager horaireManager;
	private CustomProgressDialog customProgressDialog;
	
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
				
		
		Button updateCalendarButton = (Button) v.findViewById(R.id.btn_update_calendar);
		
		updateCalendarButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
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
		
		horaireManager = new HoraireManager(this, getActivity());
		horaireManager.addObserver(this);
		
		customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner,"Synchronisation en cours");
		customProgressDialog.show();
		
		dataManager.getDataFromSignet(SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
		dataManager.getDataFromSignet(SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
		dataManager.getDataFromSignet(SignetMethods.LIST_EXAM_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
		
		return v;
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		customProgressDialog.dismiss();
		Toast.makeText(getActivity(), "La synchronisation a échoué.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRequestSuccess(Object o) {
		horaireManager.onRequestSuccess(o);
		
	}
	
	@Override
	void updateUI() {
		
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		
		// Affichage des entrées
		message.setText("");
		try {
			List<HoraireExamenFinal> list = null;
			list = dbHelper.getDao(HoraireExamenFinal.class).queryForAll();
		
			if(list.isEmpty()) {
				Log.e("liste vide", "liste vide");
			}
			
			for(HoraireExamenFinal horaireExamenFinal : list){
				message.setText(message.getText() +horaireExamenFinal.id+" "+ horaireExamenFinal.sigle + " "
						+ horaireExamenFinal.local + " " + horaireExamenFinal.dateExamen + " "
						+ horaireExamenFinal.heureDebut+"\n");
				
//				Log.d("cours", horaireExamenFinal.titreCours + " "
//				+ horaireExamenFinal.jour + " " + horaireExamenFinal.journee + " "
//				+ horaireExamenFinal.heureDebut);
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

	@Override
	public void update(Observable observable, Object data) {
		
		if(data == HoraireManager.Synchronized.DB_CALENDAR) {
			customProgressDialog.dismiss();
			message.setText(message.getText()+"DB Synchronisée\n");
			
		}
		
		
		
	}

}
