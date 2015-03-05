package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.Programme;
import ca.etsmtl.applets.etsmobile.model.listeDesProgrammes;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * @author Philippe, Laurence
 */
public class ProfilFragment extends HttpFragment implements View.OnClickListener {

	private static final String TAG = "ProfilFragment";
	RelativeLayout profileLayout;
	private Etudiant etudiant;
	private listeDesProgrammes mlisteDesProgrammes;
    private ProfilManager profilManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_profil, container,  false);
        super.onCreateView(inflater, v, savedInstanceState);
		profileLayout = (RelativeLayout) v.findViewById(R.id.profil_layout_info);
        v.findViewById(R.id.profil_button_logout).setOnClickListener(this);

        profilManager = new ProfilManager(getActivity().getApplicationContext());

		return v;
	}

	@Override
	void updateUI() {
        loadingView.showLoadingView();
		dataManager.getDataFromSignet(SignetMethods.INFO_ETUDIANT, ApplicationManager.userCredentials, this, "");
		dataManager.getDataFromSignet(SignetMethods.LIST_PROGRAM, ApplicationManager.userCredentials, this, "");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.profil_button_logout) {
			ApplicationManager.deconnexion(getActivity());
		}
	}

	@Override
	public void onRequestSuccess(Object o) {
		super.onRequestSuccess(o);
		if (o != null) {
			if (o instanceof Etudiant) {
				etudiant = (Etudiant) o;
				if (etudiant.erreur != null) {
					Log.d(TAG, ": onRequestSuccess: etudiant error" + etudiant.erreur);
					etudiant = null;
				} else {
                    // Save Etudiant class in DB
                    profilManager.updateEtudiant(etudiant);
                }
			} else if (o instanceof listeDesProgrammes) {
				mlisteDesProgrammes = (listeDesProgrammes) o;
				if (mlisteDesProgrammes.erreur != null) {
					Log.d(TAG, ": onRequestSuccess: listeDesProgramme error" + mlisteDesProgrammes.erreur);
					mlisteDesProgrammes = null;
				}
			}
			refreshUi();
		}

	}

	private void refreshUi() {

        Programme tempProgram = null;
        
        // Local query if no Network Access
        if(etudiant == null)
            etudiant = profilManager.getEtudiant();
        
        
        if(mlisteDesProgrammes == null) {
            tempProgram = profilManager.getProgramme();
        } else {
            for (Programme p : mlisteDesProgrammes.liste) {
                if (p.statut.equals("actif") || p.statut.equals("tutuelle")) {
                    profilManager.updateProgramme(p);
                    tempProgram = p;
                }
            }
        }

        final Programme program = tempProgram;
        
		if (etudiant != null && program != null && getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					profileLayout.startLayoutAnimation();
					View v = getView();
					if (v != null) {
						String nom = etudiant.nom != null ? etudiant.nom.trim() : "";
						String prenom = etudiant.prenom != null ? etudiant.prenom.trim() : "";

						((TextView) v.findViewById(R.id.profil_nom_prenom_item)).setText(nom + ", " + prenom);
						((TextView) v.findViewById(R.id.profil_code_permanent_item)).setText(etudiant.codePerm);
						((TextView) v.findViewById(R.id.profil_solde_item)).setText(etudiant.soldeTotal);


						((TextView) v.findViewById(R.id.profil_programme_item)).setText(program.libelle);
						((TextView) v.findViewById(R.id.profil_credit_reussis_item))
								.setText(program.nbCreditsCompletes);
						((TextView) v.findViewById(R.id.profil_cours_reussis_item)).setText(program.nbCrsReussis);
						((TextView) v.findViewById(R.id.profil_credit_echoue_item)).setText(program.nbCrsEchoues);
						((TextView) v.findViewById(R.id.profil_credit_inscrit_item)).setText(program.nbCreditsInscrits);
						((TextView) v.findViewById(R.id.profil_cours_equivalent_item)).setText(program.nbEquivalences);
						((TextView) v.findViewById(R.id.profil_credit_potentiel_item))
								.setText(program.nbCreditsPotentiels);
						((TextView) v.findViewById(R.id.profil_moyenne_item)).setText(program.moyenne);
					}
				}
			});
		}
	}
}
