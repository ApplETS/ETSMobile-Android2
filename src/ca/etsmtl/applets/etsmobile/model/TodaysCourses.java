package ca.etsmtl.applets.etsmobile.model;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TodaysCourses {
	public TodaysCourses() {
		// TODO Auto-generated constructor stub
	}

	@JsonProperty
	public String codePerm = "DAVP02068904";
	@JsonProperty
	public String erreur = null;
	@JsonProperty
	public boolean masculin = true;
	@JsonProperty
	public String nom = "David ";
	@JsonProperty
	public String prenom = "Philippe ";
	@JsonProperty
	public String programme = "Baccalauréat en génie logiciel";
	@JsonProperty
	public String soldeTotal = "0,00$";
	@JsonProperty
	public ArrayList<Seance> horaire = new ArrayList<Seance>();

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Seance {
		public Seance() {
		}

		@JsonProperty
		public String coursGroupe = "GTI745-01 ";
		@JsonProperty
		public String dateDebut = "2014-04-09T09=00:00";
		@JsonProperty
		public String dateFin = "2014-04-09T12:00:00";
		@JsonProperty
		public String descriptionActivite = "Examen final";
		@JsonProperty
		public String libelleCours = "Interfaces utilisateurs avancées";
		@JsonProperty
		public String local = "A-1600B";
		@JsonProperty
		public String nomActivite = "Final";
		@JsonProperty
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