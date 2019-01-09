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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile2.R;

public class RegistrationIntentService extends JobIntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public static final int REG_JOB_ID = 1;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, RegistrationIntentService.class, REG_JOB_ID, intent);
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // In the (unlikely) event that multiple refresh operations occur simultaneously,
        // ensure that they are processed sequentially.x
        // [START register_for_gcm]
        // Initially this call goes out to the network to retrieve the token, subsequent calls
        // are local.
        // [START get_token]
        Task<InstanceIdResult> instanceId = FirebaseInstanceId.getInstance().getInstanceId();
        instanceId.addOnSuccessListener(task -> {
            String token = task.getToken();
            Log.i(TAG, "FCM Registration Token: " + token);
            if (ApplicationManager.domaine != null && ApplicationManager.userCredentials != null) {
                sendRegistrationToServer(token);

                // Subscribe to topic channels
                subscribeTopics();
                // [END get_token]

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(Constants.IS_FCM_TOKEN_SENT_TO_SERVER, true).apply();
                // [END register_for_fcm]
            }
        });

        instanceId.addOnFailureListener(task -> {
            Log.d(TAG, "Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Constants.IS_FCM_TOKEN_SENT_TO_SERVER, false).apply();
        });

        instanceId.addOnCompleteListener(task -> {
            // Notify UI that registration has completed, so the progress indicator can be hidden.
            Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        });
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        ExecutorService executor = Executors.newFixedThreadPool(1);
        CreateEndpointJob worker = new CreateEndpointJob(getApplicationContext());

        //TODO put application ARN in a property file
        worker.setThreadProperties(token,
                ApplicationManager.domaine+"\\"+ApplicationManager.userCredentials.getUsername(),
                getString(R.string.aws_application_arn));
        worker.run();
        executor.execute(worker);
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
