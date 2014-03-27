package ca.etsmtl.applets.etsmobile.http;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import android.util.Log;
import ca.etsmtl.applets.etsmobile.http.soap.SignetsMobileSoap;
import ca.etsmtl.applets.etsmobile.http.soap.WebServiceSoap;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
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
	private DatabaseHelper dbHelper;
	private static Context c;

	private DataManager() {
		spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
		dbHelper = new DatabaseHelper(c);
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
	public boolean sendRequest(TypedRequest request,
			RequestListener<Object> listener) {

		final Object key = request.createCacheKey();
		spiceManager.execute(request, key, DurationInMillis.ONE_SECOND,
				listener);
		return true;
	}

	/**
	 * Send a request to Signet-Mobile Web Service
	 * 
	 * @param method
	 *            Int, methods are stored in {@link SignetMethod}
	 * @param creds
	 *            User Credentials
	 * @param listener
	 *            The callback
	 * @param params
	 *            Some methods require more than the credentials pass them here
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

						result = signetsMobileSoap.infoEtudiant(username,
								password);
						dbHelper.getDao(Etudiant.class).createOrUpdate(
								(Etudiant) result);
						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_COURS:
						result = signetsMobileSoap.listeCours(username, password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_INT_SESSION:

						String SesFin = reqParams[0];
						String SesDebut = reqParams[1];

						result = signetsMobileSoap
								.listeCoursIntervalleSessions(username,
										password, SesDebut, SesFin);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_SESSION:

						result = signetsMobileSoap.listeSessions(username,
								password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_PROGRAM:

						result = signetsMobileSoap.listeProgrammes(username,
								password);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_COEQ:

						String pNomElementEval = reqParams[0];
						String pSession = reqParams[1];
						String pGroupe = reqParams[2];
						String pSigle = reqParams[3];
						result = signetsMobileSoap.listeCoequipiers(username,
								password, pSigle, pGroupe, pSession,
								pNomElementEval);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_EVAL:

						String pSession1 = reqParams[0];
						String pGroupe1 = reqParams[1];
						String pSigle1 = reqParams[2];
						result = signetsMobileSoap.listeElementsEvaluation(
								username, password, pSigle1, pGroupe1,
								pSession1);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIST_HORAIRE_PROF:

						String pSession2 = reqParams[0];

						result = signetsMobileSoap.listeHoraireEtProf(username,
								password, pSession2);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIRE_HORAIRE:

						String pSession3 = reqParams[0];
						String prefixeSigleCours = reqParams[1];

						result = signetsMobileSoap.lireHoraire(pSession3,
								prefixeSigleCours);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.LIRE_JOUR_REMPLACE:

						String pSession4 = reqParams[0];

						result = signetsMobileSoap
								.lireJoursRemplaces(pSession4);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.BOTTIN_LIST_DEPT:
						result = new WebServiceSoap().GetListeDepartement();

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.BOTTIN_GET_FICHE:
						String numero = reqParams[0];
						String PathFiche = reqParams[1];
						result = new WebServiceSoap().GetFiche(numero,
								PathFiche);

						listener.onRequestSuccess(result);
						break;

					case SignetMethods.BOTTIN_GET_FICHE_DATA:
						String Id = reqParams[0];
						result = new WebServiceSoap().GetFicheData(Id);

						listener.onRequestSuccess(result);
						break;
					case SignetMethods.BOTTIN_GET_ALL:
						result = new WebServiceSoap().Recherche(null, null,
								null);

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
		public static final int BOTTIN_LIST_DEPT = 10;
		public static final int BOTTIN_GET_FICHE = 11;
		public static final int BOTTIN_GET_FICHE_DATA = 12;
		public static final int BOTTIN_GET_ALL = 13;
	}

	/**
	 * Convinience method to login a user
	 * 
	 * @param userCredentials
	 * @param listener
	 */
	public void login(UserCredentials userCredentials,
			RequestListener<Object> listener) {
		getDataFromSignet(SignetMethods.INFO_ETUDIANT, userCredentials,
				listener);
	}

	public void start() {
		if (!spiceManager.isStarted())
			spiceManager.start(c);
	}

	public void stop() {
		if (!spiceManager.isStarted())
			spiceManager.shouldStop();
	}

	/**
	 * @return the first registered {@link Etudiant} or null if none is
	 *         registered
	 * @throws SQLException
	 */
	public Etudiant getRegisteredEtudiant() throws SQLException {

		final List<Etudiant> queryForAll = dbHelper.getDao(Etudiant.class)
				.queryForAll();
		if (queryForAll.size() > 0)
			return queryForAll.get(0);
		return null;
	}

}
