import "./App.css";
import React from "react";
import ConnectForm from "./components/ConnectForm";
import GameBoard from "./components/GameBoard";
import { useConnected, useGameStarted } from "./Stores/GeneralStore";
import WaitingRoom from "./components/WaitingRoom";
import background from "./Images/Quest-Board-Background-with-Logo.png";
const App = () => {
  const connected = useConnected();
  const gameStarted = useGameStarted();
  return (
    <div className="App" style={{width:1914,height:975/*,backgroundImage: `url(${background})`*/,backgroundSize:"cover",backgroundPosition:"center", backgroundColor:"black"}}>
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
