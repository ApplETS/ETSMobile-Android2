package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;

import ca.etsmtl.applets.etsmobile2.R;

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
	private Button btn_ajout_contact;
	
	public static BottinDetailsFragment newInstance(String nom, String prenom, String telBureau, String emplacement, String courriel, String service, String titre){
		BottinDetailsFragment fragment = new BottinDetailsFragment();
		Bundle args = new Bundle();

		args.putString(NOM, nom);
		args.putString(PRENOM, prenom);
		args.putString(TITRE, titre);
		args.putString(SERVICE, service);
		args.putString(EMPLACEMENT, emplacement);
		args.putString(TELBUREAU, telBureau);
		args.putString(COURRIEL, courriel);
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
			
			if(emplacement == null)
				emplacement = "-";
			if(telbureau == null)
				telbureau = "-";
			if(courriel == null)
				courriel = "-";
			
			
				
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
		btn_ajout_contact = (Button) v.findViewById(R.id.btn_ajout_contact_bottin);
		
		tv_nom_prenom.setText(prenom+" "+nom);
		tv_titre.setText(titre);
		tv_service.setText(service);
		tv_emplacement.setText(emplacement);
		tv_telbureau.setText(telbureau);
		Linkify.addLinks(tv_telbureau, Linkify.PHONE_NUMBERS);
		tv_courriel.setText(courriel);
		Linkify.addLinks(tv_courriel, Linkify.EMAIL_ADDRESSES);
		
		btn_ajout_contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString(BottinDetailsFragment.NOM, nom);
				args.putString(PRENOM, prenom);
				args.putString(TITRE, titre);
				args.putString(SERVICE, service);
				args.putString(EMPLACEMENT, emplacement);
				args.putString(TELBUREAU, telbureau);
				args.putString(COURRIEL, courriel);
				
				
				Fragment fragment = ContactAdderFragment.newInstance(args);
				showFragment(fragment);
			}
		});
		// get the listview
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	private void showFragment(final Fragment fragment) {
		if (fragment == null)
			return;

		// Begin a fragment transaction.
		final FragmentManager fm = getActivity().getFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		// We can also animate the changing of fragment.
//		ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		// Replace current fragment by the new one.
		ft.replace(R.id.content_frame, fragment);
		// Null on the back stack to return on the previous fragment when user
		// press on back button.
		ft.addToBackStack(null);

		// Commit changes.
		ft.commit();
	}
	
	@Override
	public void onRequestFailure(SpiceException arg0) {

	}

	@Override
	public void onRequestSuccess(Object o) {
				
	} 
	
	@Override
	void updateUI() {

	}
	
	
}
