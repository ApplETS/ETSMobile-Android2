package ca.etsmtl.applets.etsmobile.service;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile2.R;

public class FcmRegistrationIntentService extends JobIntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public static final int REG_JOB_ID = 1;

    /**
     * Adds a service to be either started directly (Pre-Android 8.0) or to be enqueued as a job
     * (Android 8.0 and later)
     *
     * @param context the context required to start the service
     * @param intent the intent in which the service is based on
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, FcmRegistrationIntentService.class, REG_JOB_ID, intent);
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {

        // In the (unlikely) event that multiple refresh operations occur simultaneously,
        // ensure that they are processed sequentially.x
        // [START register_for_gcm]
        // Initially this call goes out to the network to retrieve the token, subsequent calls
        // are local.
        // [START get_token]
        try {
            Task<InstanceIdResult> instanceId = FirebaseInstanceId.getInstance().getInstanceId();
            InstanceIdResult result = Tasks.await(instanceId);
            Log.i(TAG, "FCM Registration Token: " + result.getToken());
            if (ApplicationManager.domaine != null && ApplicationManager.userCredentials != null) {
                sendRegistrationToServer(result.getToken());

                // Subscribe to topic channels
                subscribeTopics();
                // [END get_token]

                // Notify UI that registration has completed, so the progress indicator can be hidden.
                Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

                // [END register_for_fcm]
            }
        } catch (ExecutionException | InterruptedException ei) {
            ei.printStackTrace();
        }
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        String userData = ApplicationManager.domaine + "\\" + ApplicationManager.userCredentials.getUsername();
        ArnEndpointHandler handler = new ArnEndpointHandler(getApplicationContext(), token, userData, getString(R.string.aws_application_arn));
        handler.createOrUpdateEndpoint();
    }

    /**
     * Subscribe to any FCM topics of interest, as defined by the TOPICS constant.
     */
    // [START subscribe_topics]
    private void subscribeTopics() {
        for (String topic : TOPICS) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        }
    }
    // [END subscribe_topics]

}
