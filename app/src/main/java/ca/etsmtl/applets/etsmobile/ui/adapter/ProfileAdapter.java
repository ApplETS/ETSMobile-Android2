package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.Programme;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 10/08/15.
 */
public class ProfileAdapter extends BaseAdapter {

    private final Context context;
    private Etudiant etudiant = null;
    private ArrayList<Programme> listeDesProgrammes = null;
    private ProfileSpinnerAdapter profileSpinnerAdapter;

    public ProfileAdapter(Context context, Etudiant etudiant, ArrayList<Programme> listeDesProgrammes) {
        this.context = context;
        this.etudiant = etudiant;
        this.listeDesProgrammes = listeDesProgrammes;
        profileSpinnerAdapter = new ProfileSpinnerAdapter(context);
    }

    public void updateEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
        this.notifyDataSetChanged();
    }

    public void updateListeDesProgrammes(ArrayList<Programme> listeDesProgrammes) {
        if (!listeDesProgrammes.isEmpty()) {
            this.listeDesProgrammes = listeDesProgrammes;
            profileSpinnerAdapter.clear();
            profileSpinnerAdapter.addAll(listeDesProgrammes);
            this.notifyDataSetChanged();
            profileSpinnerAdapter.notifyDataSetChanged();
            profileSpinnerAdapter.setDropDownViewResource(R.layout.row_profile_spinner_dropdown);
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        count += etudiant == null ? 0 : 1;
        count += listeDesProgrammes == null ? 0 : 1;
        return count;
    }

    @Override
    public Object getItem(int position) {
        switch (position) {
            case 0:
                return etudiant;
            case 1:
                return listeDesProgrammes;
            default:
                return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if (position == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_profile_info_etudiant, parent, false);

                InfoEtudiantViewHolder infoEtudiantViewHolder = new InfoEtudiantViewHolder();
                infoEtudiantViewHolder.tvNomPrenom = (TextView) convertView.findViewById(R.id.profil_nom_prenom_item);
                infoEtudiantViewHolder.tvCodePermanent = (TextView) convertView.findViewById(R.id.profil_code_permanent_item);
                infoEtudiantViewHolder.tvSolde = (TextView) convertView.findViewById(R.id.profil_solde_item);
                infoEtudiantViewHolder.tvCodeUniversel = (TextView) convertView.findViewById(R.id.profil_code_universel_item);

                convertView.setTag(infoEtudiantViewHolder);

            } else if (position == 1) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_profile_liste_programmes, parent, false);

                ListProgramViewHolder listProgramViewHolder = new ListProgramViewHolder();
                listProgramViewHolder.spinnerProgram = (Spinner) convertView.findViewById(R.id.spinner_programmes);
                listProgramViewHolder.tvCoursEchoues = (TextView) convertView.findViewById(R.id.profil_credit_echoue_item);
                listProgramViewHolder.tvCoursEquivalents = (TextView) convertView.findViewById(R.id.profil_cours_equivalent_item);
                listProgramViewHolder.tvCoursReussis = (TextView) convertView.findViewById(R.id.profil_cours_reussis_item);
                listProgramViewHolder.tvCreditsInscrits = (TextView) convertView.findViewById(R.id.profil_credit_inscrit_item);
                listProgramViewHolder.tvCreditsReussis = (TextView) convertView.findViewById(R.id.profil_credit_reussis_item);
                listProgramViewHolder.tvMoyenne = (TextView) convertView.findViewById(R.id.profil_moyenne_item);


                convertView.setTag(listProgramViewHolder);
            }
        }

        if (position == 0) {
            InfoEtudiantViewHolder infoEtudiantViewHolder = (InfoEtudiantViewHolder) convertView.getTag();

            String nom = etudiant.nom != null ? etudiant.nom.trim() : "";
            String prenom = etudiant.prenom != null ? etudiant.prenom.trim() : "";
            infoEtudiantViewHolder.tvNomPrenom.setText(nom + ", " + prenom);

            infoEtudiantViewHolder.tvCodePermanent.setText(etudiant.codePerm);
            infoEtudiantViewHolder.tvSolde.setText(etudiant.soldeTotal);
            infoEtudiantViewHolder.tvCodeUniversel.setText(ApplicationManager.userCredentials.getUsername());

        } else if (position == 1) {

            final ListProgramViewHolder listProgramViewHolder = (ListProgramViewHolder) convertView.getTag();

            listProgramViewHolder.spinnerProgram.setAdapter(profileSpinnerAdapter);

            Programme programme = (Programme) listProgramViewHolder.spinnerProgram.getSelectedItem();

            listProgramViewHolder.tvMoyenne.setText(programme.moyenne);
            listProgramViewHolder.tvCreditsReussis.setText(programme.nbCreditsCompletes);
            listProgramViewHolder.tvCreditsInscrits.setText(programme.nbCreditsInscrits);
            listProgramViewHolder.tvCoursReussis.setText(programme.nbCrsReussis);
            listProgramViewHolder.tvCoursEquivalents.setText(programme.nbEquivalences);
            listProgramViewHolder.tvCoursEchoues.setText(programme.nbCrsEchoues);

            listProgramViewHolder.spinnerProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    notifyDataSetChanged();

                    Programme selectedProgramme = profileSpinnerAdapter.getItem(position);
                    listProgramViewHolder.tvMoyenne.setText(selectedProgramme.moyenne);
                    listProgramViewHolder.tvCreditsReussis.setText(selectedProgramme.nbCreditsCompletes);
                    listProgramViewHolder.tvCreditsInscrits.setText(selectedProgramme.nbCreditsInscrits);
                    listProgramViewHolder.tvCoursReussis.setText(selectedProgramme.nbCrsReussis);
                    listProgramViewHolder.tvCoursEquivalents.setText(selectedProgramme.nbEquivalences);
                    listProgramViewHolder.tvCoursEchoues.setText(selectedProgramme.nbCrsEchoues);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }


        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class InfoEtudiantViewHolder {
        TextView tvNomPrenom;
        TextView tvCodePermanent;
        TextView tvSolde;
        TextView tvCodeUniversel;
    }

    static class ListProgramViewHolder {
        Spinner spinnerProgram;
        TextView tvMoyenne;
        TextView tvCreditsReussis;
        TextView tvCreditsInscrits;
        TextView tvCoursReussis;
        TextView tvCoursEquivalents;
        TextView tvCoursEchoues;
    }
}
