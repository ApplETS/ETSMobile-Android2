package ca.etsmtl.applets.etsmobile.model;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 2.0.3.1
//
// Created by Quasar Development at 03-03-2014
//
//---------------------------------------------------

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.Serializable;
import java.util.Hashtable;

import ca.etsmtl.applets.etsmobile.http.soap.ExtendedSoapSerializationEnvelope;
import ca.etsmtl.applets.etsmobile.http.soap.Helper;

@DatabaseTable(tableName = "fiche_employee")
public class FicheEmploye extends AttributeContainer implements KvmSerializable,Serializable {

	@DatabaseField(id = true)
	public Integer Id = 0;

	@DatabaseField
	public String Nom;

	@DatabaseField
	public String Prenom;

	@DatabaseField
	public String TelBureau;

	@DatabaseField
	public String Emplacement;

	@DatabaseField
	public String Courriel;

	@DatabaseField
	public String Service;

	@DatabaseField
	public String Titre;

	@DatabaseField
	public DateTime DateModif;

	public FicheEmploye() {
	}

	public FicheEmploye(AttributeContainer inObj, ExtendedSoapSerializationEnvelope envelope) {

		if (inObj == null)
			return;

		SoapObject soapObject = (SoapObject) inObj;

		if (soapObject.hasProperty("Id")) {
			Object obj = soapObject.getProperty("Id");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Id = Integer.parseInt(j.toString());
				}
			} else if (obj != null && obj instanceof Integer) {
				this.Id = (Integer) obj;
			}
		}
		if (soapObject.hasProperty("Nom")) {
			Object obj = soapObject.getProperty("Nom");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Nom = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.Nom = (String) obj;
			}
		}
		if (soapObject.hasProperty("Prenom")) {
			Object obj = soapObject.getProperty("Prenom");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Prenom = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.Prenom = (String) obj;
			}
		}
		if (soapObject.hasProperty("TelBureau")) {
			Object obj = soapObject.getProperty("TelBureau");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.TelBureau = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.TelBureau = (String) obj;
			}
		}
		if (soapObject.hasProperty("Emplacement")) {
			Object obj = soapObject.getProperty("Emplacement");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Emplacement = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.Emplacement = (String) obj;
			}
		}
		if (soapObject.hasProperty("Courriel")) {
			Object obj = soapObject.getProperty("Courriel");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Courriel = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.Courriel = (String) obj;
			}
		}
		if (soapObject.hasProperty("Service")) {
			Object obj = soapObject.getProperty("Service");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Service = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.Service = (String) obj;
			}
		}
		if (soapObject.hasProperty("Titre")) {
			Object obj = soapObject.getProperty("Titre");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.Titre = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				this.Titre = (String) obj;
			}
		}
		if (soapObject.hasProperty("DateModif")) {
			Object obj = soapObject.getProperty("DateModif");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					this.DateModif = Helper.ConvertFromWebService(j.toString());
				}
			} else if (obj != null && obj instanceof DateTime) {
				this.DateModif = (DateTime) obj;
			}
		}

	}

	@Override
	public Object getProperty(int propertyIndex) {
		if (propertyIndex == 0) {
			return Id;
		}
		if (propertyIndex == 1) {
			return Nom;
		}
		if (propertyIndex == 2) {
			return Prenom;
		}
		if (propertyIndex == 3) {
			return TelBureau;
		}
		if (propertyIndex == 4) {
			return Emplacement;
		}
		if (propertyIndex == 5) {
			return Courriel;
		}
		if (propertyIndex == 6) {
			return Service;
		}
		if (propertyIndex == 7) {
			return Titre;
		}
		if (propertyIndex == 8) {
			return DateModif;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 9;
	}

	@Override
	public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		if (propertyIndex == +0) {
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "Id";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +1) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Nom";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +2) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Prenom";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +3) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "TelBureau";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +4) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Emplacement";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +5) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Courriel";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +6) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Service";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +7) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Titre";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +8) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "DateModif";
			info.namespace = "http://etsmtl.ca/";
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
	}

}
