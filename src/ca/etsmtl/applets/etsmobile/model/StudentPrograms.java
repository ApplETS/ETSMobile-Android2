/*******************************************************************************
 * Copyright 2013 Club ApplETS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ca.etsmtl.applets.etsmobile.model;

import java.io.Serializable;

public class StudentPrograms implements Serializable {

	private static final long serialVersionUID = 7328350494569964493L;

	private String code;
	private String libelle;
	private String profil;
	private String statut;
	private String sessionDebut;
	private String sessionFin;
	private String moyenne;
	private String nbEquivalences;
	private String nbCrsReussis;
	private String nbCrsEchoues;
	private String nbCreditsInscrits;
	private String nbCreditsCompletes;
	private String nbCreditsPotentiels;
	private String nbCreditsRecherche;

	public StudentPrograms() {
	}

	public StudentPrograms(String code, String libelle, String profil,
			String statut, String sessionDebut, String sessionFin,
			String moyenne, String nbEquivalences, String nbCrsReussis,
			String nbCrsEchoues, String nbCreditsInscrits,
			String nbCreditsCompletes, String nbCreditsPotentiels,
			String nbCreditsRecherche) {
		super();
		this.code = code;
		this.libelle = libelle;
		this.profil = profil;
		this.statut = statut;
		this.sessionDebut = sessionDebut;
		this.sessionFin = sessionFin;
		this.moyenne = moyenne;
		this.nbEquivalences = nbEquivalences;
		this.nbCrsReussis = nbCrsReussis;
		this.nbCrsEchoues = nbCrsEchoues;
		this.nbCreditsInscrits = nbCreditsInscrits;
		this.nbCreditsCompletes = nbCreditsCompletes;
		this.nbCreditsPotentiels = nbCreditsPotentiels;
		this.nbCreditsRecherche = nbCreditsRecherche;
	}

	public String getCode() {
		return code;
	}

	public String getLibelle() {
		return libelle;
	}

	public String getProfil() {
		return profil;
	}

	public String getStatut() {
		return statut;
	}

	public String getSessionDebut() {
		return sessionDebut;
	}

	public String getSessionFin() {
		return sessionFin;
	}

	public String getMoyenne() {
		return moyenne;
	}

	public String getNbEquivalences() {
		return nbEquivalences;
	}

	public String getNbCrsReussis() {
		return nbCrsReussis;
	}

	public String getNbCrsEchoues() {
		return nbCrsEchoues;
	}

	public String getNbCreditsInscrits() {
		return nbCreditsInscrits;
	}

	public String getNbCreditsCompletes() {
		return nbCreditsCompletes;
	}

	public String getNbCreditsPotentiels() {
		return nbCreditsPotentiels;
	}

	public String getNbCreditsRecherche() {
		return nbCreditsRecherche;
	}

}
