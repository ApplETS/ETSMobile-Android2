package ca.etsmtl.applets.etsmobile.model;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 2.0.0.4
//
// Created by Quasar Development at 15-01-2014
//
//---------------------------------------------------

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;

import ca.etsmtl.applets.etsmobile.http.soap.ExtendedSoapSerializationEnvelope;

@DatabaseTable(tableName = "etudiant")
public class Etudiant extends DonneesRetournees implements KvmSerializable {

	@DatabaseField
	public String username;
	
	@DatabaseField
	public String nom;

	@DatabaseField
	public String prenom;

	@DatabaseField(id = true)
	public String codePerm;

	@DatabaseField
	public String soldeTotal;

    @DatabaseField
    public String status;

	public Etudiant() {
	}

	public Etudiant(AttributeContainer inObj, ExtendedSoapSerializationEnvelope envelope) {
		super(inObj, envelope);
		if (inObj == null)
			return;

		SoapObject soapObject = (SoapObject) inObj;

		if (soapObject.hasProperty("nom")) {
			Object obj = soapObject.getProperty("nom");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					nom = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				nom = (String) obj;
			}
		}
		if (soapObject.hasProperty("prenom")) {
			Object obj = soapObject.getProperty("prenom");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					prenom = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				prenom = (String) obj;
			}
		}
		if (soapObject.hasProperty("codePerm")) {
			Object obj = soapObject.getProperty("codePerm");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					codePerm = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				codePerm = (String) obj;
			}
		}
		if (soapObject.hasProperty("soldeTotal")) {
			Object obj = soapObject.getProperty("soldeTotal");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					soldeTotal = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				soldeTotal = (String) obj;
			}
		}

	}

	@Override
	public Object getProperty(int propertyIndex) {
		int count = super.getPropertyCount();
		if (propertyIndex == count) {
			return nom;
		}
		if (propertyIndex == count + 1) {
			return prenom;
		}
		if (propertyIndex == count + 2) {
			return codePerm;
		}
		if (propertyIndex == count + 3) {
			return soldeTotal;
		}
		return super.getProperty(propertyIndex);
	}

	@Override
	public int getPropertyCount() {
		return super.getPropertyCount() + 4;
	}

	@Override
	public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		int count = super.getPropertyCount();
		if (propertyIndex == count) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "nom";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == count + 1) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "prenom";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == count + 2) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "codePerm";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == count + 3) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "soldeTotal";
			info.namespace = "http://etsmtl.ca/";
		}
		super.getPropertyInfo(propertyIndex, arg1, info);
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
	}

}
