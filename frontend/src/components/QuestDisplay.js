import React from "react";
import FoeStageDisplay from "./FoeStageDisplay";
import Card from "./Card";
import CardImages from "../Images/index";
import BigCard from "./BigCard";
import "./GameBoard.css";


const stages = {
    0: {stageCard: {cardID: 5, cardName: "Boar", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Boar.png",bpValue: 5, boostedBpValue: 15, isBoosted: true },
        activeCards: [
            {cardID:9,cardName:"Horse",imgsrc:"./Assets/Adventure Deck (346x470)/Weapon - Horse.png",battlePoints:10,hasActiveEffect:false},
            {cardID:9,cardName:"Battle-ax",imgsrc:"./Assets/Adventure Deck (346x470)/Weapon - Battle-ax.png",battlePoints:15,hasActiveEffect:false},
            {cardID:9,cardName:"Sword",imgsrc:"./Assets/Adventure Deck (346x470)/Weapon - Sword.png",battlePoints:10,hasActiveEffect:false}
        ]},
    1: {stageCard: {cardID: 6, cardName: "Saxons", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Saxons.png",bpValue: 10, boostedBpValue: 20, isBoosted: true, },
        activeCards: [{cardID:9,cardName:"Battle-ax",imgsrc:"./Assets/Adventure Deck (346x470)/Weapon - Battle-ax.png",battlePoints:15,hasActiveEffect:false}]},
    2: {stageCard: {cardID: 7, cardName: "Black Knight", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Black Knight.png",bpValue: 25, boostedBpValue: 35, isBoosted: false },
        activeCards: []},
    3: {stageCard: {cardID: 8, cardName: "Dragon", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Dragon.png",bpValue: 50, boostedBpValue: 70, isBoosted: false },
        activeCards: []}
};
const activePlayers = ["John","Yusuf"];
const questSponsor = "player3";
const playerActiveCards = {
    player1: [], 
    player2: [], 
    player3: [], 
    player4: []
};
let currentStage = 2;
let currentTurn = "player1";

const QuestDisplay = (props) => {

    const RenderStages = Object.keys(stages).map((stageNo) => (
        (stageNo < currentStage) ? (
            <BigCard cardId={stages[stageNo].stageCard.cardID} key={stages[stageNo].stageCard.cardID} cardImage={stages[stageNo].stageCard.imgsrc} numCards={stages[stageNo].activeCards.length}></BigCard>
        ) : (
            <BigCard cardId={stages[stageNo].stageCard.cardID} key={stages[stageNo].stageCard.cardID} cardImage={CardImages.Back_Adventure} numCards={stages[stageNo].activeCards.length}></BigCard>
        )
    ));

  return (
    <div>
        <div style={{width:1000,top:60,left:1050,position:'fixed'}}>
            <h3>Quest Stages</h3>
        </div>
        <div style={{width:1000,top:100,left:1300,position:'fixed'}}>
            {RenderStages}
        </div>
        <div>
            <FoeStageDisplay activePlayers={activePlayers} currentStage={stages[currentStage-1]}></FoeStageDisplay>
        </div>
    </div>
  );
};

export default QuestDisplay;
