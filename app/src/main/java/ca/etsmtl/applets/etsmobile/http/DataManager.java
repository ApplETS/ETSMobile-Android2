package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.os.AsyncTask;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.soap.SignetsMobileSoap;
import ca.etsmtl.applets.etsmobile.http.soap.WebServiceSoap;
import ca.etsmtl.applets.etsmobile.model.ArrayOfFicheEmploye;
import ca.etsmtl.applets.etsmobile.model.ArrayOfService;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Service;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.model.listeHoraireExamensFinaux;
import ca.etsmtl.applets.etsmobile.model.listeJoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.listeSeances;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataManager {

	private static final String TAG = "DataManager::";
	private static DataManager instance;
	private SpiceManager spiceManager;
	private DatabaseHelper dbHelper;
	private static Context c;
	private List<AsyncTask<Object, Void, Object>> tasks = new ArrayList<>();

	private DataManager() {
		spiceManager = new SpiceManager(MyJackSpringAndroidSpiceService.class);
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
	// public boolean sendRequest(TypedRequest request, RequestListener<Object>
	// listener) {
	//
	// final Object key = request.createCacheKey();
	// spiceManager.execute(request, key, DurationInMillis.ONE_SECOND,
	// listener);
	// return true;
	// }

	/**
	 * Send a request to Signet-Mobile Web Service
	 * 
	 * @param method
	 *            Int, methods are stored in SignetMethod
	 * @param creds
	 *            User Credentials
	 * @param listener
	 *            The callback
	 * @param params
	 *            Some methods require more than the credentials pass them here
	 */
	public void getDataFromSignet(int method, final UserCredentials creds, final RequestListener<Object> listener,
			String... params) {

		// inline asynctask
		final AsyncTask<Object, Void, Object> task = new AsyncTask<Object, Void, Object>() {
			
			private Exception exception = null;
			private Object result;
			
			@Override
			protected Object doInBackground(Object... params) {
				try {

					final int methodID = (Integer) params[0];
					String[] reqParams = (String[]) params[1];
					

					final SignetsMobileSoap signetsMobileSoap = new SignetsMobileSoap();

					String username = "";
					String password = "";

					if(methodID < SignetMethods.BOTTIN_LIST_DEPT || methodID > SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP ) {
						username = creds.getUsername();
						password = creds.getPassword();
					}
					switch (methodID) {
					case SignetMethods.INFO_ETUDIANT:

						result = signetsMobileSoap.infoEtudiant(username, password);
						dbHelper.getDao(Etudiant.class).createOrUpdate((Etudiant) result);

						break;
					case SignetMethods.LIST_COURS:
						result = signetsMobileSoap.listeCours(username, password);
						break;
					case SignetMethods.LIST_INT_SESSION:

						String SesFin = reqParams[0];
						String SesDebut = reqParams[1];

						result = signetsMobileSoap.listeCoursIntervalleSessions(username, password, SesDebut, SesFin);

						break;
					case SignetMethods.LIST_SESSION:

						result = signetsMobileSoap.listeSessions(username, password);

						break;
					case SignetMethods.LIST_PROGRAM:

						result = signetsMobileSoap.listeProgrammes(username, password);

						break;
					case SignetMethods.LIST_COEQ:

						String pNomElementEval = reqParams[0];
						String pSession = reqParams[1];
						String pGroupe = reqParams[2];
						String pSigle = reqParams[3];
						result = signetsMobileSoap.listeCoequipiers(username, password, pSigle, pGroupe, pSession,
								pNomElementEval);

						break;
					case SignetMethods.LIST_EVAL:

						String pSession1 = reqParams[0];
						String pGroupe1 = reqParams[1];
						String pSigle1 = reqParams[2];
						result = signetsMobileSoap.listeElementsEvaluation(username, password, pSigle1, pGroupe1,
								pSession1);

						break;
					case SignetMethods.LIST_HORAIRE_PROF:

						String pSession2 = reqParams[0];

						result = signetsMobileSoap.listeHoraireEtProf(username, password, pSession2);

						break;
					case SignetMethods.LIRE_HORAIRE:

						String pSession3 = reqParams[0];
						String prefixeSigleCours = reqParams[1];

						result = signetsMobileSoap.lireHoraire(pSession3, prefixeSigleCours);

						break;
					case SignetMethods.LIRE_JOURS_REMPLACES:

						String pSession4 = reqParams[0];

						result = signetsMobileSoap.lireJoursRemplaces(pSession4);

						break;
					case SignetMethods.BOTTIN_LIST_DEPT:
						result = new WebServiceSoap().GetListeDepartement();

						break;
					case SignetMethods.BOTTIN_GET_FICHE:
						String numero = reqParams[0];
						String PathFiche = reqParams[1];
						result = new WebServiceSoap().GetFiche(numero, PathFiche);

						break;

					case SignetMethods.BOTTIN_GET_FICHE_DATA:
						String Id = reqParams[0];
						result = new WebServiceSoap().GetFicheData(Id);

						break;

					case SignetMethods.BOTTIN_GET_ALL:
						result = new WebServiceSoap().Recherche(null, null, null);

						break;

					case SignetMethods.BOTTIN_GET_FICHE_BY_SERVICE:

						String filtreServiceCode = reqParams[0];

						result = new WebServiceSoap().Recherche(null, null, filtreServiceCode);
						break;

					case SignetMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP:

						ArrayOfService arrayOfService = new WebServiceSoap().GetListeDepartement();

						HashMap<String, List<FicheEmploye>> listeEmployeByService = new HashMap<String, List<FicheEmploye>>();
						ArrayOfFicheEmploye arrayOfFicheEmploye;
						
						for (int i = 0; i < arrayOfService.size(); i++) {
							
							Service service = arrayOfService.get(i);
							arrayOfFicheEmploye = new WebServiceSoap().Recherche(null, null, "" + service.ServiceCode);

							listeEmployeByService.put(service.Nom, arrayOfFicheEmploye);

						}
                        result = listeEmployeByService;

                        break;
						
						
					case SignetMethods.LIST_EXAMENS_FINAUX:
						String pSession5 = reqParams[0];
						result = signetsMobileSoap.listeHoraireExamensFin(username, password, pSession5);
						
						break;
						
						
					case SignetMethods.LIST_SEANCES:
						
						String pCoursGroupe = reqParams[0];
						String pSession6 = reqParams[1];
						String pDateDebut = reqParams[2];
						String pDateFin = reqParams[3];
						
						result = signetsMobileSoap.lireHoraireDesSeances(username, password, pCoursGroupe, pSession6, pDateDebut, pDateFin);
						
						break;
						
					case SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION:

							ListeDeSessions listeDeSessions = signetsMobileSoap.listeSessions(username, password);

							listeSeances listeSeances = new listeSeances();

                            DateTime dt = new DateTime();

							DateTime dtEnd = new DateTime();

							for(Trimestre trimestre : listeDeSessions.liste) {

								dtEnd = new DateTime(trimestre.dateFin);

								if(dt.isBefore(dtEnd.plusDays(1))) {
									listeSeances.ListeDesSeances.addAll( signetsMobileSoap.lireHoraireDesSeances(username, password, "", trimestre.abrege, "", "").ListeDesSeances);
								}
							}

							result = listeSeances;
						
						break;
						
					case SignetMethods.LIST_EXAM_CURRENT_AND_NEXT_SESSION:
						
							ListeDeSessions listeDeSessions2 = signetsMobileSoap.listeSessions(username, password);
							
							listeHoraireExamensFinaux listeHoraireExamensFinaux = new listeHoraireExamensFinaux();
							
							DateTime dt2 = new DateTime();
							DateTime dtEnd2 = new DateTime();
							
							for(Trimestre trimestre : listeDeSessions2.liste) {
								
								dtEnd2 = new DateTime(trimestre.dateFin);
								
								if(dt2.isBefore(dtEnd2.plusDays(1))) {
									listeHoraireExamensFinaux.listeHoraire.addAll( signetsMobileSoap.listeHoraireExamensFin(username, password, trimestre.abrege).listeHoraire );
								}
							}
							
							result = listeHoraireExamensFinaux;
						break;
						
					case SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION:
						
							ListeDeSessions listeDeSessions3 = signetsMobileSoap.listeSessions(username, password);
							
							listeJoursRemplaces listeJoursRemplaces = new listeJoursRemplaces();
							
							
							DateTime dt3 = new DateTime();
							DateTime dtEnd3 = new DateTime();
							
							for(Trimestre trimestre : listeDeSessions3.liste) {
								
								dtEnd3 = new DateTime(trimestre.dateFin);
								
								if(dt3.isBefore(dtEnd3.plusDays(1))) {
									
									listeJoursRemplaces.listeJours.addAll( signetsMobileSoap.lireJoursRemplaces(trimestre.abrege).listeJours );
									
								}
							}
							
							
							result = listeJoursRemplaces;
						
						break;
						

					default:
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
					exception = e ;
					
				}
				return null;
			}
			
			protected void onPostExecute(Object result2) {
				if (exception != null) {
					listener.onRequestFailure(new SpiceException("Couldn't get datas"));
				} else {
					listener.onRequestSuccess(result);
				}

				tasks.remove(this);
			}
			
		};

		tasks.add(task);

		task.execute(method, params);
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
		public static final int LIRE_JOURS_REMPLACES = 9;
		public static final int BOTTIN_LIST_DEPT = 10;
		public static final int BOTTIN_GET_FICHE = 11;
		public static final int BOTTIN_GET_FICHE_DATA = 12;
		public static final int BOTTIN_GET_ALL = 13;
		public static final int BOTTIN_GET_FICHE_BY_SERVICE = 14;
		public static final int BOTTIN_GET_LIST_SERVICE_AND_EMP = 15;
		public static final int LIST_EXAMENS_FINAUX = 16;
		public static final int LIST_SEANCES = 17;
		public static final int LIST_SEANCES_CURRENT_AND_NEXT_SESSION = 18;
		public static final int LIST_EXAM_CURRENT_AND_NEXT_SESSION = 19;
		public static final int LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION = 20;
	}


	/**
	 * Convenience method to login a user
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

		for (AsyncTask<Object, Void, Object> task : tasks) {
			task.cancel(true);
		}
	}

	/**
	 * @return the first registered {@link Etudiant} or null if none is
	 *         registered
	 * @throws java.sql.SQLException
	 */
	public Etudiant getRegisteredEtudiant() throws SQLException {

		final List<Etudiant> queryForAll = dbHelper.getDao(Etudiant.class).queryForAll();
		if (queryForAll.size() > 0)
			return queryForAll.get(0);
		return null;
	}

	public void sendRequest(SpringAndroidSpiceRequest request, RequestListener<Object> listener) {
		spiceManager.execute(request, listener);
	}

}
