package ca.etsmtl.applets.etsmobilenotifications;

import java.util.List;

/**
 * Created by Sonphil on 21-04-19.
 */

/**
 * Manager for push {@link MonETSNotification}s
 */
public interface EtsMobileNotificationManager {
    /**
     * Save new notification on device
     *
     * @param newNotification       New notification to save
     * @param previousNotifications Previous notifications
     */
    void saveNewNotification(MonETSNotification newNotification, List<MonETSNotification> previousNotifications);

    /**
     * Get the previous notifications persisted on the device
     *
     * @return The previous notifications persisted on the device
     */
    List<MonETSNotification> getNotifications();
}
