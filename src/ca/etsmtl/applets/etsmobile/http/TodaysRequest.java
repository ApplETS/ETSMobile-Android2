package ca.etsmtl.applets.etsmobile.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ca.etsmtl.applets.etsmobile.model.TodaysCourses;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class TodaysRequest extends SpringAndroidSpiceRequest<TodaysCourses> {

	private String url;
	private UserCredentials c;

	public TodaysRequest(String url, UserCredentials c) {
		super(TodaysCourses.class);
		this.url = url;
		this.c = c;
	}

	@Override
	public TodaysCourses loadDataFromNetwork() throws Exception {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("c", c.getUsername());
		parameters.set("p", c.getPassword());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(parameters,
				headers);
		getRestTemplate().getMessageConverters().add(new FormHttpMessageConverter());
		return getRestTemplate().postForObject(url, request, TodaysCourses.class);
	}

}
