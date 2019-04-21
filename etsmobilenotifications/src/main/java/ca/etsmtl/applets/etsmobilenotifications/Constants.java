package ca.etsmtl.applets.etsmobilenotifications;

/**
 * Created by Sonphil on 19-04-19.
 */

final class Constants {
    static final int MALFORMED_PROPERTIES_ERROR_CODE = 1;
    static final int CREDENTIAL_RETRIEVAL_FAILURE_ERROR_CODE = 2;
    static final int FILE_ACCESS_FAILURE_ERROR_CODE = 3;
    static final int NOT_FOUND_ERROR_CODE = 4;
    static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "fcm_fallback_notification_channel";
    static final String SNS_ARN_ENDPOINT = "SNS_ARN_ENDPOINT";
    static final String RECEIVED_NOTIF = "RECEIVED_NOTIF";
    static final String REGISTRATION_COMPLETE = "registrationComplete";
    static final String USER_NAME_PREF_KEY = "EtsMobileNotificationsUserName";
    static final String MON_ETS_DOMAINE_PREF_KEY = "EtsMobileNotificationsMonEtsDomaine";
    static final String USER_LOGGED_IN_PREF_KEY = "EtsMobileNotificationsUserLoggedIn";
    static final int NOTIFICATIONS_SUMMARY_ID = 0;
    static final String NOTIFICATIONS_GROUP_KEY = "ca.etsmtl.applets.etsmobilenotifications.NOTIFICATIONS";
}
