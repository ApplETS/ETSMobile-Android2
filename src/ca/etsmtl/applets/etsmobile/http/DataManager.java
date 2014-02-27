package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ca.etsmtl.applets.etsmobile.http.soap.SignetsMobileSoap;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Singleton to get data from HTTP/SOAP request
 * 
 * @author Phil
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataManager {

	private static DataManager instance;
	private SpiceManager spiceManager;
	private static Context c;

	private DataManager() {
		spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
	}

	public static DataManager getInstance(Context c) {
		DataManager.c = c;
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	/**
	 * Robospice request manager for everything not related to Signet-Mobile the
	 * class (result) must be mapped with Gson
	 * 
	 * @param request
	 * @param listener
	 * @return true if request is sent
	 */
	public boolean sendRequest(TypedRequest request, RequestListener<Object> listener) {

		final Object key = request.createCacheKey();
		spiceManager.execute(request, key, DurationInMillis.ONE_SECOND, listener);
		return true;
	}

	/**
	 * Send a request to Signet-Mobile Web Service
	 * 
	 * @param method
	 *            Int, methods are stored in {@link SignetMethod}
	 * @param creds
	 *            User Credentials, some methods require more than u/p
	 * @param listener
	 *            The callback
	 * @return An Object
	 */
	public void getDataFromSignet(int method, final UserCredentials creds,
			final RequestListener<Object> listener, String... params) {

		// inline asynctask
		new AsyncTask<Object, Void, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				try {

					final int methodID = (Integer) params[0];
					String[] reqParams = (String[]) params[1];
					Object result;

					final SignetsMobileSoap signetsMobileSoap = new SignetsMobileSoap();
					String username = creds.getUsername();
					String password = creds.getPassword();
					switch (methodID) {
					case SignetMethods.INFO_ETUDIANT:

						result = signetsMobileSoap.infoEtudiant(username, password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_COURS:
						result = signetsMobileSoap.listeCours(username, password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_INT_SESSION:

						String SesFin = reqParams[0];
						String SesDebut = reqParams[1];

						result = signetsMobileSoap.listeCoursIntervalleSessions(username, password,
								SesDebut, SesFin);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_SESSION:

						result = signetsMobileSoap.listeSessions(username, password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_PROGRAM:

						result = signetsMobileSoap.listeProgrammes(username, password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_COEQ:

						String pNomElementEval = reqParams[0];
						String pSession = reqParams[1];
						String pGroupe = reqParams[2];
						String pSigle = reqParams[3];
						result = signetsMobileSoap.listeCoequipiers(username, password, pSigle,
								pGroupe, pSession, pNomElementEval);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_EVAL:

						String pSession1 = reqParams[0];
						String pGroupe1 = reqParams[1];
						String pSigle1 = reqParams[2];
						result = signetsMobileSoap.listeElementsEvaluation(username, password,
								pSigle1, pGroupe1, pSession1);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_HORAIRE_PROF:

						String pSession2 = reqParams[0];

						result = signetsMobileSoap
								.listeHoraireEtProf(username, password, pSession2);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIRE_HORAIRE:

						String pSession3 = reqParams[0];
						String prefixeSigleCours = reqParams[1];

						result = signetsMobileSoap.lireHoraire(pSession3, prefixeSigleCours);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIRE_JOUR_REMPLACE:

						String pSession4 = reqParams[0];

						result = signetsMobileSoap.lireJoursRemplaces(pSession4);

						listener.onRequestSuccess(result);
						break;

					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute(method, params);
	}

	/**
	 * Method mapping for Signets-Mobile
	 * 
	 * @author Phil
	 * 
	 */
	public static class SignetMethods {
		public static final int INFO_ETUDIANT = 0;
		public static final int LIST_COURS = 3;
		public static final int LIST_INT_SESSION = 4;
		public static final int LIST_SESSION = 2;
		public static final int LIST_PROGRAM = 6;
		public static final int LIST_COEQ = 5;
		public static final int LIST_EVAL = 1;
		public static final int LIST_HORAIRE_PROF = 7;
		public static final int LIRE_HORAIRE = 8;
		public static final int LIRE_JOUR_REMPLACE = 9;
	}

	/**
	 * Convinience method to login a user
	 * 
	 * @param userCredentials
	 * @param listener
	 */
	public void login(UserCredentials userCredentials, RequestListener<Object> listener) {
		getDataFromSignet(SignetMethods.INFO_ETUDIANT, userCredentials, listener);
	}

	public void start() {
		if (!spiceManager.isStarted())
			spiceManager.start(c);
	}

	public void stop() {
		if (!spiceManager.isStarted())
			spiceManager.shouldStop();
	}

}
