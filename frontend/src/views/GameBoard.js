import PlayerHand from "../components/player/PlayerHand";
import QuestDisplay from "./quest/QuestDisplay";
import CardImages from "../assets/images/index";
import Popup from "../components/popups/Popup";
import { drawCard, setupComplete, participantSetupComplete } from "../services/clientSocket";
import { useName, usePlayerHands, useTurn, useSponsorRequest, useIsSponsoring, useSetIsSponsoring, useJoinRequest, useFoeStageStart } from "../stores/generalStore";
import { usePlayerPlayAreas, useStageAreas } from "../stores/playAreaStore";
import { Button } from "react-bootstrap";
import React, { useState } from "react";
import "./GameBoard.css";

function GameBoard({}) {
    const name = useName();
    const hands = usePlayerHands();
    const active = usePlayerPlayAreas();
    const stageAreas = useStageAreas();
    const turn = useTurn();
    const sponsorRequest = useSponsorRequest();
    const isSponsoring = useIsSponsoring();
    const setIsSponsoring = useSetIsSponsoring();
    const joinRequest = useJoinRequest();
    const [popup, setPopup] = useState(true);
    const foeStageStart = useFoeStageStart();

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
        const init = 20;
        const jump = 240;
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
        return <div>{allHands}</div>;
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

            <div className="questDisplay" style={{left:1150,top:100,position:"fixed"}}>
                <QuestDisplay></QuestDisplay>
            </div>

            {popup && name === sponsorRequest &&
                <div id="sponsor-popup">
                    <Popup popupType="SPONSORQUEST" setPopup={setPopup}></Popup>
                </div>
            }
            {popup && joinRequest && !isSponsoring &&
                <div id="join-popup">
                    <Popup popupType="JOINQUEST" setPopup={setPopup}></Popup>
                </div>
            }
            {isSponsoring &&
                (<div id="finish-setup">
                    <Button
                        onClick={() => {
                            setupComplete(name, myPlayerID, setIsSponsoring);
                        }} style={{}}>Finished Sponsoring
                    </Button>
                </div>)
            }
            {foeStageStart && !isSponsoring &&
                (<div id="finish-setup">
                    <Button
                        onClick={() => {
                            participantSetupComplete(name, myPlayerID);
                            // setPartSetupButton(false);
                        }}>Participant Setup Complete
                    </Button>
                </div>)
            }
        </div>
    );
}

export default GameBoard;