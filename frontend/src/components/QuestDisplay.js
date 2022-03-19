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
    const currentStageNum = useCurrentStage();
    const currentStageObject = stages.find(obj => obj.stageNum === currentStageNum);
    /*const currentStageObject = stages.filter(obj => {
        return obj.stageNum === currentStage;
    });*/

    
    const getStages = () => {
        let x = [];
        for(let i = 0; i <= props.numStages; i++){
            let curStage = stages.find(obj => obj.stageNum === i);
            x.push(curStage);
        }
        return x;
    };
    const RenderStages = getStages()?.map((stage) => (
            (currentStageNum < stage.stageNum)? (
                <BigCard cardId={stage.stageCard.cardID} key={stage.stageCard.cardID} cardImage={CardImages.Back_Adventure} numCards={stage.activeCards.length}></BigCard>
            ) : (
                <BigCard cardId={stage.stageCard.cardID} key={stage.stageCard.cardID} cardImage={stage.stageCard.imgsrc} numCards={stage.activeCards.length}></BigCard>
            )
    ));

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
