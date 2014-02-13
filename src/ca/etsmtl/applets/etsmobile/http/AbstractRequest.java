package ca.etsmtl.applets.etsmobile.http;

import java.util.Random;

import android.os.SystemClock;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class AbstractRequest<X> extends SpringAndroidSpiceRequest<X> {

	private static final Random RANDOM = new Random();
	private Class<X> klass;
	private String url;

	@SuppressWarnings("unchecked")
	<T> AbstractRequest(T t, String url) {
		super((Class<X>) t.getClass());
		this.url = url;
		klass = (Class<X>) t.getClass();

	}

	@Override
	public X loadDataFromNetwork() throws Exception {

		// String url =
		// String.format("https://api.github.com/users/%s/followers", user);

		return getRestTemplate().getForObject(url, klass);
	}

	/**
	 * This method generates a unique cache key for this request. In this case
	 * our cache key depends just on the keyword.
	 * 
	 * @return
	 */
	public String createCacheKey() {
		return "etsmobile_v2_cache_key."
				+ RANDOM.nextInt((int) SystemClock.currentThreadTimeMillis()) * 10;
	}

}
