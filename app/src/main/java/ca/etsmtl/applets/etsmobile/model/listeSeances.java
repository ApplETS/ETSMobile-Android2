package ca.etsmtl.applets.etsmobile.model;

//----------------------------------------------------
//
//Generated by www.easywsdl.com
//Version: 4.0.1.0
//
//Created by Quasar Development at 03-09-2014
//
//---------------------------------------------------


import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;

import ca.etsmtl.applets.etsmobile.http.soap.ExtendedSoapSerializationEnvelope;

public class listeSeances extends DonneesRetournees implements KvmSerializable
{
  
  public ArrayOfSeances ListeDesSeances=new ArrayOfSeances();

  public listeSeances ()
  {
  }

  public listeSeances (AttributeContainer inObj, ExtendedSoapSerializationEnvelope envelope)
  {
	    super(inObj, envelope);
	    if (inObj == null)
          return;


      SoapObject soapObject=(SoapObject)inObj;  
      if (soapObject.hasProperty("ListeDesSeances"))
      {	
	        SoapObject j = (SoapObject) soapObject.getProperty("ListeDesSeances");
	        this.ListeDesSeances = new ArrayOfSeances(j,envelope);
      }


  }

  @Override
  public Object getProperty(int propertyIndex) {
      int count = super.getPropertyCount();
      if(propertyIndex==count+0)
      {
          return ListeDesSeances;
      }
      return super.getProperty(propertyIndex);
  }


  @Override
  public int getPropertyCount() {
      return super.getPropertyCount()+1;
  }

  @Override
  public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
  {
      int count = super.getPropertyCount();
      if(propertyIndex==count+0)
      {
          info.type = PropertyInfo.VECTOR_CLASS;
          info.name = "ListeDesSeances";
          info.namespace= "http://etsmtl.ca/";
      }
      super.getPropertyInfo(propertyIndex,arg1,info);
  }
  
  @Override
  public void setProperty(int arg0, java.lang.Object arg1)
  {
  }

}