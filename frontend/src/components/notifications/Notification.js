import { useState, useEffect } from "react";
import Toast from 'react-bootstrap/Toast';
import ToastHeader from 'react-bootstrap/ToastHeader';
import ToastBody from 'react-bootstrap/ToastBody';
import { useRemoveNotification, useNotificationDelay, useHideNotification } from "../../stores/notificationStore";
import "./Notifications.css";

const Notification = ({ notification }) => {
    const [bgColor, setBgColor] = useState(null);
    const removeNotification = useRemoveNotification();
    const delay = useNotificationDelay();
    const hideNotification = useHideNotification();

    const onClose = () => {
        // removeNotification(notification.id);
        hideNotification(notification.id);
    }

    useEffect(() => {
        switch (notification.type) {
            case "GOOD":
                setBgColor("success");
                break;
            case "BAD":
                setBgColor("danger");
                break;
            case "INFO":
                setBgColor("info");
                break;
            case "WARNING":
                setBgColor("warning");
                break;
            case "DEBUG":
                setBgColor("secondary");
                break;
            default:
                setBgColor("primary");
                break;
        }
    }, [notification.type]);

    const renderToastHeader = () => {
        let title = notification.body.title ? notification.body.title : "";
        return (
            <div>
                <strong>{title}</strong>
            </div>
        );
    }

    const renderToastBody = () => {
        if (notification.body.imgSrc) {
            return (
                <div>
                    {notification.body.message}
                    <br/>
                    <img src={notification.body.imgSrc} alt="notification" className="notification-image"/>
                </div>
            )
        } else {
            return (
                <div>
                    {notification.body.message}
                </div>
            )
        }
    }

    return (
        <Toast bg={bgColor} animation={true} autohide={true} delay={delay} onClose={onClose} show={notification.show}>
            <ToastHeader closeButton={true}>{renderToastHeader()}</ToastHeader>
            <ToastBody> {renderToastBody()} </ToastBody>
        </Toast>
    )
}

export default Notification;