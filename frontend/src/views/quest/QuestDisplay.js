import React from "react";
import FoeStageDisplay from "./FoeStageDisplay";
import CardImages from "../../assets/images/index";
import BigCard from "../../components/cards/BigCard";
import "./QuestDisplay.css";
import { useIsSponsoring, useFoeStageStart } from "../../stores/generalStore";
import { useStageAreas, useCurrentStage } from "../../stores/playAreaStore";

const activePlayers = ["John","Yusuf"];

const QuestDisplay = (props) => {
    const isSponsoring = useIsSponsoring();
    const foeStageStart = useFoeStageStart();
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
                ((currentStageNum >= stage.stageNum && foeStageStart == true) || (isSponsoring == true))? (
                    <BigCard cardId={stage.stageCard.cardID} key={stage.stageCard.cardID} cardImage={stage.stageCard.imgSrc} numCards={stage.activeCards.length}></BigCard>
                ) : (
                    <BigCard cardId={stage.stageCard.cardID} key={stage.stageCard.cardID} cardImage={CardImages.Back_Adventure} numCards={stage.activeCards.length}></BigCard>
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
            {(currentStageObject && foeStageStart == true) && <FoeStageDisplay activePlayers={activePlayers} currentStage={currentStageObject}></FoeStageDisplay>}
        </div>
    </div>
  );
};

export default QuestDisplay;
