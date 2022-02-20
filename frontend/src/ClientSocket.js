import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

let client;

export function connect(setConnected, addNewMessage) {
  console.log("Connect");

  client = new Client({
    webSocketFactory: () => {
      return new SockJS("http://localhost:8080/quest-game-websocket");
    },
    reconnectDelay: 50000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = (frame) => {
    setConnected(true);
    client.subscribe("/topic/message", (message) => {
      let body = JSON.parse(message.body);
      console.log(body);
      addNewMessage(body.name, body.message);
    });
  };

  client.onDisconnect = () => {
    setConnected(false);
  };

  client.onStompError = function (frame) {
    console.log("Broker reported error: " + frame.headers["message"]);
    console.log("Additional details: " + frame.body);
  };

  client.activate();
}

export function sendMessage(name, message) {
  client.publish({
    destination: "/app/message",
    body: JSON.stringify({
      name: name,
      message: message,
    }),
  });
}
