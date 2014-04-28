package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.ArrayOfFicheEmploye;
import ca.etsmtl.applets.etsmobile.model.ArrayOfService;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListAdapter;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

public class BottinDetailsFragment extends HttpFragment {

	public static String NOM = "NOM";
	public static String PRENOM = "PRENOM";
	public static String TITRE = "TITRE";
	public static String SERVICE = "SERVICE";
	public static String EMPLACEMENT = "EMPLACEMENT";
	public static String TELBUREAU = "TELBUREAU";
	public static String COURRIEL = "COURRIEL";
	
	private String nom;
	private String prenom;
	private String titre;
	private String service;
	private String emplacement;
	private String telbureau;
	private String courriel;
	
	private TextView tv_nom_prenom;
	private TextView tv_titre;
	private TextView tv_service;
	private TextView tv_emplacement;
	private TextView tv_telbureau;
	private TextView tv_courriel;
	
	public static BottinDetailsFragment newInstance(FicheEmploye ficheEmploye){
		BottinDetailsFragment fragment = new BottinDetailsFragment();
		Bundle args = new Bundle();
		args.putString(NOM, ficheEmploye.Nom);
		args.putString(PRENOM, ficheEmploye.Prenom);
		args.putString(TITRE, ficheEmploye.Titre);
		args.putString(SERVICE, ficheEmploye.Service);
		args.putString(EMPLACEMENT, ficheEmploye.Emplacement);
		args.putString(TELBUREAU, ficheEmploye.TelBureau);
		args.putString(COURRIEL, ficheEmploye.Courriel);
		fragment.setArguments(args);
		return fragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			
			Bundle bundle = getArguments();
			nom = bundle.getString(NOM);
			prenom = bundle.getString(PRENOM);
			titre = bundle.getString(TITRE);
			service = bundle.getString(SERVICE);
			emplacement = bundle.getString(EMPLACEMENT);
			telbureau = bundle.getString(TELBUREAU);
			courriel = bundle.getString(COURRIEL);
			
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View v = inflater.inflate(R.layout.bottin_detail_fragment, container, false);
		
		tv_nom_prenom = (TextView) v.findViewById(R.id.tv_nom_prenom_bottin);
		tv_titre = (TextView) v.findViewById(R.id.tv_titre_bottin);
		tv_service = (TextView) v.findViewById(R.id.tv_service_bottin);
		tv_emplacement = (TextView) v.findViewById(R.id.tv_emplacement_bottin);
		tv_telbureau = (TextView) v.findViewById(R.id.tv_telbureau_bottin);
		tv_courriel = (TextView) v.findViewById(R.id.tv_courriel_bottin);
		
		tv_nom_prenom.setText(prenom+" "+nom);
		tv_titre.setText(titre);
		tv_service.setText(service);
		tv_emplacement.setText(emplacement);
		tv_telbureau.setText(telbureau);
		tv_courriel.setText(courriel);
		
		
		// get the listview
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		
//		DataManager datamanager = DataManager.getInstance(getActivity());
//		datamanager.getDataFromSignet(SignetMethods.BOTTIN_LIST_DEPT, ApplicationManager.userCredentials, this);
	}
	
	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestSuccess(Object o) {
				
				
		

	} 
	
//	private void refresh(){
//		
//		final BottinAdapter bottinAdapter = new BottinAdapter(getActivity(),R.id.activity_bottin_listview, arrayOfService);
//		Activity activity = getActivity();
//		if(activity!=null){
//			getActivity().runOnUiThread( new Runnable() {
//				public void run() {
//					mListView.setAdapter(bottinAdapter);
//				}
//			});
//		}
//	}

	@Override
	void updateUI() {
		// TODO Auto-generated method stub

	}
	
	
}
