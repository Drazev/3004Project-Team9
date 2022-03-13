import React, { useState } from "react";
import { sendMessage } from "../ClientSocket";
import { useName } from "../Stores/GeneralStore";

const MessageForm = () => {
  const [message, setMessage] = useState("");

  const name = useName();

  // Preprocess before making connection request
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!message) {
      return; // Nothing to do
    }
    sendMessage(name, message);
    setMessage("");
  };

  return (
    <form className="m-3" onSubmit={handleSubmit}>
      <div className="input-group mb-2 mt-4">
        <input
          type="text"
          className="form-control"
          placeholder="Enter your message"
          value={message}
          onChange={(e) => {
            setMessage(e.target.value);
          }}
        />
        <button type="submit" className="btn btn-primary">
          Send
        </button>
      </div>
    </form>
  );
};

export default MessageForm;
