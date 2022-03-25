import PlayerHand from "../components/player/PlayerHand";
import QuestDisplay from "./quest/QuestDisplay";
import CardImages from "../assets/images/index";
import Popup from "../components/popups/Popup";
import { drawCard, setupComplete, participantSetupComplete } from "../services/clientSocket";
import { useName, usePlayerHands, useTurn, useSponsorRequest, useActivePlayers, useIsSponsoring, useSetIsSponsoring, useJoinRequest, useFoeStageStart, useStoryCard, useSetFoeStageStart} from "../stores/generalStore";
import { useNotifyStageStart, useNotifyStageEnd, useNotifyQuestEnd, useSetNotifyStageStart, useSetNotifyStageEnd, useSetNotifyQuestEnd, useNotifyHandOversize, useSetNotifyHandOversize, useNotifyHandNotOversize, useSetNotifyHandNotOversize } from "../stores/notificationStore";
import { usePlayerPlayAreas, useStageAreas } from "../stores/playAreaStore";
import { Button } from "react-bootstrap";
import React, { useState } from "react";
import "./GameBoard.css";

function GameBoard(props) {
    const init = 80;
    const jump = 240;
    const name = useName();
    const hands = usePlayerHands();
    const active = usePlayerPlayAreas();
    const stageAreas = useStageAreas();
    const turn = useTurn();
    const sponsorRequest = useSponsorRequest();
    const isSponsoring = useIsSponsoring();
    const setIsSponsoring = useSetIsSponsoring();
    const joinRequest = useJoinRequest();
    const activePlayers = useActivePlayers();
    const storyCard = useStoryCard();
    const [popup, setPopup] = useState(true);
    const [foeStageStart, setFoeStageStart] = [useFoeStageStart(), useSetFoeStageStart()];
    const [notifyStageStart, setNotifyStageStart] = [useNotifyStageStart(), useSetNotifyStageStart()];
    const [notifyStageEnd, setNotifyStageEnd] = [useNotifyStageEnd(), useSetNotifyStageEnd()];
    const [notifyQuestEnd, setNotifyQuestEnd] = [useNotifyQuestEnd(), useSetNotifyQuestEnd()];
    const [notifyHandOversize, setNotifyHandOversize] = [useNotifyHandOversize(), useSetNotifyHandOversize()];
    const [notifyHandNotOverSize, setNotifyHandNotOversize] = [useNotifyHandNotOversize(), useSetNotifyHandNotOversize()];

    let myHandArr = [false, false, false, false];
    let myPlayerID = -1;
    for (let i = 0; i < hands.length; i++) {
        if (hands[i].playerName === name) {
            myHandArr[i] = true;
            myPlayerID = hands[i].playerId;
        }
    }

    const findPlayAreaById = (id) => {
        const finder = (playArea) => {
            if (playArea.id === id) {
                return true;
            }
            return false;
        }
        return finder
    }

    const getActiveCard = (playerId) => {
        return active.find(findPlayAreaById(playerId)) != undefined
            ? active.find(findPlayAreaById(playerId)).cardsInPlay : []
    }

    const renderAllHands = () => {
        var allHands = [];
        for (var i = 0; i < hands.length; i++){
            var curTop = init+jump*i;
            allHands.push(
                <div style={{position:"fixed",top:curTop,left:10}}>
                    <PlayerHand
                        playerName={hands[i].playerName}
                        playerID={hands[i].playerId}
                        isTurn={(hands[0].playerName === turn)}
                        isMyHand={myHandArr[i]}
                        cardsInHand={hands[i].hand}
                        activeCards={getActiveCard(hands[i].playerId)}
                        rank={CardImages.Rank_Squire/*hands[0].rank*/}
                        numShields={5/*hands[0].shields*/}
                        shield={CardImages.Shield_3}
                        numStages={stageAreas.length}
                    ></PlayerHand>
                </div>
            );
        }
        return <tbody>{allHands}</tbody>;
    }

    return (
        <div id="GameBoard">
            <div id="allHands">
                {renderAllHands()}
            </div>
            <div className="decks">
                <img src={CardImages.Back_Adventure} className="deck"></img>
                <img src={CardImages.Back_Story} className="deck"></img>
                <button className="drawButton" onClick={() => drawCard(name, myPlayerID)} style={{ left: "123px" }}>Draw</button>
            </div>

            <div className="questDisplay">
                <QuestDisplay></QuestDisplay>
            </div>

            {(popup && name === sponsorRequest) &&
                <div id="sponsor-popup">
                    <Popup popupType="SPONSORQUEST" setPopup={setPopup}></Popup>
                </div>
            }
            {(name === sponsorRequest) && isSponsoring &&
                (<div id="finish-setup">
                    <Button
                        onClick={() => {
                            setupComplete(name, myPlayerID, setIsSponsoring);
                        }} style={{}}>Finished Sponsoring
                    </Button>
                </div>)
            }
            {(popup && joinRequest && name !== sponsorRequest) &&
                <div id="join-popup">
                    <Popup popupType="JOINQUEST" setPopup={setPopup}></Popup>
                </div>
            }
            {(name !== sponsorRequest && foeStageStart) &&
                (<div id="finish-setup">
                    <Button
                        onClick={() => {
                            participantSetupComplete(name, myPlayerID);
                        }} style={{}}>Participant Setup Complete
                    </Button>
                </div>)
            }
            {(notifyStageStart) &&
                <div id="foe-stage-start-popup">
                    <Popup popupType="FOESTAGESTART" setPopup={setNotifyStageStart}></Popup>
                </div>
            }
            {(notifyStageEnd) &&
                <div id="foe-stage-end-popup">
                    <Popup popupType="FOESTAGEEND" setPopup={setNotifyStageEnd}></Popup>
                </div>
            }
            {(notifyQuestEnd) &&
                <div id="quest-end-popup">
                    <Popup popupType="QUESTEND" setPopup={setNotifyQuestEnd}></Popup>
                </div>
            }
            {(notifyHandOversize) &&
                <div id="hand-oversize-popup">
                    <Popup popupType="HANDOVERSIZE" setPopup={setNotifyHandOversize}></Popup>
                </div>
            }
            {(notifyHandNotOverSize) &&
                <div id="hand-not-oversize-popup">
                    <Popup popupType="HANDNOTOVERSIZE" setPopup={setNotifyHandNotOversize}></Popup>
                </div>
            }

        </div>
    );
}

export default GameBoard;