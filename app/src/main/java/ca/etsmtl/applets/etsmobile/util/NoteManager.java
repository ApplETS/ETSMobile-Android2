package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Cours;
import ca.etsmtl.applets.etsmobile.model.ElementEvaluation;
import ca.etsmtl.applets.etsmobile.model.ListeDeCours;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.ListeDesElementsEvaluation;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.ui.fragment.NotesDetailsFragment;
import ca.etsmtl.applets.etsmobile.ui.fragment.NotesFragment;

/**
 * Created by nicolas on 15-05-14.
 */
public class NoteManager extends Observable implements RequestListener<Object> {

    private Context context;

    private boolean synchListeDeCours = false;
    private boolean synchListeDeSessions = false;
    private boolean synchListeDesElementsEvaluation = false;

    public NoteManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void updateCours(List<Cours> coursList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            for (Cours cours : coursList) {
                cours.id = cours.sigle + cours.session; // Id field upgraded to make difference between upgrading row or creating row
                dbHelper.getDao(Cours.class).createOrUpdate(cours);
            }
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    public void updateTrimestres(List<Trimestre> trimestresList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            for (Trimestre trimestre : trimestresList)
                dbHelper.getDao(Trimestre.class).createOrUpdate(trimestre);
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    /**
     * Rajout de l'id pour identifier les éléments d'évaluation par rapport au cours effectué dans une session
     */
    public void updateElementsEvaluation(ListeDesElementsEvaluation listElementsEvaluation) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.getDao(ListeDesElementsEvaluation.class).createOrUpdate(listElementsEvaluation);

            for (ElementEvaluation elementEvaluation : listElementsEvaluation.liste) {
                elementEvaluation.id = listElementsEvaluation.id + elementEvaluation.nom;
                elementEvaluation.listeDesElementsEvaluation = listElementsEvaluation;
                dbHelper.getDao(ElementEvaluation.class).createOrUpdate(elementEvaluation);
            }
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    public ArrayList<Cours> getCours() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        ArrayList<Cours> coursList = null;
        try {
            coursList = (ArrayList<Cours>) dbHelper.getDao(Cours.class).queryForAll();
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return coursList;
    }

    public List<Trimestre> getTrimestres() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<Trimestre> trimestresList = null;
        try {
            trimestresList = dbHelper.getDao(Trimestre.class).queryForAll();
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return trimestresList;
    }

    public ListeDesElementsEvaluation getListElementsEvaluation(String id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        ListeDesElementsEvaluation listElementsEvaluation = null;
        try {
            Dao<ListeDesElementsEvaluation, String> listElementsEvaluationsDao = dbHelper.getDao(ListeDesElementsEvaluation.class);
            listElementsEvaluation = listElementsEvaluationsDao.queryForId(id);
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return listElementsEvaluation;
    }

    public List<ElementEvaluation> getElementsEvaluation(ListeDesElementsEvaluation listeDesElementsEvaluation) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<ElementEvaluation> elementEvaluationList = null;
        try {
            Dao<ElementEvaluation, String> elementsEvaluationDao = dbHelper.getDao(ElementEvaluation.class);
            QueryBuilder<ElementEvaluation, String> builder = elementsEvaluationDao.queryBuilder();

            Where where = builder.where();
            where.eq("listeDesElementsEvaluation_id", listeDesElementsEvaluation);

            elementEvaluationList = builder.query();

        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return elementEvaluationList;
    }

    public void remove() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            // Delete all rows that contains an Etudiant and listeDesProgrammes classes
            dbHelper.getDao(Cours.class).deleteBuilder().delete();
            dbHelper.getDao(Trimestre.class).deleteBuilder().delete();
            dbHelper.getDao(ListeDesElementsEvaluation.class).deleteBuilder().delete();
            dbHelper.getDao(ElementEvaluation.class).deleteBuilder().delete();
            //  dbHelper.getDao(Programme.class).deleteBuilder().delete();
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    /**
     * Deletes courses in DB that doesn't exist on API
     *
     * @param
     */
    public void deleteExpiredCours(ListeDeCours listeDeCours) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        HashMap<String, Cours> coursHashMap = new HashMap<String, Cours>();
        for (Cours cours : listeDeCours.liste) {
            cours.id = cours.sigle + cours.session;
            coursHashMap.put(cours.id, cours);
        }

        ArrayList<Cours> dbCours = new ArrayList<Cours>();
        try {
            dbCours = (ArrayList<Cours>) dbHelper.getDao(Cours.class).queryForAll();
            ArrayList<ListeDesElementsEvaluation> dbliste = (ArrayList<ListeDesElementsEvaluation>) dbHelper.getDao(ListeDesElementsEvaluation.class).queryForAll();
            for (Cours coursNew : dbCours) {

                if (!coursHashMap.containsKey(coursNew.id)) {
                    Dao<Cours, String> coursDao = dbHelper.getDao(Cours.class);
                    coursDao.deleteById(coursNew.id);

                    deleteExpiredListeDesElementsEvaluation(coursNew.id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes trimestres in DB that doesn't exist on API
     *
     * @param
     */
    public void deleteExpiredTrimestres(ListeDeSessions listeDeSessions) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        HashMap<String, Trimestre> trimestresHashMap = new HashMap<String, Trimestre>();
        for (Trimestre trimestre : listeDeSessions.liste) {
            trimestresHashMap.put(trimestre.abrege, trimestre);
        }

        ArrayList<Trimestre> dbTrimestres = new ArrayList<Trimestre>();
        try {
            dbTrimestres = (ArrayList<Trimestre>) dbHelper.getDao(Trimestre.class).queryForAll();
            for (Trimestre trimestreNew : dbTrimestres) {

                if (!trimestresHashMap.containsKey(trimestreNew.abrege)) {
                    Dao<Trimestre, String> trimestreDao = dbHelper.getDao(Trimestre.class);
                    trimestreDao.deleteById(trimestreNew.abrege);

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes marks in DB that doesn't exist on API
     *
     * @param
     */
    private void deleteExpiredListeDesElementsEvaluation(String id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        try {

            Dao<ListeDesElementsEvaluation, String> listeDesElementsEvaluationDao = dbHelper.getDao(ListeDesElementsEvaluation.class);
            ListeDesElementsEvaluation listeDesElementsEvaluation = listeDesElementsEvaluationDao.queryForId(id);

            if (listeDesElementsEvaluation != null) {
                Dao<ElementEvaluation, String> elementsEvaluationDao = dbHelper.getDao(ElementEvaluation.class);
                DeleteBuilder<ElementEvaluation, String> deleteBuilder = elementsEvaluationDao.deleteBuilder();

                Where where = deleteBuilder.where();
                where.eq("listeDesElementsEvaluation_id", listeDesElementsEvaluation);

                deleteBuilder.delete();
            }

            listeDesElementsEvaluationDao.deleteById(id);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes marks in DB that doesn't exist on API
     *
     * @param
     */
    public void deleteExpiredElementsEvaluation(ListeDesElementsEvaluation listeDesElementsEvaluation) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        HashMap<String, ElementEvaluation> elementEvaluationHashMap = new HashMap<String, ElementEvaluation>();
        for (ElementEvaluation elem : listeDesElementsEvaluation.liste) {
            String id = listeDesElementsEvaluation.id + elem.nom;
            elementEvaluationHashMap.put(id, elem);
        }

        List<ElementEvaluation> elementEvaluationList = null;
        try {
            Dao<ElementEvaluation, String> elementsEvaluationDao = dbHelper.getDao(ElementEvaluation.class);
            QueryBuilder<ElementEvaluation, String> builder = elementsEvaluationDao.queryBuilder();

            Where where = builder.where();
            where.eq("listeDesElementsEvaluation_id", listeDesElementsEvaluation);

            elementEvaluationList = builder.query();
            for (ElementEvaluation element : elementEvaluationList) {
                if (!elementEvaluationHashMap.containsKey(element.id))
                    elementsEvaluationDao.deleteById(element.id);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestFailure(SpiceException spiceException) {
        spiceException.printStackTrace();
    }

    @Override
    public void onRequestSuccess(final Object o) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                //ListeDeCours
                if (o instanceof ListeDeCours) {
                    ListeDeCours listeDeCours = (ListeDeCours) o;

                    deleteExpiredCours(listeDeCours);
                    updateCours(listeDeCours.liste);

                    synchListeDeCours = true;
                }

                //ListeDeSessions
                if (o instanceof ListeDeSessions) {
                    ListeDeSessions listeDeSessions = (ListeDeSessions) o;

                    deleteExpiredTrimestres(listeDeSessions);
                    updateTrimestres(listeDeSessions.liste);

                    synchListeDeSessions = true;
                }

                //ListeDesElementsEvaluation
                if (o instanceof ListeDesElementsEvaluation) {
                    ListeDesElementsEvaluation listeDesElementsEvaluation = (ListeDesElementsEvaluation) o;

                    deleteExpiredElementsEvaluation(listeDesElementsEvaluation);
                    updateElementsEvaluation(listeDesElementsEvaluation);

                    synchListeDesElementsEvaluation = true;
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (synchListeDeCours && synchListeDeSessions) {
                    NoteManager.this.setChanged();
                    NoteManager.this.notifyObservers(NotesFragment.class.getName());
                }

                if (synchListeDesElementsEvaluation) {
                    NoteManager.this.setChanged();
                    NoteManager.this.notifyObservers(NotesDetailsFragment.class.getName());
                }
            }


        }.execute();
    }
}
