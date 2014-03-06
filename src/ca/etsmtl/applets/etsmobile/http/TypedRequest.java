package ca.etsmtl.applets.etsmobile.http;

import java.util.Random;

import org.springframework.web.client.RestTemplate;

import android.os.SystemClock;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class TypedRequest<X> extends SpringAndroidSpiceRequest<X> {

	private static final Random RANDOM = new Random();
	private Class<X> klass;
	private String url;

	@SuppressWarnings("unchecked")
	public <T> TypedRequest(T t, String url) {
		super((Class<X>) t.getClass());
		this.url = url;
		klass = (Class<X>) t.getClass();

	}

	@Override
	public X loadDataFromNetwork() throws Exception {

		// HttpAuthentication authHeader = new
		// HttpBasicAuthentication("aj39950", "Kiss1234");
		// HttpHeaders requestHeaders = new HttpHeaders();
		// requestHeaders.setAuthorization(authHeader);
		// requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		//
		RestTemplate restTemplate = getRestTemplate();
		//
		// ResponseEntity<StudentProfile> response = restTemplate.exchange(url,
		// HttpMethod.POST,
		// new HttpEntity<Object>(requestHeaders), StudentProfile.class);

		// "{'codeAccessUniversel':'aj39950','motPasse':'Kiss1234'}"
		return restTemplate.postForObject(url, null, klass);
	}

	/**
	 * This method generates a unique cache key for this request. In this case
	 * our cache key depends just on the keyword.
	 * 
	 * @return
	 */
	public String createCacheKey() {
		return "etsmobile_v2_cache_key."
				+ RANDOM.nextInt((int) SystemClock.currentThreadTimeMillis())
				* 10;
	}

}
