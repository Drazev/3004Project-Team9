import React from "react";
import { usePlayerHands, useActivePlayers } from "../../stores/generalStore";
import BigCard from "../../components/cards/BigCard";
import Card from "../../components/cards/Card";
import "../GameBoard.css";

function FoeStageDisplay(props){
  const activePlayers = useActivePlayers().remainingPlayers;
  const hands = usePlayerHands();
  const getBP = (name) => {
    let totalBP = 5
    for(let i = 0; i < hands.length; i++){
      if(hands[i].name === name){
        let curHand = hands[i].hand;
        for(let b = 0; b < curHand.length; b++){
          totalBP += curHand[b].battlePoints;
        }
      }
    }
    return totalBP;
  }
  const RenderActiveCards = props.currentStage.activeCards?.map((card) => (
    <Card cardId={card.cardID} key={card.cardID} cardImage={card.imgSrc}></Card>
));
  const RenderPlayerCurrentBP = () => {
    let render = [];
    console.log("this one tom" + JSON.stringify(activePlayers));
    activePlayers.forEach(player => {
      hands.forEach(hand => {
        if(hand.name === player.name) {
          render.push(<h4 key={hand.name} style={{textAlign: "left"}}>{hand.name} Current BP: {hand.battlePoints}</h4>)
        }
      });
    });
    return <div>{render}</div>
  }
    const totalBP = props.currentStage.battlePoints;
  return (
    <div>
      <div style={{position: "absolute"}}>
        {(props.currentStage.stageCard != null) && <BigCard cardId={props.currentStage.stageCard.cardID} key={props.currentStage.stageCard.cardID} cardImage={props.currentStage.stageCard.imgSrc}></BigCard>}
      </div>
      <div style={{position: "absolute"}}>
        {RenderActiveCards}
      </div>
      <div style={{position: "absolute"}}>
        <h4 style={{color: "white"}}>Total Battle Points: {totalBP}</h4>
      </div>
      <div style={{position: "absolute"}}>
        {/* {props.currentStage.stageCard.isBoosted && (
          <h4 style={{color: "white"}}>Foe is Boosted</h4>
        )} */}
      </div>
      <div style={{position: "absolute"}}>
        {RenderPlayerCurrentBP()}
      </div>
    </div>
  );
};

export default FoeStageDisplay;
