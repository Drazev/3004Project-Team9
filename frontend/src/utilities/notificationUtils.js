/**
 * Notifications
 * Payload:
 * {
 *    "title": string (Optional)  // Title of the notification, default will be the empty string
 *    "message": string           // custom message
 *    "imgSrc": string            // image source to show alongside the message
 *     "action": string           // (optional - TBD) the action that server wants the client to do
 * }
 */
const DEBUG = true;
let lastNotificationId = 0;

export const handleGoodNotification = (body, utilities) => {
    /**
     * Server is notifying clients of a good notification
     */
    console.log("/user/topic/notification: " + JSON.stringify(body));
    utilities.pushNotification({id: lastNotificationId++, type: "GOOD", body})
}

export const handleBadNotification = (body, utilities) => {
    /**
     * Server is notifying clients of a bad notification
     */
    console.log("/user/topic/notification: " + JSON.stringify(body));
    utilities.pushNotification({ id: lastNotificationId++, type: "BAD", body})
}

export const handleInfoNotification = (body, utilities) => {
    /**
     * Server is notifying clients of a info notification
     */
    console.log("/user/topic/notification: " + JSON.stringify(body));
    utilities.pushNotification({ id: lastNotificationId++, type: "INFO", body})
}

export const handleWarningNotification = (body, utilities) => {
    /**
     * Server is notifying clients of a warning notification
     */
    console.log("/user/topic/notification: " + JSON.stringify(body));
    utilities.pushNotification({ id: lastNotificationId++, type: "WARNING", body })
}

export const handleDebugNotification = (body, utilities) => {
    /**
     * Server is notifying clients of a debug notification
     */
    if (DEBUG) {
        console.log("/user/topic/notification: " + JSON.stringify(body));
        utilities.pushNotification({ id:lastNotificationId++, type: "DEBUG", body });
    }
}
