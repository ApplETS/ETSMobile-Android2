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

@DatabaseTable(tableName = "element_evaluation")
public class ElementEvaluation extends AttributeContainer implements KvmSerializable {

	@DatabaseField
	public String coursGroupe;

	@DatabaseField
	public String nom;

	@DatabaseField
	public String equipe;

	@DatabaseField
	public String dateCible;

	@DatabaseField
	public String note;

	@DatabaseField
	public String corrigeSur;

	@DatabaseField
	public String ponderation;

	@DatabaseField
	public String moyenne;

	@DatabaseField
	public String ecartType;

	@DatabaseField
	public String mediane;

	@DatabaseField
	public String rangCentile;

	@DatabaseField
	public String publie;

	@DatabaseField
	public String messageDuProf;

	@DatabaseField
	public String ignoreDuCalcul;

    @DatabaseField(id = true)
    public String id;

    @DatabaseField(foreign=true)
    public ListeDesElementsEvaluation listeDesElementsEvaluation;

	public ElementEvaluation() {
	}

	public ElementEvaluation(AttributeContainer inObj, ExtendedSoapSerializationEnvelope envelope) {

		if (inObj == null)
			return;

		SoapObject soapObject = (SoapObject) inObj;

		if (soapObject.hasProperty("coursGroupe")) {
			Object obj = soapObject.getProperty("coursGroupe");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					coursGroupe = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				coursGroupe = (String) obj;
			}
		}
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
		if (soapObject.hasProperty("equipe")) {
			Object obj = soapObject.getProperty("equipe");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					equipe = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				equipe = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateCible")) {
			Object obj = soapObject.getProperty("dateCible");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateCible = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateCible = (String) obj;
			}
		}
		if (soapObject.hasProperty("note")) {
			Object obj = soapObject.getProperty("note");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					note = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				note = (String) obj;
			}
		}
		if (soapObject.hasProperty("corrigeSur")) {
			Object obj = soapObject.getProperty("corrigeSur");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					corrigeSur = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				corrigeSur = (String) obj;
			}
		}
		if (soapObject.hasProperty("ponderation")) {
			Object obj = soapObject.getProperty("ponderation");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					ponderation = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				ponderation = (String) obj;
			}
		}
		if (soapObject.hasProperty("moyenne")) {
			Object obj = soapObject.getProperty("moyenne");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					moyenne = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				moyenne = (String) obj;
			}
		}
		if (soapObject.hasProperty("ecartType")) {
			Object obj = soapObject.getProperty("ecartType");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					ecartType = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				ecartType = (String) obj;
			}
		}
		if (soapObject.hasProperty("mediane")) {
			Object obj = soapObject.getProperty("mediane");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					mediane = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				mediane = (String) obj;
			}
		}
		if (soapObject.hasProperty("rangCentile")) {
			Object obj = soapObject.getProperty("rangCentile");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					rangCentile = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				rangCentile = (String) obj;
			}
		}
		if (soapObject.hasProperty("publie")) {
			Object obj = soapObject.getProperty("publie");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					publie = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				publie = (String) obj;
			}
		}
		if (soapObject.hasProperty("messageDuProf")) {
			Object obj = soapObject.getProperty("messageDuProf");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					messageDuProf = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				messageDuProf = (String) obj;
			}
		}
		if (soapObject.hasProperty("ignoreDuCalcul")) {
			Object obj = soapObject.getProperty("ignoreDuCalcul");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					ignoreDuCalcul = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				ignoreDuCalcul = (String) obj;
			}
		}

	}

	@Override
	public Object getProperty(int propertyIndex) {
		if (propertyIndex == 0) {
			return coursGroupe;
		}
		if (propertyIndex == 1) {
			return nom;
		}
		if (propertyIndex == 2) {
			return equipe;
		}
		if (propertyIndex == 3) {
			return dateCible;
		}
		if (propertyIndex == 4) {
			return note;
		}
		if (propertyIndex == 5) {
			return corrigeSur;
		}
		if (propertyIndex == 6) {
			return ponderation;
		}
		if (propertyIndex == 7) {
			return moyenne;
		}
		if (propertyIndex == 8) {
			return ecartType;
		}
		if (propertyIndex == 9) {
			return mediane;
		}
		if (propertyIndex == 10) {
			return rangCentile;
		}
		if (propertyIndex == 11) {
			return publie;
		}
		if (propertyIndex == 12) {
			return messageDuProf;
		}
		if (propertyIndex == 13) {
			return ignoreDuCalcul;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 14;
	}

	@Override
	public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		if (propertyIndex == +0) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "coursGroupe";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +1) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "nom";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +2) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "equipe";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +3) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateCible";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +4) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "note";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +5) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "corrigeSur";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +6) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "ponderation";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +7) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "moyenne";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +8) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "ecartType";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +9) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "mediane";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +10) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "rangCentile";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +11) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "publie";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +12) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "messageDuProf";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +13) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "ignoreDuCalcul";
			info.namespace = "http://etsmtl.ca/";
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
	}

}
