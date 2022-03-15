import "./App.css";
import React from "react";
import ConnectForm from "./components/ConnectForm";
import GameBoard from "./components/GameBoard";
import { useConnected, useGameStarted } from "./Stores/GeneralStore";
import WaitingRoom from "./components/WaitingRoom";
import Background from "./Images/Quest-Board-Background-with-Logo.png";
const App = () => {
  const connected = useConnected();
  const gameStarted = useGameStarted();
  return (
    <div className="App" style={{width:1920,height:975,backgroundSize:"cover",backgroundPosition:"center",background:`url(https://wallpaperaccess.com/full/88167.jpg)`}}>
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
