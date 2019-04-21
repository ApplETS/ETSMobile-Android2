package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeleteEndpointRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.GetPlatformApplicationAttributesRequest;
import com.amazonaws.services.sns.model.GetPlatformApplicationAttributesResult;
import com.amazonaws.services.sns.model.NotFoundException;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.securepreferences.SecurePreferences;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gnut3ll4 on 16/10/15.
 */
public class ArnEndpointHandler {

    static final List<String> listOfRegions = Collections
            .unmodifiableList(new ArrayList<String>() {
                private static final long serialVersionUID = 1L;

                {
                    add("us-east-1");
                    add("us-west-1");
                    add("us-west-2");
                    add("sa-east-1");
                    add("eu-west-1");
                    add("ap-southeast-1");
                    add("ap-southeast-2");
                    add("ap-northeast-1");
                    add("us-gov-west-1");
                }
            });

    private AmazonSNS client;
    private String token;
    private String userData;
    private String applicationArn;
    private String region;
    private SecurePreferences securePreferences;

    public ArnEndpointHandler(Context context, String appToken, String data) {
        final String metaDataPrefix = "ca.etsmtl.applets.etsmobilenotifications";

        client = new AmazonSNSClient(new BasicAWSCredentials(
                MetaDataUtils.getValue(context, metaDataPrefix + ".AWS_ACCESS_KEY"),
                MetaDataUtils.getValue(context, metaDataPrefix + ".AWS_SECRET_KEY"))
        );
        token = appToken;
        userData = data;
        applicationArn =  MetaDataUtils.getValue(context, metaDataPrefix + ".SNS_ARN");
        securePreferences = new SecurePreferences(context);
    }

    private void verifyPlatformApplication(AmazonSNS client) {
        try {
            if (!listOfRegions.contains(this.region = this.applicationArn.split(":")[3])) {
                System.err.println("[ERROR] The region " + region + " is invalid");
                System.exit(Constants.MALFORMED_PROPERTIES_ERROR_CODE);
            }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.err.println("[ERROR] The ARN " + this.applicationArn + " is malformed");
            System.exit(Constants.MALFORMED_PROPERTIES_ERROR_CODE);
        }
        client.setEndpoint("https://sns." + this.region + ".amazonaws.com/");
        try {
            GetPlatformApplicationAttributesRequest applicationAttributesRequest
                    = new GetPlatformApplicationAttributesRequest();
            applicationAttributesRequest.setPlatformApplicationArn(this.applicationArn);
            @SuppressWarnings("unused")
            GetPlatformApplicationAttributesResult getAttributesResult = client
                    .getPlatformApplicationAttributes(applicationAttributesRequest);
        } catch (NotFoundException nfe) {
            System.err.println("[ERROR: APP NOT FOUND] The application ARN provided: "
                    + this.applicationArn
                    + " does not correspond to any existing platform applications. "
                    + nfe.getMessage());
            System.exit(Constants.NOT_FOUND_ERROR_CODE);
        } catch (InvalidParameterException ipe) {
            System.err.println("[ERROR: APP ARN INVALID] The application ARN provided: "
                    + this.applicationArn
                    + " is malformed"
                    + ipe.getMessage());
            System.exit(Constants.NOT_FOUND_ERROR_CODE);
        }
    }

    void createOrUpdateEndpoint() {

        verifyPlatformApplication(client);

        if (!isEndpointExists()) {
            createEndpoint();
        } else {
            String endpointArn = securePreferences.getString(Constants.SNS_ARN_ENDPOINT, "");
            GetEndpointAttributesRequest geaReq = new GetEndpointAttributesRequest()
                    .withEndpointArn(endpointArn);
            GetEndpointAttributesResult geaRes = client.getEndpointAttributes(geaReq);

            boolean updateNeeded = !geaRes.getAttributes().get("Token").equals(token) || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

            if (updateNeeded) {
                Log.d(getClass().getSimpleName(), "Updating platform endpoint " + endpointArn);
                Map<String, String> attribs = new HashMap<>();
                attribs.put("Token", token);
                attribs.put("Enabled", "true");
                SetEndpointAttributesRequest saeReq = new SetEndpointAttributesRequest()
                        .withEndpointArn(endpointArn)
                        .withAttributes(attribs);
                client.setEndpointAttributes(saeReq);
            }
        }
    }

    void deleteEndpoint() {
        if (isEndpointExists()) {
            new AsyncDeleteEndpoint(securePreferences.getString(Constants.SNS_ARN_ENDPOINT,
                    ""), client).execute();
        }
    }

    private boolean isEndpointExists() {
        return !securePreferences.getString(Constants.SNS_ARN_ENDPOINT, "").isEmpty();
    }

    private void createEndpoint() {
        try {
            CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest()
                    .withPlatformApplicationArn(this.applicationArn)
                    .withToken(this.token)
                    .withCustomUserData(this.userData);
            CreatePlatformEndpointResult createResult = client.createPlatformEndpoint(request);

            Log.d("CreateResult", createResult.toString());

            SecurePreferences.Editor editor = securePreferences.edit();
            editor.putString(Constants.SNS_ARN_ENDPOINT, createResult.getEndpointArn()).apply();
        } catch (AmazonClientException ace) {
            ace.printStackTrace();
        }
    }

    private static class AsyncDeleteEndpoint extends AsyncTask<Void, Void, Void> {

        private String endpointArn;
        private AmazonSNS client;

        AsyncDeleteEndpoint(String arn, AmazonSNS snsClient) {
            endpointArn = arn;
            client = snsClient;
        }

        @Override
        public Void doInBackground(Void... params) {
            DeleteEndpointRequest request = new DeleteEndpointRequest().withEndpointArn(endpointArn);
            client.deleteEndpoint(request);
            return null;
        }
    }
}