import "./App.css";
import React from "react";
import Header from "./components/Header";
import Card from "./components/Card";
import ConnectForm from "./components/ConnectForm";
import GameBoard from "./components/GameBoard";
import MessageForm from "./components/MessageForm";
import Messages from "./components/Messages";
import { useConnected, useGameStarted, usePlayerHands } from "./Store";
import WaitingRoom from "./components/WaitingRoom";
import Background1 from './Images/Quest Background.png';
import Background2 from './Images/Quest Board Background with Logo.png';

const App = () => {
  const connected = useConnected();
  const gameStarted = useGameStarted();
  const playerHands = usePlayerHands();


  return (
    <div className="App" style={{width:1000,height:1000,backgroundImage:'url('+${Background1}+')'}}>
      {/* <Header /> */}
      {gameStarted ? (
          <GameBoard style={{backgroundImage:""}}/>
      ) : <>
            {connected ? (
              <>
                <WaitingRoom style={{backgroundImage:""}}/>
                {/* <MessageForm /> */}
                {/* <Messages /> */}
              </>
              ) : (
                <ConnectForm style={{backgroundImage:""}}/>
              )}
         </>
      }
      
    </div>
  );
};

export default App;
