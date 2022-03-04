import "./App.css";
import Header from "./components/Header";
import Card from "./components/Card";
import ConnectForm from "./components/ConnectForm";
import GameBoard from "./components/GameBoard";
import MessageForm from "./components/MessageForm";
import Messages from "./components/Messages";
import { useConnected, useGameStarted } from "./Store";
import WaitingRoom from "./components/WaitingRoom";

const App = () => {
  const connected = useConnected();
  const gameStarted = useGameStarted();


  return (
    <div className="App" style={{width:1000,height:1000}}>
      <Header />
      {gameStarted ? (
          <GameBoard />
      ) : <>
            {connected ? (
              <>
                <WaitingRoom />
                {/* <MessageForm /> */}
                <Messages />
              </>
              ) : (
                <ConnectForm />
              )}
         </>
      }
      
    </div>
  );
};

export default App;
