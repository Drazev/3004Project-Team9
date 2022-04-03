import React from "react";
import FoeStageDisplay from "./FoeStageDisplay";
import TestStageDisplay from "./TestStageDisplay";
import BigCard from "../../components/cards/BigCard";
import Popup from "../../components/popups/Popup";
import "./QuestDisplay.css";
import { useIsSponsoring, useFoeStageStart, useTestStageStart, useStoryCard } from "../../stores/generalStore";
import { useStageAreas, useCurrentStage } from "../../stores/playAreaStore";
import { useStageTargetSelectionRequest } from "../../stores/effects/effectRequestStore";

const QuestDisplay = (props) => {
    const storyCard = useStoryCard();
    const isSponsoring = useIsSponsoring();
    const foeStageStart = useFoeStageStart();
    const stages = useStageAreas();
    const testStageStart = useTestStageStart();
    const numStages = stages.length - 1;
    const currentStageNum = useCurrentStage();
    const currentStageObject = stages.find(obj => obj.stageNum === currentStageNum);
    const stageTargetSelectionRequest = useStageTargetSelectionRequest();

    console.log("Current Stage: " + JSON.stringify(currentStageObject));
    console.log("Current Story Card: " + JSON.stringify(storyCard));
    if (currentStageObject) {
        console.log("Current Stage Object isBoosted: " + currentStageObject.isBoosted);
    }
    const getStages = () => {
        let x = [];
        for (let i = 0; i <= numStages; i++) {
            let curStage = stages.find(obj => obj.stageNum === i);
            x.push(curStage);
        }
        return x;
    };
    const RenderStages = () => {
        const stage = getStages();
        var rendered = [];
        let jump = 110;
        let curLeft = jump * -1;
        for (var x = 0; x <= numStages; x++) {
            if (stage[x] != null && stage[x].stageCard != null) {
                curLeft += jump;
                rendered.push(
                    <div style={{ left: curLeft, position: 'absolute' }}>
                        <BigCard 
                            cardId={stage[x].stageCard.cardID} 
                            key={stage[x].stageCard.cardID} 
                            cardImage={stage[x].stageCard.imgSrc} 
                            numCards={stage[x].activeCards.length} 
                            flippable={stageTargetSelectionRequest} 
                            stageNum={x}
                        />
                    </div>
                )
            }
        }
        return <div>{rendered}</div>
    }

    return (
        <div style={{ backgroundColor: "rgba(30, 30, 30, .4)", width: 600, height: 700, borderRadius: 30 }}>
            <div style={{ position: 'absolute', top: 50, left: 30, width: 360 }}>
                {currentStageObject && <h1>Quest Stage</h1>}
            </div>
            <div style={{ position: 'absolute', top: 20, left: 440 }}>
                {(storyCard != null) && <BigCard cardId={storyCard.cardID} cardImage={storyCard.imgSrc}></BigCard>}
            </div>
            <div style={{ position: 'absolute', top: 170 }}>
                {RenderStages()}
            </div>
            <div style={{ position: 'absolute', top: 120, left: 0 }}>
                {(currentStageObject && foeStageStart == true && testStageStart == false) && <FoeStageDisplay currentStage={currentStageObject}></FoeStageDisplay>}
                {(currentStageObject && testStageStart == true && foeStageStart == false) && <TestStageDisplay currentStage={currentStageObject}></TestStageDisplay>}
            </div>
        </div>
    );
};

export default QuestDisplay;
