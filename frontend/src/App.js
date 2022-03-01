import "./App.css";
import Header from "./components/Header";
import Card from "./components/Card";
import ConnectForm from "./components/ConnectForm";
import GameBoard from "./components/GameBoard";
import MessageForm from "./components/MessageForm";
import Messages from "./components/Messages";
import { useConnected } from "./Store";

const App = () => {
  const connected = useConnected();

  return (
    <div className="App" style={{width:1000,height:1000}}>
      <Header />
      {connected ? (
        <>
          <GameBoard />
        </>
      ) : (
        <ConnectForm />
      )}
    </div>
  );
};

export default App;
