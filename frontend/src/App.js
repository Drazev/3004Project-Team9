import "./App.css";
import React from "react";
import ConnectForm from "./components/forms/ConnectForm";
import GameBoard from "./views/GameBoard";
import { useConnected, useGameStarted, useSponsorName } from "./stores/generalStore";
import { useStageAreas } from "./stores/playAreaStore";
import WaitingRoom from "./views/waitingroom/WaitingRoom";
import Notifications from "./components/notifications/Notifications";

const App = () => {
  const connected = useConnected();
  const gameStarted = useGameStarted();
  const sponsorName = useSponsorName();
  const stageAreas = useStageAreas();
  return (
    // <div className="App" style={{width:1914,height:975,backgroundImage:`url("https://wallpaperaccess.com/full/88167.jpg")`,backgroundSize:"cover",backgroundPosition:"center", backgroundColor:"black"}}>
    <div className="App background-image">
      {/* <div className="background-image"></div> */}
      {gameStarted ? (
          <GameBoard/>
      ) : <>
            {connected ? (
              <>
                <WaitingRoom/>
              </>
              ) : (
                <ConnectForm />
              )}
         </>
      }
      <Notifications/>
    </div>
  );
};

export default App;
