import "./App.css";
import Header from "./components/Header";
import Card from "./components/Card";
import ConnectForm from "./components/ConnectForm";
import MessageForm from "./components/MessageForm";
import Messages from "./components/Messages";
import { useConnected } from "./Store";

const App = () => {
  const connected = useConnected();

  return (
    <div className="App">
      <Header />
      <Card isInHand={true} isTurn={true} canGrow={true} />
      {connected ? (
        <>
          <MessageForm />
          <Messages />
        </>
      ) : (
        <ConnectForm />
      )}
    </div>
  );
};

export default App;
