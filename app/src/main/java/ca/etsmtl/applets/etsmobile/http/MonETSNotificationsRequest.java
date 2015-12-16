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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.applets.etsmobile.model.MonETSNotificationList;
import ca.etsmtl.applets.etsmobile.util.Constants;

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

        String url;
        if (onlyNewNotifs) {
            url = "https://portail.etsmtl.ca/api/notification/dossier/1";
        } else {
            url = "https://portail.etsmtl.ca/api/notification";
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

        return restTemplate.getForObject(url, MonETSNotificationList.class);
    }
}
