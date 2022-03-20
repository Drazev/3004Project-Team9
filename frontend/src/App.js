import "./App.css";
import React from "react";
import ConnectForm from "./components/ConnectForm";
import GameBoard from "./components/GameBoard";
import { useConnected, useGameStarted } from "./Stores/GeneralStore";
import { useStageAreas } from "./Stores/PlayAreaStore";
import WaitingRoom from "./components/WaitingRoom";
import background from "./Images/Quest Background.png";
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
