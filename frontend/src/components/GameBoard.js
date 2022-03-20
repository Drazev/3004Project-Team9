import PlayerHand from "./PlayerHand";
import QuestDisplay from "./QuestDisplay";
import CardImages from "../Images/index";
import Popup from "./Popup";
import Card from "./Card";
import {drawCard, sponsorRespond, setupComplete} from "../ClientSocket";
import {useName, usePlayerHands, usePlayers, useTurn, useSponsorRequest, useSetPopupType, usePopupType, useIsSponsoring } from "../Stores/GeneralStore";
import { useUpdatePlayArea, usePlayerPlayAreas, useStageAreas } from "../Stores/PlayAreaStore";
import {Button} from "react-bootstrap";
import React, { useState, useEffect } from "react";
import "./GameBoard.css";

function GameBoard(props){
    let init = 80;
    let jump = 240;
    const name = useName();
    const popupType = usePopupType();
    const allPlayers = usePlayers();
    let hands = usePlayerHands();
    let active = usePlayerPlayAreas();
    let stageAreas = useStageAreas();
    const turn = useTurn();
    let sponsorRequest = useSponsorRequest();
    const setPopupType = useSetPopupType();
    const isSponsoring = useIsSponsoring();
    console.log("Is Sponsoring right now: " + isSponsoring);

    const [popup, setPopup] = useState(true);

    const togglePopup = () => {
      setPopup(!popup);
      console.log("trigger popup: " + popup);
    }

    useEffect(() => {
        setPopupType("SPONSORQUEST")
    }, [])

    // hands = [{name:"Test1",isTurn:true,hand:[{cardId:1,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:54},
    // {name:"Test2",isTurn:true,hand:[{cardId:2,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:5}];

    let myHandArr = [false,false,false,false];
    let myPlayerID = -1;
    for(let i = 0; i < hands.length; i++){
        if(hands[i].playerName === name){
            myHandArr[i] = true;
            myPlayerID = hands[i].playerId;
        }
    }

    return (
        <div id="GameBoard">
            <div id="allHands">
                <PlayerHand 
                    playerName={hands[0].playerName} 
                    playerID={hands[0].playerId}
                    isTurn={(hands[0].playerName === turn)} 
                    isMyHand={myHandArr[0]} 
                    cardsInHand={hands[0].hand} 
                    activeCards={active[0].hand}
                    rank={ CardImages.Rank_Squire/*hands[0].rank*/}
                    numShields={5/*hands[0].shields*/}
                    top={init}
                    left={0}
                    shield={CardImages.Shield_3}
                    numStages={stageAreas.length}>
                </PlayerHand>
                <PlayerHand 
                    playerName={hands[1].playerName} 
                    playerID={hands[1].playerId}
                    isTurn={(hands[1].playerName === turn)} 
                    isMyHand={myHandArr[1]} 
                    cardsInHand={hands[1].hand} 
                    activeCards={active[1].hand}
                    rank={ CardImages.Rank_Squire/*hands[1].rank*/}
                    numShields={5/*hands[1].shields*/}
                    top={init+jump}
                    left={0}
                    shield={CardImages.Shield_1}
                    numStages={stageAreas.length}>
                </PlayerHand>
                {hands.length > 2 &&
                    <PlayerHand 
                        playerName={hands[2].playerName} 
                        playerID={hands[2].playerId}
                        isTurn={(hands[2].playerName === turn)} 
                        isMyHand={myHandArr[2]} 
                        cardsInHand={hands[2].hand} 
                        activeCards={active[2].hand}
                        rank={ CardImages.Rank_Squire/*hands[2].rank*/}
                        numShields={5/*hands[2].shields*/}
                        top={init+jump*2}
                        left={0}
                        numStages={stageAreas.length}
                        shield={CardImages.Shield_8}>
                    </PlayerHand>
                }
                {hands.length > 3 &&
                    <PlayerHand 
                        playerName={hands[3].playerName} 
                        playerID={hands[3].playerId}
                        isTurn={(hands[3].playerName === turn)} 
                        isMyHand={myHandArr[3]} 
                        cardsInHand={hands[3].hand} 
                        activeCards={active[3].hand}
                        rank={ CardImages.Rank_Squire/*hands[3].rank*/}
                        numShields={5/*hands[3].shields*/}
                        top={init+jump*3}
                        left={0}
                        numStages={stageAreas.length}
                        shield={CardImages.Shield_6}>
                    </PlayerHand>
                }
            </div>
            <div className="decks">
                <img src={CardImages.Back_Adventure} className="deck"></img>
                <img src={CardImages.Back_Story} className="deck"></img>
                <button className="drawButton" onClick={() => drawCard(name,myPlayerID)} style={{left: "123px"}}>Draw</button>
            </div>

            <div className="questDisplay">
                <QuestDisplay></QuestDisplay>
            </div>

            {/* <Button onClick={() => drawCard(name,0)} >Draw</Button>
            <Button onClick={() => togglePopup()}>End Turn</Button> */}

            {(popup && name === sponsorRequest) && 
                <div>
                    <Popup popupType={popupType} setPopup={setPopup}></Popup>
                </div>
            }
            {(name === sponsorRequest) && isSponsoring &&
                (<div>
                    <Button 
                        onClick={() => {
                            setupComplete(name, myPlayerID);
                        }} style={{}}>Finished Sponsoring
                    </Button>
                </div>)
            }
        </div>
    );
}

export default GameBoard;