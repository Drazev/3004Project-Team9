import "./App.css";
import React from "react";
import ConnectForm from "./components/forms/ConnectForm";
import GameBoard from "./views/GameBoard";
import { useConnected, useGameStarted } from "./stores/generalStore";
import { useStageAreas } from "./stores/playAreaStore";
import WaitingRoom from "./views/waitingroom/WaitingRoom";

const App = () => {
  const stagePlayArea = useStageAreas();
  const connected = useConnected();
  const gameStarted = useGameStarted();
  return (
    <div className="App" style={{width:1914,height:975,backgroundImage:`url("https://wallpaperaccess.com/full/88167.jpg")`,backgroundSize:"cover",backgroundPosition:"center", backgroundColor:"black"}}>
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
