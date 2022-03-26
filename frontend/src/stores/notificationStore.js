import create from 'zustand';

const useStore = create((set) => ({
    notificationDelay: 3000,
    maxNotifications: 5,
    notificationQueue: [
        // { id: 0, "type": "BAD", "body": { "title": "This is a bad notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 1, "type": "GOOD", "body": { "title": "This is a good notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 2, "type": "DEBUG", "body": { "title": "This is a debug notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 3, "type": "WARNING", "body": { "title": "This is a warning notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 4, "type": "INFO", "body": { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 6, "type": "INFO", "body": { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 7, "type": "INFO", "body": { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 8, "type": "INFO", "body": { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 9, "type": "INFO", "body": { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
        // { id: 10, "type": "INFO", "body": { "title": "This is an info notification", "message": "You suk", "imgSrc": "", "action": "" } },
    ],
    pushNotification: (newNotification) => set((current) => ({
        // Add the new notification to the back of the queue
        notificationQueue: [...(current.notificationQueue.slice(-current.maxNotifications)), newNotification],
    })),
    removeNotification: (notificationId) => set((current) => ({
        // Remove a notification from the queue by id
        notificationQueue: current.notificationQueue.filter((notification) => notification.id !== notificationId)
    })),
}));

export const useMaxNotifications = () => useStore((state) => state.maxNotifications);
export const useNotificationDelay = () => useStore((state) => state.notificationDelay);
export const useNotificationQueue = () => useStore((state) => state.notificationQueue);

export const usePushNotification = () => useStore((state) => state.pushNotification);
export const useRemoveNotification = () => useStore((state) => state.removeNotification);
