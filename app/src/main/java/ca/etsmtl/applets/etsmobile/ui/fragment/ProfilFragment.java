package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;

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
	private Etudiant etudiant = null;
	private listeDesProgrammes mlisteDesProgrammes;
    private ProfilManager profilManager;
	private TextView tvNomPrenom;
	private TextView tvMoyenne;
	private TextView tvCodePermanent;
	private TextView tvSolde;
	private TextView tvProgramme;
	private TextView tvCreditReussis;
	private TextView tvCreditEchoue;
	private TextView tvCoursReussis;
	private TextView tvCreditInscrit;
	private TextView tvCoursEquivalent;
	private TextView tvCreditPotentiel;

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


		tvNomPrenom = (TextView) v.findViewById(R.id.profil_nom_prenom_item);
		tvCodePermanent = (TextView) v.findViewById(R.id.profil_code_permanent_item);
		tvSolde = (TextView) v.findViewById(R.id.profil_solde_item);
		tvProgramme = (TextView) v.findViewById(R.id.profil_programme_item);
		tvCreditReussis = (TextView) v.findViewById(R.id.profil_credit_reussis_item);
		tvCoursReussis = (TextView) v.findViewById(R.id.profil_cours_reussis_item);
		tvCreditEchoue = (TextView) v.findViewById(R.id.profil_credit_echoue_item);
		tvCreditInscrit = (TextView) v.findViewById(R.id.profil_credit_inscrit_item);
		tvCoursEquivalent = (TextView) v.findViewById(R.id.profil_cours_equivalent_item);
        tvCreditPotentiel = (TextView) v.findViewById(R.id.profil_credit_potentiel_item);
		tvMoyenne = (TextView) v.findViewById(R.id.profil_moyenne_item);

		profilManager = new ProfilManager(getActivity());

		refreshUi();
		
		loadingView.showLoadingView();
		dataManager.getDataFromSignet(SignetMethods.INFO_ETUDIANT, ApplicationManager.userCredentials, this, "");
		dataManager.getDataFromSignet(SignetMethods.LIST_PROGRAM, ApplicationManager.userCredentials, this, "");
		return v;
	}

	@Override
	void updateUI() {

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

	@Override
	public void onRequestFailure(SpiceException e) {
		loadingView.hideProgessBar();
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
                if (p.statut.equals("actif") || p.statut.equals("tutelle")) {
                    profilManager.updateProgramme(p);
                    tempProgram = profilManager.getProgramme();
                }
            }
        }

        final Programme program = tempProgram;
        
		if (etudiant != null && program != null && getActivity() != null) {

			profileLayout.startLayoutAnimation();
			String nom = etudiant.nom != null ? etudiant.nom.trim() : "";
			String prenom = etudiant.prenom != null ? etudiant.prenom.trim() : "";

			tvNomPrenom.setText(nom + ", " + prenom);
			tvCodePermanent.setText(etudiant.codePerm);
			tvSolde.setText(etudiant.soldeTotal);
			tvProgramme.setText(program.libelle);
			tvCreditReussis.setText(program.nbCreditsCompletes);
			tvCoursReussis.setText(program.nbCrsReussis);
			tvCreditEchoue.setText(program.nbCrsEchoues);
			tvCreditInscrit.setText(program.nbCreditsInscrits);
			tvCoursEquivalent.setText(program.nbEquivalences);
			tvCreditPotentiel.setText(program.nbCreditsPotentiels);
			tvMoyenne.setText(program.moyenne);
		}



	}
}
