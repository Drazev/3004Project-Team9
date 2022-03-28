import create from 'zustand';

const useStore = create((set) => ({
    notificationDelay: 7000,
    maxNotifications: 5,
    notificationQueue: [
        // { id: 0, show: true, type: "BAD", body: { "title": "This is a bad notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 1, show: true, type: "GOOD", body: { "title": "This is a good notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 2, show: true, type: "DEBUG", body: { "title": "This is a debug notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 3, show: true, type: "WARNING", body: { "title": "This is a warning notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 4, show: true, type: "INFO", body: { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 6, show: true, type: "INFO", body: { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 7, show: true, type: "INFO", body: { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 8, show: true, type: "INFO", body: { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 9, show: true, type: "INFO", body: { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 10, show: true,  type: "INFO", body: { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
    ],
    pushNotification: (newNotification) => set((current) => ({
        // Add the new notification to the back of the queue
        notificationQueue: [...(current.notificationQueue.slice(-current.maxNotifications)), newNotification],
    })),
    removeNotification: (notificationId) => set((current) => ({
        // Remove a notification from the queue by id
        notificationQueue: current.notificationQueue.filter((notification) => notification.id !== notificationId)
    })),
    hideNotification: (notificationId) => set((current) => ({
        // Hide a notification from the queue by id
        notificationQueue: current.notificationQueue.map((notification) => {
            if (notification.id === notificationId) {
                return { ...notification, show: false };
            }
            return notification;
        })
    })),
}));

/**
 * Getter for non-component use
 */
export const notificationStore = () => useStore.getState();

/**
 * Getters
 */
export const useMaxNotifications = () => useStore((state) => state.maxNotifications);
export const useNotificationDelay = () => useStore((state) => state.notificationDelay);
export const useNotificationQueue = () => useStore((state) => state.notificationQueue);

/**
 * Setters
 */
export const usePushNotification = () => useStore((state) => state.pushNotification);
export const useRemoveNotification = () => useStore((state) => state.removeNotification);
export const useHideNotification = () => useStore((state) => state.hideNotification);
