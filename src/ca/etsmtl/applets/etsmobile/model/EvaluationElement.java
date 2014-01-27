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

import com.google.gson.annotations.SerializedName;

public class EvaluationElement implements Serializable {

	private static final long serialVersionUID = -8642324351015289340L;

	@SerializedName("coursGroupe")
	private String coursGroupe;

	@SerializedName("nom")
	private String nom;

	@SerializedName("equipe")
	private String equipe;

	@SerializedName("dateCible")
	private String dateCible;

	@SerializedName("note")
	private String note;

	@SerializedName("corrigeSur")
	private String corrigeSur;

	@SerializedName("ponderation")
	private String ponderation;

	@SerializedName("moyenne")
	private String moyenne;

	@SerializedName("ecartType")
	private String ecartType;

	@SerializedName("mediane")
	private String mediane;

	@SerializedName("rangCentile")
	private String rangCentile;

	@SerializedName("publie")
	private String publie;

	@SerializedName("messageDuProf")
	private String messageDuProf;

	@SerializedName("ignoreDuCalcul")
	private String ignoreDuCalcul;

	public String getCorrigeSur() {
		return corrigeSur;
	}

	public String getCoursGroupe() {
		return coursGroupe;
	}

	public String getDateCible() {
		return dateCible;
	}

	public String getEcartType() {
		return ecartType;
	}

	public String getEquipe() {
		return equipe;
	}

	public String getIgnoreDuCalcul() {
		return ignoreDuCalcul;
	}

	public String getMediane() {
		return mediane;
	}

	public String getMessageDuProf() {
		return messageDuProf;
	}

	public String getMoyenne() {
		return moyenne;
	}

	public String getNom() {
		return nom;
	}

	public String getNote() {
		return note;
	}

	public String getPonderation() {
		return ponderation;
	}

	public String getPublie() {
		return publie;
	}

	public String getRangCentile() {
		return rangCentile;
	}

	public void setCorrigeSur(final String corrigeSur) {
		this.corrigeSur = corrigeSur;
	}

	public void setCoursGroupe(final String coursGroupe) {
		this.coursGroupe = coursGroupe;
	}

	public void setDateCible(final String dateCible) {
		this.dateCible = dateCible;
	}

	public void setEcartType(final String ecartType) {
		this.ecartType = ecartType;
	}

	public void setEquipe(final String equipe) {
		this.equipe = equipe;
	}

	public void setIgnoreDuCalcul(final String ignoreDuCalcul) {
		this.ignoreDuCalcul = ignoreDuCalcul;
	}

	public void setMediane(final String mediane) {
		this.mediane = mediane;
	}

	public void setMessageDuProf(final String messageDuProf) {
		this.messageDuProf = messageDuProf;
	}

	public void setMoyenne(final String moyenne) {
		this.moyenne = moyenne;
	}

	public void setNom(final String nom) {
		this.nom = nom;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setPonderation(final String ponderation) {
		this.ponderation = ponderation;
	}

	public void setPublie(final String publie) {
		this.publie = publie;
	}

	public void setRangCentile(final String rangCentile) {
		this.rangCentile = rangCentile;
	}

	@Override
	public String toString() {
		return nom + " - " + note;
	}
}
