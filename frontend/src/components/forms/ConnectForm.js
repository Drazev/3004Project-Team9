import { connect } from "../../services/clientSocket";

import "./ConnectForm.css";

import * as generalStore from "../../stores/generalStore";
import * as playAreaStore from "../../stores/playAreaStore";
import * as notificationStore from "../../stores/notificationStore";
import * as questRequestStore from "../../stores/questRequestStore";
import * as eventRequestStore from "../../stores/eventRequestStore";

const ConnectForm = () => {

  const connectFunctions = {
    // generalStore
    name: generalStore.useName(),
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
    setSponsorName: generalStore.useSetSponsorName(),

    // notificationStore
    pushNotification: notificationStore.usePushNotification(),

    setTestStageStart: generalStore.useSetTestStageStart(),
    setMaxBid: generalStore.useSetMaxBid(),
    setMaxBidPlayer: generalStore.useSetMaxBidPlayer(),
    setCurrentBidder: generalStore.useSetCurrentBidder(),
    // questRequestStore
    setSponsorSearchRequest: questRequestStore.useSetSponsorSearchRequest(),
    setQuestJoinRequest: questRequestStore.useSetQuestJoinRequest(),

    // eventRequestStore
    setTargetSelectionRequest: eventRequestStore.useSetTargetSelectionRequest(),
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
    <form className="ConnectForm" onSubmit={handleSubmit}>
      <div>
        <input
          type="text"
          className="NameInput"
          placeholder="Enter your name"
          value={connectFunctions.name}
          onChange={(e) => {
            setName(e.target.value);
          }}
        />
      </div>
      <button type="submit" className="ConnectButton">
        Connect
      </button>
    </form>
  );
};

export default ConnectForm;
