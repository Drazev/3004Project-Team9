import { connect } from "../../services/clientSocket";

import "./ConnectForm.css";

import * as generalStore from "../../stores/generalStore";
import * as playAreaStore from "../../stores/playAreaStore";
import * as notificationStore from "../../stores/notificationStore";

const ConnectForm = () => {

  const connectFunctions = {
    name: generalStore.useName(),

    addNewPlayer: generalStore.useAddNewPlayer(),
    setConnected: generalStore.useSetConnected(),
    setPlayers: generalStore.useSetPlayers(),
    setGameStarted: generalStore.useSetGameStarted(),
    setTurn: generalStore.useSetTurn(),
    setJoinRequest: generalStore.useSetJoinRequest(),

    updateHand: generalStore.useUpdateHand(),
    updatePlayer: generalStore.useUpdatePlayer(),
    updateStageArea: playAreaStore.useUpdateStageArea(),
    updatePlayerPlayArea: playAreaStore.useUpdatePlayerPlayArea(),

    setHandOversize: generalStore.useSetHandOversize(),
    setStoryCard: generalStore.useSetStoryCard(),
    setActivePlayers: generalStore.useSetActivePlayers(),
    setFoeStageStart: generalStore.useSetFoeStageStart(),

    notifySponsorRequest: generalStore.useSetSponsorRequest(),

    setNotifyStageStart: notificationStore.useSetNotifyStageStart(),
    setNotifyStageEnd: notificationStore.useSetNotifyStageEnd(),
    setNotifyQuestEnd: notificationStore.useSetNotifyQuestEnd(),
    setNotifyHandOversize: notificationStore.useSetNotifyHandNotOversize(),
    setNotifyHandNotOversize: notificationStore.useSetNotifyHandNotOversize(),
    addNewNotification: notificationStore.useAddNewNotification()
  }

  const setName = generalStore.useSetName();
  // Preprocess before making connection request
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!connectFunctions.name) {
      alert("Please enter your name");
      return;
    }
    if (connect(connectFunctions) === false) {
      alert("Name already taken, please choose a different one")
    }
  };

  return (
    <form className="m-3" onSubmit={handleSubmit}>
      <div className="input-group mb-2 mt-4">
        <input
          type="text"
          className="form-control"
          placeholder="Enter your name"
          value={connectFunctions.name}
          onChange={(e) => {
            setName(e.target.value);
          }}
        />
      </div>
      <button type="submit" className="btn btn-primary">
        Connect
      </button>
    </form>
  );
};

export default ConnectForm;
