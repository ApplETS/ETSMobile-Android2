package ca.etsmtl.applets.etsmobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ca.etsmtl.applets.etsmobile.model.AppMonETSNotification;
import ca.etsmtl.applets.etsmobile.model.Cours;
import ca.etsmtl.applets.etsmobile.model.ElementEvaluation;
import ca.etsmtl.applets.etsmobile.model.Enseignant;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.FicheEmploye;
import ca.etsmtl.applets.etsmobile.model.HoraireActivite;
import ca.etsmtl.applets.etsmobile.model.HoraireExamenFinal;
import ca.etsmtl.applets.etsmobile.model.JoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.ListeDesElementsEvaluation;
import ca.etsmtl.applets.etsmobile.model.Personne;
import ca.etsmtl.applets.etsmobile.model.Programme;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.model.TodaysCourses;
import ca.etsmtl.applets.etsmobile.model.Trimestre;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "etsmobile2.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 5;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Etudiant.class);
			TableUtils.createTable(connectionSource, Cours.class);
			TableUtils.createTable(connectionSource, JoursRemplaces.class);
            TableUtils.createTable(connectionSource, ListeDesElementsEvaluation.class);
			TableUtils.createTable(connectionSource, ElementEvaluation.class);
			TableUtils.createTable(connectionSource, Enseignant.class);
			TableUtils.createTable(connectionSource, HoraireActivite.class);
			TableUtils.createTable(connectionSource, Personne.class);
			TableUtils.createTable(connectionSource, Programme.class);
			TableUtils.createTable(connectionSource, Trimestre.class);
            TableUtils.createTable(connectionSource, Event.class);
			TableUtils.createTable(connectionSource, TodaysCourses.class);
			TableUtils.createTable(connectionSource, TodaysCourses.Seance.class);
			TableUtils.createTable(connectionSource, HoraireExamenFinal.class);
			TableUtils.createTable(connectionSource, Seances.class);
			TableUtils.createTable(connectionSource, FicheEmploye.class);
			TableUtils.createTable(connectionSource, Sponsor.class);
			TableUtils.createTable(connectionSource, AppMonETSNotification.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Etudiant.class, true);
            TableUtils.dropTable(connectionSource, Cours.class, true);
            TableUtils.dropTable(connectionSource, JoursRemplaces.class, true);
            TableUtils.dropTable(connectionSource, ListeDesElementsEvaluation.class, true);
            TableUtils.dropTable(connectionSource, ElementEvaluation.class, true);
            TableUtils.dropTable(connectionSource, Enseignant.class, true);
            TableUtils.dropTable(connectionSource, HoraireActivite.class, true);
            TableUtils.dropTable(connectionSource, Personne.class, true);
            TableUtils.dropTable(connectionSource, Programme.class, true);
            TableUtils.dropTable(connectionSource, Trimestre.class, true);
            TableUtils.dropTable(connectionSource, Event.class, true);
            TableUtils.dropTable(connectionSource, TodaysCourses.class, true);
            TableUtils.dropTable(connectionSource, TodaysCourses.Seance.class, true);
            TableUtils.dropTable(connectionSource, HoraireExamenFinal.class, true);
            TableUtils.dropTable(connectionSource, Seances.class, true);
			TableUtils.dropTable(connectionSource, FicheEmploye.class, true);
			TableUtils.dropTable(connectionSource, Sponsor.class, true);
            TableUtils.dropTable(connectionSource, AppMonETSNotification.class, true);

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}
}