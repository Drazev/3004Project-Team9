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
import img from './Images/Quest-Board-Background-with-Logo.png';
///Users/Johnathan/Desktop/3004Project-Team9/frontend/src/Images/Quest Board Background with Logo.png
const App = () => {
  const connected = useConnected();
  const gameStarted = useGameStarted();
  const playerHands = usePlayerHands();
//backgroundImage:`url(${Background1})`
  return (
    <div className="App" style={{width:1914,height:975,backgroundImage:`url(${img})`,backgroundSize:"cover",backgroundPosition:"center"}}>
      {gameStarted ? (
          <GameBoard/>
      ) : <>
            {connected ? (
              <>
                <WaitingRoom/>
              </>
              ) : (
                <ConnectForm/>
              )}
         </>
      }
      
    </div>
  );
};

export default App;
