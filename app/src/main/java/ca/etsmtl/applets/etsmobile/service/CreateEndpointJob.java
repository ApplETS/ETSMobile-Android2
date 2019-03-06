package ca.etsmtl.applets.etsmobile.service;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.GetPlatformApplicationAttributesRequest;
import com.amazonaws.services.sns.model.GetPlatformApplicationAttributesResult;
import com.amazonaws.services.sns.model.NotFoundException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 16/10/15.
 */
public class CreateEndpointJob implements Runnable {

    static final int MALFORMED_PROPERTIES_ERROR_CODE = 1;
    static final int CREDENTIAL_RETRIEVAL_FAILURE_ERROR_CODE = 2;
    static final int FILE_ACCESS_FAILURE_ERROR_CODE = 3;
    static final int NOT_FOUND_ERROR_CODE = 4;

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

    public CreateEndpointJob(Context context) {

        try {
            //TODO put the credentials in a properties file
            client = new AmazonSNSClient(new BasicAWSCredentials(
                    context.getString(R.string.aws_access_key),
                    context.getString(R.string.aws_secret_key)
            ));
//            client = new AmazonSNSClient(new PropertiesCredentials(
//                    CreateEndpointJob.class.getClassLoader().getResourceAsStream("/AwsCredentials.properties")
//            ));
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    public void setThreadProperties(String token, String userData, String applicationArn) {
        this.token = token;
        this.userData = userData;
        this.applicationArn = applicationArn;
    }

    public void verifyPlatformApplication(AmazonSNS client) {
        try {
            if (!listOfRegions.contains(this.region = this.applicationArn.split(":")[3])) {
                System.err.println("[ERROR] The region " + region + " is invalid");
                System.exit(MALFORMED_PROPERTIES_ERROR_CODE);
            }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.err.println("[ERROR] The ARN " + this.applicationArn + " is malformed");
            System.exit(MALFORMED_PROPERTIES_ERROR_CODE);
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
            System.exit(NOT_FOUND_ERROR_CODE);
        } catch (InvalidParameterException ipe) {
            System.err.println("[ERROR: APP ARN INVALID] The application ARN provided: "
                    + this.applicationArn
                    + " is malformed"
                    + ipe.getMessage());
            System.exit(NOT_FOUND_ERROR_CODE);
        }
    }

    @Override
    public void run() {

        verifyPlatformApplication(this.client);
        try {
            CreatePlatformEndpointResult createResult =
                    client.createPlatformEndpoint(
                            new CreatePlatformEndpointRequest()
                                    .withPlatformApplicationArn(this.applicationArn)
                                    .withToken(this.token)
                                    .withCustomUserData(this.userData)
                    );

            Log.i("CreateResult", createResult.toString());
        } catch (AmazonClientException ace) {
            ace.printStackTrace();
        }
    }

}