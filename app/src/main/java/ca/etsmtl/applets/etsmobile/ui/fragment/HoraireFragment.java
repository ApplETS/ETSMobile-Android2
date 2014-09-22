package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.Observable;
import java.util.Observer;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.util.HoraireManager;
import ca.etsmtl.applets.etsmobile.views.CustomProgressDialog;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by Thibaut on 30/08/14.
 */
public class HoraireFragment extends HttpFragment implements Observer {

	private HoraireManager horaireManager;
	private CustomProgressDialog customProgressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_calendar, container, false);

		horaireManager = new HoraireManager(this, getActivity());
		horaireManager.addObserver(this);
		
		customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner,"Synchronisation en cours");
		customProgressDialog.show();
		
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
	public void onRequestSuccess(Object o) {
		horaireManager.onRequestSuccess(o);
		
	}
	
	@Override
	void updateUI() {
		
	}

	@Override
	public void update(Observable observable, Object data) {
		
		customProgressDialog.dismiss();
		
		new AsyncTask<Object, Void, Object>() {
			private Exception exception = null;
			protected void onPreExecute() {
				customProgressDialog = new CustomProgressDialog(getActivity(), R.drawable.loading_spinner,"Mise à jour du calendrier en cours");
				customProgressDialog.show();
			}
			
			@Override
			protected Object doInBackground(Object... params) {
				try {
					horaireManager.updateCalendar();
				} catch(Exception e) {
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
