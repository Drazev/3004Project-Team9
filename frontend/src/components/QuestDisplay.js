import React from "react";
import FoeStageDisplay from "./FoeStageDisplay";
import Card from "./Card";
import CardImages from "../Images/index";
import BigCard from "./BigCard";
import "./QuestDisplay.css";
import { useStageAreas, useCurrentStage } from "../Stores/PlayAreaStore";


const activePlayers = ["John","Yusuf"];
const questSponsor = "player3";
const playerActiveCards = {
    player1: [], 
    player2: [], 
    player3: [], 
    player4: []
};
let currentTurn = "player1";

const QuestDisplay = (props) => {
    const stages = useStageAreas();
    const numStages = stages.length-1;
    const currentStageNum = useCurrentStage();
    const currentStageObject = stages.find(obj => obj.stageNum === currentStageNum);

    console.log("Current Stage: " + JSON.stringify(currentStageObject));
    if(currentStageObject){
        console.log("Current Stage Object isBoosted: " + currentStageObject.isBoosted);
    }
    const getStages = () => {
        let x = [];
        for(let i = 0; i <= numStages; i++){
            let curStage = stages.find(obj => obj.stageNum === i);
            x.push(curStage);
        }
        return x;
    };
    const RenderStages = getStages()?.map((stage) => {
        if (stage == null || stage.stageCard == null) {
            return;
        } else {
            return (
                (currentStageNum < stage.stageNum)? (
                    <BigCard cardId={stage.stageCard.cardID} key={stage.stageCard.cardID} cardImage={CardImages.Back_Adventure} numCards={stage.activeCards.length}></BigCard>
                ) : (
                    <BigCard cardId={stage.stageCard.cardID} key={stage.stageCard.cardID} cardImage={stage.stageCard.imgSrc} numCards={stage.activeCards.length}></BigCard>
                )
            )
        }
    });

  return (
    <div>
        <div style={{width:1000,top:60,left:1050,position:'fixed'}}>
            {currentStageObject && <h3>Quest Stages</h3>}
        </div>
        <div style={{width:1000,top:100,left:1300,position:'fixed'}}>
            {RenderStages}
        </div>
        <div>
            {currentStageObject && <FoeStageDisplay activePlayers={activePlayers} currentStage={currentStageObject}></FoeStageDisplay>}
        </div>
    </div>
  );
};

export default QuestDisplay;
