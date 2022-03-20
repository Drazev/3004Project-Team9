import React from "react";
import { usePlayerHands } from "../Stores/GeneralStore";
import BigCard from "./BigCard";
import Card from "./Card";
import "./GameBoard.css";

function FoeStageDisplay(props){
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
  const RenderPlayerCurrentBP = hands?.map((hand) => (
    (props.activePlayers.includes(hand.name)) ? (
      <h4 style={{textAlign: "left"}}>{hand.name} Current BP: {getBP(hand.name)}</h4>
    ):(<></>)
  ));
    const totalBP = props.currentStage.battlePoints;
  return (
    <div>
      <div style={{position: "absolute", left:1300, top:283}}>
        {(props.currentStage.stageCard != null) && <BigCard cardId={props.currentStage.stageCard.cardID} key={props.currentStage.stageCard.cardID} cardImage={props.currentStage.stageCard.imgSrc}></BigCard>}
      </div>
      <div style={{position: "absolute", left:1397, top:397}}>
        {RenderActiveCards}
      </div>
      <div style={{position: "absolute", left:1450, top:297}}>
        <h4 style={{color: "white"}}>Total Battle Points: {totalBP}</h4>
      </div>
      <div style={{position: "absolute", left:1450, top:335}}>
        {/* {props.currentStage.stageCard.isBoosted && (
          <h4 style={{color: "white"}}>Foe is Boosted</h4>
        )} */}
      </div>
      <div style={{position: "absolute", left:1340, top:480}}>
        {RenderPlayerCurrentBP}
      </div>
    </div>
  );
};

export default FoeStageDisplay;
