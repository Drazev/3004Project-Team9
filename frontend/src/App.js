import "./App.css";
import Header from "./components/Header";
import ConnectForm from "./components/ConnectForm";
import MessageForm from "./components/MessageForm";
import Messages from "./components/Messages";
import { useConnected } from "./Store";

const App = () => {
  const connected = useConnected();

  return (
    <div className="App">
      <Header />
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
