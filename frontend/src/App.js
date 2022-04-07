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
    <div className="App background-image">
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
