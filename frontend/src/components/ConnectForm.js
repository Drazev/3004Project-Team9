import React, { useState } from "react";
import { connect } from "../ClientSocket";

import {
  useName,
  useSetConnected,
  useSetName,
  useAddNewMessage,
} from "../Store";

const ConnectForm = () => {
  const name = useName();
  const addNewMessage = useAddNewMessage();
  const setConnected = useSetConnected();

  const setName = useSetName();

  // Preprocess before making connection request
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!name) {
      alert("Please enter your name");
      return;
    }
    connect(setConnected, addNewMessage);
  };

  return (
    <form className="m-2" onSubmit={handleSubmit}>
      <div className="input-group mb-2 mt-4">
        <input
          type="text"
          className="form-control"
          placeholder="Enter your name"
          value={name}
          onChange={(e) => {
            setName(e.target.value);
          }}
        />
      </div>
      <button type="submit" className="btn btn-primary">
        Connect
      </button>
    </form>
  );
};

export default ConnectForm;
