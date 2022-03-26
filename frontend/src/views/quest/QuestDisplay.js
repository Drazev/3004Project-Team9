import React from "react";
import FoeStageDisplay from "./FoeStageDisplay";
import CardImages from "../../assets/images/index";
import BigCard from "../../components/cards/BigCard";
import "./QuestDisplay.css";
import { useIsSponsoring, useFoeStageStart, useStoryCard, useActivePlayers } from "../../stores/generalStore";
import { useStageAreas, useCurrentStage } from "../../stores/playAreaStore";

const QuestDisplay = (props) => {
    const storyCard = useStoryCard();
    const isSponsoring = useIsSponsoring();
    const foeStageStart = useFoeStageStart();
    const stages = useStageAreas();
    const numStages = stages.length-1;
    const currentStageNum = useCurrentStage();
    const currentStageObject = stages.find(obj => obj.stageNum === currentStageNum);

    console.log("Current Stage: " + JSON.stringify(currentStageObject));
    console.log("Current Story Card: " + JSON.stringify(storyCard));
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
    const RenderStages = () => {
        const stage = getStages();
        var rendered = [];
        let jump = 110;
        let curLeft = jump*-1;
        for(var x = 0; x <= numStages; x++){
            if (stage[x] != null && stage[x].stageCard != null){
                curLeft += jump;
                if((currentStageNum >= stage[x].stageNum && foeStageStart == true) || (isSponsoring == true)){
                    rendered.push(
                        <div style={{left:curLeft,position:'absolute'}}>
                            <BigCard cardId={stage[x].stageCard.cardID} key={stage[x].stageCard.cardID} cardImage={stage[x].stageCard.imgSrc} numCards={stage[x].activeCards.length}></BigCard>
                        </div>
                    )
                }else{
                    rendered.push(
                        <div style={{left:curLeft,position:'absolute'}}>
                            <BigCard cardId={stage[x].stageCard.cardID} key={stage[x].stageCard.cardID} cardImage={CardImages.Back_Adventure} numCards={stage[x].activeCards.length}></BigCard>
                        </div>
                    )
                }
            }
        }
        return <div>{rendered}</div>
    }

  return (
    <div style={{backgroundColor: "rgba(30, 30, 30, .4)", width:600, height:700, borderRadius:30}}>
        <div style={{position:'absolute',top:50,left:30,width:360}}>
            {currentStageObject && <h1 style={{textDecoration: "underline",fontFamily: "Apple Chancery", fontSize:60}}>Quest Stage</h1>}
        </div>
        <div style={{position:'absolute',top:20,left:440}}>
            {(storyCard != null) && <BigCard cardId={storyCard.cardID} cardImage={storyCard.imgSrc}></BigCard>}
        </div>
        <div style={{position:'absolute',top:170}}>
            {RenderStages()}
        </div>
        <div style={{position:'absolute',top:120,left:0}}>
            {(currentStageObject && foeStageStart == true) && <FoeStageDisplay currentStage={currentStageObject}></FoeStageDisplay>}
        </div>
    </div>
  );
};

export default QuestDisplay;
