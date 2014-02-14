package ca.etsmtl.applets.etsmobile.http;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ca.etsmtl.applets.etsmobile.model.StudentProfile;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile2.R;

import com.google.android.gms.internal.cr;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataManager {

	private static String BASE_URL = "";
	private static final String NAMESPACE = "http://etsmtl.ca/";
	private static DataManager instance;
	private SpiceManager spiceManager;

	private DataManager() {
		spiceManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
	}

	public static DataManager getInstance(Context c) {
		if (instance == null) {
			BASE_URL = c.getString(R.string.ets_signets);
			instance = new DataManager();
		}
		return instance;
	}

	public boolean sendRequest(TypedRequest request, RequestListener<Object> listener) {

		final Object key = request.createCacheKey();
		spiceManager.execute(request, key, DurationInMillis.ONE_MINUTE, listener);
		return true;
	}

	public SoapPrimitive getDataFromSignet(String method, String soap_action,
			UserCredentials creds, RequestListener<Object> listener) {

		new AsyncTask<Object, Void, Object>() {
			@Override
			protected Object doInBackground(Object... objs) {
				String method = (String) objs[0], soap_action = (String) objs[1];
				UserCredentials creds = (UserCredentials) objs[2];
				RequestListener<Object> listener = (RequestListener<Object>) objs[3];
				try {

					SoapObject request = new SoapObject(NAMESPACE, method);

					request.addProperty(UserCredentials.CODE_U, creds.getUsername());
					request.addProperty(UserCredentials.CODE_P, creds.getPassword());

					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.dotNet = true;
					envelope.setOutputSoapObject(request);

					HttpTransportSE androidHttpTransport = new HttpTransportSE(BASE_URL);
					androidHttpTransport.debug = true;
					androidHttpTransport.call(soap_action, envelope);
					if (envelope.bodyIn instanceof SoapFault) {
						String str = ((SoapFault) envelope.bodyIn).faultstring;
						Log.i("SoapFault", str);

						// Another way to travers through the SoapFault object
						/*
						 * Node detailsString =str= ((SoapFault)
						 * envelope.bodyIn).detail; Element detailElem =
						 * (Element) details.getElement(0) .getChild(0); Element
						 * e = (Element) detailElem.getChild(2);faultstring;
						 * Log.i("", e.getName() + " " + e.getText(0)str);
						 */
					} else {
						SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
						Log.d("WS", String.valueOf(resultsRequestSOAP));
						listener.onRequestSuccess(resultsRequestSOAP);
					}

					return null;
				} catch (Exception e) {
					e.printStackTrace();
					listener.onRequestFailure(new SpiceException(e.getMessage()));
				}

				return null;
			}
		}.execute(method, soap_action, creds, listener);
		return null;
	}

	public static class SignetActions {
		public static final String ListEvaluation = "http://etsmtl.ca/listeElementsEvaluation";
		public static final String LoginAction = "http://etsmtl.ca/infoEtudiant";
	}

	public static class SignetMethods {
		public static final String ListEvaluation = "listeElementsEvaluation";
		public static final String Login = "infoEtudiant";
	}

	public void login(UserCredentials userCredentials, RequestListener<Object> listener) {
		getDataFromSignet(SignetMethods.Login, SignetActions.LoginAction, userCredentials, listener);
	}

}
