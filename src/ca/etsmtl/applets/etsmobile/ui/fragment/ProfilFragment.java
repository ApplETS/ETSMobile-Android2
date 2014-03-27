package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.Programme;
import ca.etsmtl.applets.etsmobile.model.listeDesProgrammes;
import ca.etsmtl.applets.etsmobile.ui.fragment.HttpFragment;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 * Create content by Laurence on 27/02/2014
 */
public class ProfilFragment extends HttpFragment implements android.view.View.OnClickListener{
	
	private Etudiant etudiant;
	private listeDesProgrammes mlisteDesProgrammes;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profil, container, false);
	
		((Button)v.findViewById(R.id.profil_button_logout)).setOnClickListener(this);
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		DataManager datamanager = DataManager.getInstance(getActivity());
		datamanager.getDataFromSignet(SignetMethods.INFO_ETUDIANT, ApplicationManager.userCredentials, this, "");
		datamanager.getDataFromSignet(SignetMethods.LIST_PROGRAM, ApplicationManager.userCredentials, this, "");
	}

	
	@Override
	public void onClick(View v) {
		if(v.getId()== R.id.profil_button_logout){
			Utility.deconnexion(getActivity());
		}
		
	}
	
	
	@Override
	public void onRequestFailure(SpiceException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestSuccess(Object o) {
		if(o!=null){
			if(o instanceof Etudiant){
				etudiant = (Etudiant)o;
				if(etudiant.erreur!=null){
					Log.d("ProfilFragment", "ProfilFragment : onRequestSuccess: etudiant error"+etudiant.erreur);
					etudiant = null;
				}
			}else if( o instanceof listeDesProgrammes){
				mlisteDesProgrammes = (listeDesProgrammes)o;
				if(mlisteDesProgrammes.erreur!=null){
					Log.d("ProfilFragment", "ProfilFragment : onRequestSuccess: listeDesProgramme error"+mlisteDesProgrammes.erreur);
					mlisteDesProgrammes=null;
				}
			}
			refreshUi();
		}

	}

	@Override
	void updateUI() {
		// TODO Auto-generated method stub
	}

	private void refreshUi(){
		if( etudiant!=null && mlisteDesProgrammes!=null){
			getActivity().runOnUiThread( new Runnable() {
				public void run() {
					View v = getView();
					if( v!=null){
						String nom = etudiant.nom.trim();
						String prenom = etudiant.prenom.trim();
					
				        ((TextView)v.findViewById(R.id.profil_nom_prenom_item)).setText(nom+", "+prenom);
					    ((TextView)v.findViewById(R.id.profil_code_permanent_item)).setText(etudiant.codePerm);
					    ((TextView)v.findViewById(R.id.profil_solde_item)).setText(etudiant.soldeTotal);
					    int size = mlisteDesProgrammes.liste.size();
					    Programme program = mlisteDesProgrammes.liste.get(size-1);
					  
					    ((TextView)v.findViewById(R.id.profil_programme_item)).setText(program.libelle);
					    ((TextView)v.findViewById(R.id.profil_credit_reussis_item)).setText(program.nbCreditsCompletes);
					    ((TextView)v.findViewById(R.id.profil_cours_reussis_item)).setText(program.nbCrsReussis);
					    ((TextView)v.findViewById(R.id.profil_credit_echoue_item)).setText(program.nbCrsEchoues);
					    ((TextView)v.findViewById(R.id.profil_credit_inscrit_item)).setText(program.nbCreditsInscrits);
					    ((TextView)v.findViewById(R.id.profil_cours_equivalent_item)).setText(program.nbEquivalences);
					    ((TextView)v.findViewById(R.id.profil_credit_potentiel_item)).setText(program.nbCreditsPotentiels);
					    ((TextView)v.findViewById(R.id.profil_moyenne_item)).setText(program.moyenne);
					}
				}
			});
		}
	}
}
