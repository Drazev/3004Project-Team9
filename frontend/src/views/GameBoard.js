import PlayerHand from "../components/player/PlayerHand";
import QuestDisplay from "./quest/QuestDisplay";
import CardImages from "../assets/images/index";
import Popup from "../components/popups/Popup";
import { drawCard, setupComplete, participantSetupComplete, tournamentSetupComplete } from "../services/clientSocket";
import { useName, usePlayerHands, useTurn, useSponsorRequest, useIsSponsoring, useSetIsSponsoring, useJoinRequest, useFoeStageStart, useTestStageStart, usePlayers, useActivePlayers, useHandOversize } from "../stores/generalStore";
import { usePlayerPlayAreas, useStageAreas } from "../stores/playAreaStore";
import { useTournamentJoinRequest, useSetTournamentJoinRequest, useTournamentStageStart, useTournamentSetup, useSetTournamentSetup } from "../stores/tournamentStore";
import { useSponsorSearchRequest, useSetSponsorSearchRequest, useQuestJoinRequest, useSetQuestJoinRequest, useParticipantSetupRequest, useSetParticipantSetupRequest, useBidRequestSetup, useSetBidRequestSetup } from "../stores/quest/questRequestStore";
import { useCardTargetSelectionRequest, useSetCardTargetSelectionRequest, useStageTargetSelectionRequest, useSetStageTargetSelectionRequest } from "../stores/effects/effectRequestStore";
import { Button } from "react-bootstrap";
import React, { useState } from "react";
import "./GameBoard.css";

function GameBoard({}) {
    const handOversize = useHandOversize()
    const activePlayers = useActivePlayers();
    const setTournamentSetup = useSetTournamentSetup();
    const name = useName();
    const tournamentSetup = useTournamentSetup();
    const tournamentState = useTournamentStageStart();
    const [tournamentJoinRequest, setTournamentJoinRequest] = [useTournamentJoinRequest(),useSetTournamentJoinRequest()];
    const hands = usePlayerHands();
    const active = usePlayerPlayAreas();
    const stageAreas = useStageAreas();
    const turn = useTurn();
    const isSponsoring = useIsSponsoring();
    const setIsSponsoring = useSetIsSponsoring();
    const [isActive, setIsActive] = useState(false);
    const foeStageStart = useFoeStageStart();
    const [bidRequestSetup, setBidRequestSetup] = [useBidRequestSetup(), useSetBidRequestSetup()];
    const [sponsorSearchRequest, setSponsorSearchRequest] = [useSponsorSearchRequest(), useSetSponsorSearchRequest()];
    const [questJoinRequest, setQuestJoinRequest] = [useQuestJoinRequest(), useSetQuestJoinRequest()];
    const [participantSetupRequest, setParticipantSetupRequest] = [useParticipantSetupRequest(), useSetParticipantSetupRequest()];

    for(var i = 0; i < activePlayers.length; i++){
            const player = activePlayers[i];
        console.log(JSON.stringify(player) + " " + name);
        if(player.name === name && isActive == false){
            setIsActive(true);
        }
    }

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
                        key={hands[i].playerName}
                        playerName={hands[i].playerName}
                        playerID={hands[i].playerId}
                        isTurn={(hands[0].playerName === turn)}
                        isMyHand={myHandArr[i]}
                        cardsInHand={hands[i].hand}
                        activeCards={getActiveCard(hands[i].playerId)}
                        numShields={5/*curPlayer.shields*/}
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

            {sponsorSearchRequest &&
                <div id="sponsor-popup">
                    <Popup popupType="SPONSORQUEST" setPopup={setSponsorSearchRequest}></Popup>
                </div>
            }
            {questJoinRequest &&
                <div id="join-popup">
                    <Popup popupType="JOINQUEST" setPopup={setQuestJoinRequest}></Popup>
                </div>
            }
            {tournamentJoinRequest &&
                <div id="join-popup">
                    <Popup popupType="JOINTOURNAMENT" setPopup={setTournamentJoinRequest}></Popup>
                </div>
            }
            {tournamentSetup &&
                <div>
                    <Button
                        onClick={() => {
                            tournamentSetupComplete(name,myPlayerID)
                            setTournamentSetup(false)
                        }}
                    >Finish Tournament Setup</Button>
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
            {participantSetupRequest &&
                (<div id="participant-setup-complete">
                    <Button
                        onClick={() => {
                            participantSetupComplete(name, myPlayerID);
                            setParticipantSetupRequest(false);
                            // setPartSetupButton(false);
                        }}>Participant Setup Complete
                    </Button>
                </div>)
            }
        </div>
    );
}

export default GameBoard;