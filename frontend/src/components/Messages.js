import React from "react";
import { useMessages } from "../Stores/GeneralStore";

const Messages = () => {
  const messages = useMessages();

  return (
    <ul className="list-group list-group-flush">
      {messages.map((message, index) => (
        <li key={index} className="list-group-item">
          {message.name} says: {message.message}
        </li>
      ))}
    </ul>
  );
};

export default Messages;
