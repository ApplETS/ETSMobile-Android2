package ca.etsmtl.applets.etsmobile.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "today")
public class TodaysCourses {

	public TodaysCourses() {
		// required by jackson
	}

	@JsonProperty
	@DatabaseField
	public String codePerm = "DAVP02068904";

	@JsonProperty
	@DatabaseField
	public String erreur = null;

	@JsonProperty
	@DatabaseField
	public boolean masculin = true;

	@JsonProperty
	@DatabaseField
	public String nom = "David ";

	@JsonProperty
	@DatabaseField
	public String prenom = "Philippe ";

	@JsonProperty
	@DatabaseField
	public String programme = "Baccalauréat en génie logiciel";

	@JsonProperty
	@DatabaseField
	public String soldeTotal = "0,00$";

	@JsonProperty
	public ArrayList<Seance> horaire = new ArrayList<Seance>();

	@DatabaseField(generatedId = true)
	public int horaire_id = -1;

	@JsonIgnoreProperties(ignoreUnknown = true)
	@DatabaseTable(tableName = "seance")
	public static class Seance {
		public Seance() {
			// required by jackson
		}

		public int horaire_id;
		
		@JsonProperty
		@DatabaseField
		public String coursGroupe = "GTI745-01 ";

		@JsonProperty
		@DatabaseField
		public String dateDebut = "2014-04-09T09=00:00";

		@JsonProperty
		@DatabaseField
		public String dateFin = "2014-04-09T12:00:00";

		@JsonProperty
		@DatabaseField
		public String descriptionActivite = "Examen final";

		@JsonProperty
		@DatabaseField
		public String libelleCours = "Interfaces utilisateurs avancées";

		@JsonProperty
		@DatabaseField
		public String local = "A-1600B";

		@JsonProperty
		@DatabaseField
		public String nomActivite = "Final";

		@JsonProperty
		@DatabaseField
		public String plageHoraire = "09:00-12:00";
	}

	public String getCodePerm() {
		return codePerm;
	}

	public void setCodePerm(String codePerm) {
		this.codePerm = codePerm;
	}

	public String getErreur() {
		return erreur;
	}

	public void setErreur(String erreur) {
		this.erreur = erreur;
	}

	public ArrayList<Seance> getHoraire() {
		return horaire;
	}

	public void setHoraire(ArrayList<Seance> horaire) {
		this.horaire = horaire;
	}

	public boolean isMasculin() {
		return masculin;
	}

	public void setMasculin(boolean masculin) {
		this.masculin = masculin;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getProgramme() {
		return programme;
	}

	public void setProgramme(String programme) {
		this.programme = programme;
	}

	public String getSoldeTotal() {
		return soldeTotal;
	}

	public void setSoldeTotal(String soldeTotal) {
		this.soldeTotal = soldeTotal;
	}

}