package ca.etsmtl.applets.etsmobile.http;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.MonETSNotificationList;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 15/12/15.
 */
public class MonETSNotificationsRequest extends SpringAndroidSpiceRequest<MonETSNotificationList> {

    private final Context context;
    private final boolean onlyNewNotifs;
    private AccountManager accountManager;
    private String authToken = "";

    public MonETSNotificationsRequest(Context context, boolean onlyNewNotifs) {
        super(MonETSNotificationList.class);

        this.context = context;
        this.onlyNewNotifs = onlyNewNotifs;

    }

    @Override
    public MonETSNotificationList loadDataFromNetwork() throws Exception {

        accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            authToken = accountManager.peekAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE);
        }

        String url = context.getString(R.string.portail_api_base_url);
        if (onlyNewNotifs) {
            url += "/api/notification/dossier/1";
        } else {
            url += "/api/notification";
        }

        ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
                requestWrapper.getHeaders().set("Cookie", authToken);
                return execution.execute(requestWrapper, body);
            }
        };

        RestTemplate restTemplate = getRestTemplate();
        List<ClientHttpRequestInterceptor> list = new ArrayList<ClientHttpRequestInterceptor>();
        list.add(interceptor);
        restTemplate.setInterceptors(list);
        try {
            return restTemplate.getForObject(url, MonETSNotificationList.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                if (accounts.length > 0) {
                    accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
                    authToken = accountManager.blockingGetAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE, true);

                    interceptor = new ClientHttpRequestInterceptor() {
                        @Override
                        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
                            requestWrapper.getHeaders().set("Cookie", authToken);
                            return execution.execute(requestWrapper, body);
                        }
                    };
                    list.clear();
                    list.add(interceptor);
                    restTemplate.setInterceptors(list);

                }
            }
        } finally {
            return restTemplate.getForObject(url, MonETSNotificationList.class);
        }
    }
}
