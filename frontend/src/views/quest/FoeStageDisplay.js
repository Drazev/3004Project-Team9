import React from "react";
import { usePlayerHands, useActivePlayers } from "../../stores/generalStore";
import { usePlayerPlayAreas } from "../../stores/playAreaStore";
import BigCard from "../../components/cards/BigCard";
import Card from "../../components/cards/Card";
import "../GameBoard.css";

function FoeStageDisplay(props){
  const activePlayers = useActivePlayers().remainingPlayers;
  const hands = usePlayerHands();
  const playerPlayAreas = usePlayerPlayAreas();

  const getBP = (playerId) => {
    for(let i = 0; i < playerPlayAreas.length; i++){
      if(playerPlayAreas[i].id === playerId && playerPlayAreas[i].battlePoints != null){
        return playerPlayAreas[i].battlePoints;
      }
    }
    for(let i = 0; i < activePlayers.length; i++){
      if(activePlayers[i].playerId == playerId && activePlayers[i].rankBattlePoints != null){
        return activePlayers[i].rankBattlePoints;
      }
    }
    return 0;
  }

  const RenderActiveCards = () => {
    let render = [];
    let jump = 55;
    let curLeft = jump*-1;
    let curActiveCards = props.currentStage.activeCards;
    for(let i = 0; i < curActiveCards.length; i++){
      curLeft += jump;
      render.push(
        <div style={{position:'absolute', left:curLeft}}>
          <Card cardId={curActiveCards[i].cardID} key={curActiveCards[i].cardID} cardImage={curActiveCards[i].imgSrc}></Card>
        </div>
      )
    }
    return <div>{render}</div>
  }

  const RenderPlayerCurrentBP = () => {
    let render = [];
    activePlayers.forEach(player => {
      hands.forEach(hand => {
        if(hand.playerName === player.name) {
          render.push(<h4 key={hand.playerName} style={{textAlign: "left", color:"white",width:600}}>{hand.playerName} Current BP: {getBP(hand.playerId)}</h4>)
        }
      });
    });
    return <div>{render}</div>
  }
  const totalBP = props.currentStage.battlePoints;
  return (
    <div>
      <div style={{position: "absolute", top:242}}>
        {(props.currentStage.stageCard != null) && <BigCard cardId={props.currentStage.stageCard.cardID} key={props.currentStage.stageCard.cardID} cardImage={props.currentStage.stageCard.imgSrc}></BigCard>}
      </div>
      <div style={{position: "absolute", top:350, left:100}}>
        {RenderActiveCards()}
      </div>
      <div style={{position: "absolute", width:240, top:245, left:150}}>
        <h4 style={{color: "white"}}>Foe Battle Points: {totalBP}</h4>
      </div>
      <div style={{position: "absolute"}}>
        {/* {props.currentStage.stageCard.isBoosted && (
          <h4 style={{color: "white"}}>Foe is Boosted</h4>
        )} */}
      </div>
      <div style={{position: "absolute", top:430, left:50}}>
        {RenderPlayerCurrentBP()}
      </div>
    </div>
  );
};

export default FoeStageDisplay;
