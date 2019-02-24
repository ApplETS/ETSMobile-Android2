package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.os.AsyncTask;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;

import java.io.InputStream;
import java.lang.ref.WeakReference;
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
import ca.etsmtl.applets.etsmobile.util.SignetsMethods;
import ca.etsmtl.applets.etsmobile2.R;

public class SignetsRequestTask extends AsyncTask<Object, Void, Object> {
    private UserCredentials credentials;
    private RequestListener<Object> listener;
    private DatabaseHelper dbHelper;
    private WeakReference<Context> taskContext;
    private Exception exception = null;
    private Object result;

    public SignetsRequestTask(Context context, UserCredentials userCredentials, RequestListener<Object> requestListener, DatabaseHelper databaseHelper) {
        taskContext = new WeakReference<>(context);
        credentials = userCredentials;
        listener = requestListener;
        dbHelper = databaseHelper;
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {

            final int methodID = (Integer) params[0];
            String[] reqParams = (String[]) params[1];

            Context c = taskContext.get();
            InputStream certificate = c.getResources().openRawResource(R.raw.ets_pub_cert);
            final SignetsMobileSoap signetsMobileSoap = new SignetsMobileSoap(certificate);

            String username = "";
            String password = "";

            if (methodID < SignetsMethods.BOTTIN_LIST_DEPT || methodID > SignetsMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP) {
                username = credentials.getUsername();
                password = credentials.getPassword();
            }
            switch (methodID) {
                case SignetsMethods.INFO_ETUDIANT:
                    result = signetsMobileSoap.infoEtudiant(username, password);
                    dbHelper.getDao(Etudiant.class).createOrUpdate((Etudiant) result);
                    break;
                case SignetsMethods.LIST_COURS:
                    result = signetsMobileSoap.listeCours(username, password);
                    break;
                case SignetsMethods.LIST_INT_SESSION:
                    String SesFin = reqParams[0];
                    String SesDebut = reqParams[1];
                    result = signetsMobileSoap.listeCoursIntervalleSessions(username, password, SesDebut, SesFin);
                    break;
                case SignetsMethods.LIST_SESSION:
                    result = signetsMobileSoap.listeSessions(username, password);
                    break;
                case SignetsMethods.LIST_PROGRAM:
                    result = signetsMobileSoap.listeProgrammes(username, password);
                    break;
                case SignetsMethods.LIST_COEQ:
                    String pNomElementEval = reqParams[0];
                    String pSession = reqParams[1];
                    String pGroupe = reqParams[2];
                    String pSigle = reqParams[3];
                    result = signetsMobileSoap.listeCoequipiers(username, password, pSigle, pGroupe, pSession,
                            pNomElementEval);
                    break;
                case SignetsMethods.LIST_EVAL:
                    String pSession1 = reqParams[0];
                    String pGroupe1 = reqParams[1];
                    String pSigle1 = reqParams[2];
                    result = signetsMobileSoap.listeElementsEvaluation(username, password, pSigle1, pGroupe1,
                            pSession1);
                    break;
                case SignetsMethods.LIST_HORAIRE_PROF:
                    String pSession2 = reqParams[0];
                    result = signetsMobileSoap.listeHoraireEtProf(username, password, pSession2);
                    break;
                case SignetsMethods.LIRE_HORAIRE:
                    String pSession3 = reqParams[0];
                    String prefixeSigleCours = reqParams[1];
                    result = signetsMobileSoap.lireHoraire(pSession3, prefixeSigleCours);
                    break;
                case SignetsMethods.LIRE_JOURS_REMPLACES:
                    String pSession4 = reqParams[0];
                    result = signetsMobileSoap.lireJoursRemplaces(pSession4);
                    break;
                case SignetsMethods.BOTTIN_LIST_DEPT:
                    result = new WebServiceSoap().GetListeDepartement();
                    break;
                case SignetsMethods.BOTTIN_GET_FICHE:
                    String numero = reqParams[0];
                    String PathFiche = reqParams[1];
                    result = new WebServiceSoap().GetFiche(numero, PathFiche);
                    break;
                case SignetsMethods.BOTTIN_GET_FICHE_DATA:
                    String Id = reqParams[0];
                    result = new WebServiceSoap().GetFicheData(Id);
                    break;
                case SignetsMethods.BOTTIN_GET_ALL:
                    result = new WebServiceSoap().Recherche(null, null, null);
                    break;
                case SignetsMethods.BOTTIN_GET_FICHE_BY_SERVICE:
                    String filtreServiceCode = reqParams[0];
                    result = new WebServiceSoap().Recherche(null, null, filtreServiceCode);
                    break;

                case SignetsMethods.BOTTIN_GET_LIST_SERVICE_AND_EMP:
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
                case SignetsMethods.LIST_EXAMENS_FINAUX:
                    String pSession5 = reqParams[0];
                    result = signetsMobileSoap.listeHoraireExamensFin(username, password, pSession5);
                    break;
                case SignetsMethods.LIST_SEANCES:
                    String pCoursGroupe = reqParams[0];
                    String pSession6 = reqParams[1];
                    String pDateDebut = reqParams[2];
                    String pDateFin = reqParams[3];

                    result = signetsMobileSoap.lireHoraireDesSeances(username, password, pCoursGroupe, pSession6, pDateDebut, pDateFin);
                    break;
                case SignetsMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION:
                    ListeDeSessions listeDeSessions = signetsMobileSoap.listeSessions(username, password);
                    listeSeances listeSeances = new listeSeances();
                    DateTime dt = new DateTime();

                    for (Trimestre trimestre : listeDeSessions.liste) {
                        DateTime dtEnd = new DateTime(trimestre.dateFin);

                        if (dt.isBefore(dtEnd.plusDays(1))) {
                            listeSeances.ListeDesSeances.addAll(signetsMobileSoap.lireHoraireDesSeances(username, password, "", trimestre.abrege, "", "").ListeDesSeances);
                        }
                    }
                    result = listeSeances;
                    break;
                case SignetsMethods.LIST_EXAM_CURRENT_AND_NEXT_SESSION:
                    ListeDeSessions listeDeSessions2 = signetsMobileSoap.listeSessions(username, password);
                    listeHoraireExamensFinaux listeHoraireExamensFinaux = new listeHoraireExamensFinaux();
                    DateTime dt2 = new DateTime();

                    for (Trimestre trimestre : listeDeSessions2.liste) {
                        DateTime dtEnd2 = new DateTime(trimestre.dateFin);

                        if (dt2.isBefore(dtEnd2.plusDays(1))) {
                            listeHoraireExamensFinaux.listeHoraire.addAll(signetsMobileSoap.listeHoraireExamensFin(username, password, trimestre.abrege).listeHoraire);
                        }
                    }
                    result = listeHoraireExamensFinaux;
                    break;
                case SignetsMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION:
                    ListeDeSessions listeDeSessions3 = signetsMobileSoap.listeSessions(username, password);
                    listeJoursRemplaces listeJoursRemplaces = new listeJoursRemplaces();
                    DateTime dt3 = new DateTime();

                    for (Trimestre trimestre : listeDeSessions3.liste) {
                        DateTime dtEnd3 = new DateTime(trimestre.dateFin);

                        if (dt3.isBefore(dtEnd3.plusDays(1))) {
                            listeJoursRemplaces.listeJours.addAll(signetsMobileSoap.lireJoursRemplaces(trimestre.abrege).listeJours);
                        }
                    }
                    result = listeJoursRemplaces;
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        return null;
    }

    protected void onPostExecute(Object result2) {
        if (exception != null) {
            listener.onRequestFailure(new SpiceException("Couldn't get datas"));
        } else {
            listener.onRequestSuccess(result);
        }
    }
}
