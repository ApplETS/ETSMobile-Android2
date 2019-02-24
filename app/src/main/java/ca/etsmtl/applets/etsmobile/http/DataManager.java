package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.SignetsMethods;
import ca.etsmtl.applets.etsmobile2.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataManager {

	private static final String TAG = "DataManager::";
	private static DataManager instance;
	private DatabaseHelper dbHelper;
	private MoodleWebService moodleService;
	private MonETSWebService monETSService;
	private static Context c;

	private DataManager() {
		dbHelper = new DatabaseHelper(c);
		moodleService = new Retrofit.Builder()
				.baseUrl(c.getString(R.string.moodle_url))
				.client(TLSUtilities.createETSOkHttpClient(c))
				.addConverterFactory(GsonConverterFactory.create())
				.build()
				.create(MoodleWebService.class);
		// Notifications dates layout are different for MonÉTS
		Gson monETSGson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create();
		monETSService = new Retrofit.Builder()
				.baseUrl(c.getString(R.string.url_mon_ets))
				.client(TLSUtilities.createETSOkHttpClient(c))
				.addConverterFactory(GsonConverterFactory.create(monETSGson))
				.build()
				.create(MonETSWebService.class);
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
	public void getDataFromSignet(int method, final UserCredentials creds, final RequestListener<Object> listener, String... params) {
		new SignetsRequestTask(c, creds, listener, dbHelper).execute(method, params);
	}

	/**
	 * Convenience method to login a user
	 * 
	 * @param userCredentials
	 * @param listener
	 */
	public void login(UserCredentials userCredentials, RequestListener<Object> listener) {
		getDataFromSignet(SignetsMethods.INFO_ETUDIANT, userCredentials, listener);
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

	public MoodleWebService getMoodleService() {
		return moodleService;
	}

	public MonETSWebService getMonETSService() {
		return monETSService;
	}
}
