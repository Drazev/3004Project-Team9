import React from "react";
import { useMessages } from "../Store";

const Messages = () => {
  const messages = useMessages();

  return (
    <div className="Chat">
    <ul className="list-group">
      {messages.map((message, index) => (
        <li key={index} className="list-group-item">
          {message.name} says: {message.message}
        </li>
      ))}
    </ul>
    </div>
  );
};

export default Messages;
