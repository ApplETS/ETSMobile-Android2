package ca.etsmtl.applets.etsmobile.util;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.Programme;

/**
 * Created by phil on 15-01-14.
 */
public class ProfilManager {

    private Context context;

    public ProfilManager(Context context) {
        this.context = context;
    }

    public void updateEtudiant(Etudiant etudiant) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.getDao(Etudiant.class).createOrUpdate(etudiant);
        } catch(SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    public void updateProgramme(Programme programme) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.getDao(Programme.class).createOrUpdate(programme);
        } catch(SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }

    public Etudiant getEtudiant() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Etudiant etudiant = null;
        try {
            List<Etudiant> etudiantList = dbHelper.getDao(Etudiant.class).queryForAll();
            if(!etudiantList.isEmpty())
                // Get Student if exists
                etudiant = etudiantList.get(0);
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return etudiant;
    }

    public Programme getProgramme() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Programme programme = null;
        try {
            List<Programme> programmeList = dbHelper.getDao(Programme.class).queryForAll();
            if(!programmeList.isEmpty())
                // Get Program if exists
                programme = programmeList.get(0);
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
        return programme;
    }

    // Called when a user disconnects
    public void removeProfil() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            // Delete all rows that contains an Etudiant and listeDesProgrammes classes
            dbHelper.getDao(Etudiant.class).deleteBuilder().delete();
            dbHelper.getDao(Programme.class).deleteBuilder().delete();
        } catch (SQLException e) {
            Log.e("SQL Exception", e.getMessage());
        }
    }
}
