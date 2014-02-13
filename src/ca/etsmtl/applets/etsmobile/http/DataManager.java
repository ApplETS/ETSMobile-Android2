package ca.etsmtl.applets.etsmobile.http;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
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

	public SoapPrimitive getDataFromSignet(String method, String soap_action, UserCredentials creds) {
		try {

			SoapObject request = new SoapObject(NAMESPACE, method);

			request.addProperty(UserCredentials.CODE_U, creds.getUsername());
			request.addProperty(UserCredentials.CODE_P, creds.getPassword());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(BASE_URL);
			androidHttpTransport.call(soap_action, envelope);
			SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

			// to get the data
			String resultData = result.toString();
			// 0 is the first object of data

			// sb.append(resultData + "\n");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			// sb.append("Error:\n" + e.getMessage() + "\n");
		}
		return null;

	}

	public static class SignetActions {
		public static final String ListEvaluation = "http://etsmtl.ca/listeElementsEvaluation";
	}

	public static class SignetMethods {
		public static final String ListEvaluation = "listeElementsEvaluation";
	}

}
