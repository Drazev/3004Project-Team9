import { ToastContainer } from 'react-bootstrap';
import { useNotificationQueue, useMaxNotifications } from '../../stores/notificationStore';
import Notification from './Notification';

const Notifications = () => {
    const notificationQueue = useNotificationQueue();
    const maxNotifications = useMaxNotifications();

    const renderNotifications = () => {
        let notificationToRender = notificationQueue.slice(-maxNotifications).reverse();
        return notificationToRender.map((notification, index) => {
            return <Notification key={index} notification={notification} />
        });
    }

    return (
        <ToastContainer position="bottom-center">
            {renderNotifications()}
        </ToastContainer>
    )

};
export default Notifications;