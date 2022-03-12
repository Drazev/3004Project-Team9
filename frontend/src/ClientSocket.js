import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { setGameStarted } from "./Stores/GeneralStore";


let client;

const REGISTRATION_URL = "http://localhost:8080/api/register"
const SOCK_SERVER = "http://localhost:8080/quest-game-websocket"
const START_URL = "http://localhost:8080/api/start"


export async function connect(setConnected,setGameStarted, addNewMessage, setPlayers, name, updateHand, updatePlayArea, updatePlayer) {
  console.log("Attempt connection");

  // Register player name
  let response = await fetch(REGISTRATION_URL, {
    method: "POST",
    body: JSON.stringify({
      name: name
    }),
    headers: {
      "Content-Type": "application/json"
    }
  })

  let body = await response.json();
  if (!body.confirmed || body.name !== name) {
    console.log("Connection declined or unmatch name");
    return false;
  }

  // Perform handshake with the registered name
  client = new Client({
    webSocketFactory: () => {
      return new SockJS(`${SOCK_SERVER}?name=${name}`);
    },
    reconnectDelay: 50000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });


  client.onConnect = (frame) => {
    console.log("Connection successful");
    setConnected(true);
    client.subscribe("/topic/message", (message) => {
      let body = JSON.parse(message.body);
      console.log(body);
      addNewMessage(body.name, body.message);
    });
    client.subscribe("/topic/general/next-turn", (name) => {
        console.log("Turn is now: " + name.body);
//        setTurn(name);
    });
    client.subscribe("/topic/player/hand-update", (message) => {
        let newHand = JSON.parse(message.body);
        console.log("Received hand update: " + newHand);
        updateHand(newHand);
    });
    client.subscribe("/topic/general/player-connect", (players) => {
      let body = JSON.parse(players.body);
      console.log("clientsocket players.body: " + players.body);
      const bodyKeys = Object.keys(body);
      console.log(bodyKeys);
      setPlayers(bodyKeys);
    });
    client.subscribe("/topic/general/game-start", () => {
      console.log("setting game started true");
      setGameStarted(true);
    });
    client.subscribe("topic/player/hand-oversize" , (message) => {
      let body = JSON.parse(message.body);
      console.log("Player Hand Oversize: \n" + body + " \n\n ");
    });
    client.subscribe("topic/player/player-update", (message) => {
      let body = JSON.parse(message.body);
      updatePlayer(body);
    });
  };
/*
  client.subscribe("/topic/play-areas/play-area-changed", (data) => {
    let body = JSON.parse(data.body);
    console.log("Play Area Update recieved for playerId: "+body.playerId);
    updatePlayArea(body);
});*/

  client.onDisconnect = () => {
    disconnect();
    setConnected(false);
  };

  client.onStompError = function (frame) {
    console.log("Broker reported error: " + frame.headers["message"]);
    console.log("Additional details: " + frame.body);
  };

  client.activate();
  return true;

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

export function drawCard(name, cardId) {
  console.log("Draw Card: \nName: " + name + "\nCardID: " + cardId);
  client.publish({
    destination: "/app/general/player-draw-card",
    body: JSON.stringify({
      name: name,
      cardId: cardId, //server will not care about this
    }),
  });
}

export function discardCard(name, cardId) {
  console.log("Discard Card: \nName: " + name + "\nCardID: " + cardId);
  client.publish({
    destination: "/app/general/player-discard-card",
    body: JSON.stringify({
      name: name,
      cardId: cardId,
    }),
  });
}

export function playCard(name, cardId, card) {
  console.log("Play Card: \nName: " + name + "\nCardID: " + cardId + "\nCard: " + JSON.stringify(card));
  client.publish({
    destination: "/app/general/player-play-card",
    body: JSON.stringify({
      name: name,
      cardId: cardId,
      card: card,
    }),
  });
}

export function disconnect() {
  client.disconnect();
}

export function startGame(){
  fetch(START_URL,
  {method: "POST",
  headers: {
      "Content-Type": "application/json"
    }} );
    //setGameStarted(true);
}

