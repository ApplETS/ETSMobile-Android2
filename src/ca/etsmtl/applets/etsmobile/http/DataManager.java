package ca.etsmtl.applets.etsmobile.http;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;

public class DataManager {

	private static DataManager instance;
	private SpiceManager spiceManager;
	private Object lastRequestCacheKey;

	private DataManager() {
		spiceManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public boolean sendRequest(AbstractRequest request) {
		return false;
	}

	// ------------------------------------------------------------------------
	// ---------end of block that can fit in a common base class for all
	// activities
	// ------------------------------------------------------------------------

	private void performRequest(String user, AbstractRequest request,
			RequestListener<Object> listFollowersRequestListener) {
		// MainActivity.this.setProgressBarIndeterminateVisibility(true);

		// FollowersRequest request = new FollowersRequest(user);
		// lastRequestCacheKey = request.createCacheKey();
		//
		
		spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE,
				listFollowersRequestListener);
	}
}
