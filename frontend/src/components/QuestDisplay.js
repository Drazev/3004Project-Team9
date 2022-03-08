import React from "react";
import StageDisplay from "./StageDisplay";
import Card from "./Card";
import CardImages from "../Images/index";
import BigCard from "./BigCard";


/*
  currentStage: 3
  stages: {0: {stageType: "foe", stageCard: {}, activeCards: [], totalBP: 5},1:{stageType: "test", stageCard: {}, highestBid: 10},2:{stageType: "foe"}}
  playerActiveCards: {playerName: {cardsActive[{},{}]}, playerName2:{}}
  activePlayers: ["player1","player2"]
  turns will be managed by overall gamestate store
  questSponsor: "PlayerName"


        long cardID,
        AllCardCodes cardCode,
        String cardName,
        CardTypes subType,
        String imgSrc,
        int bids,
        int battlePoints,
        String effectDescription,
        boolean hasActiveEffect


public enum CardTypes {
    FOE,
    TEST,
    AMOUR,
    WEAPON,
    ALLY,
    EVENT,
    QUEST,
    TOURNAMENT
}
    private final int bpValue;
    private final int boostedBpValue;
    private boolean isBoosted;
    private Effects activeEffect; //TODO: Modify for Effect implementation


  Overall GameState Store
    updateTurn
    endTurn
  
  Story Store
    UpdateStages (get stages from backend)
    UpdateActivePlayerCards (remove old active cards)
    GetRewards (distributes reward to players)

    PlayerPlaysCard (Card, PlayerName)
    PlayerJoinsQuest (PlayerName)
    StartQuest: {questCard}
    SponsorStageCard (sponsor plays card to sponsor stage) {stageNo, card, PlayerName}
*/

const stages = {
    0: {stageCard: {cardID: 5, cardName: "Boar", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Boar.png",bpValue: 5, boostedBpValue: 15, isBoosted: false },
        activeCards: [
            {cardID:9,cardName:"Horse",imgSrc:"./Assets/Adventure Deck (346x470)/Weapon - Horse.png",battlePoints:10,hasActiveEffect:false},
            {cardID:9,cardName:"Battle-ax",imgSrc:"./Assets/Adventure Deck (346x470)/Weapon - Battle-ax.png",battlePoints:15,hasActiveEffect:false},
            {cardID:9,cardName:"Sword",imgSrc:"./Assets/Adventure Deck (346x470)/Weapon - Sword.png",battlePoints:10,hasActiveEffect:false}
        ]},
    1: {stageCard: {cardID: 6, cardName: "Saxons", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Saxons.png",bpValue: 10, boostedBpValue: 20, isBoosted: false },
        activeCards: []},
    2: {stageCard: {cardID: 7, cardName: "Black Knight", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Black Knight.png",bpValue: 25, boostedBpValue: 35, isBoosted: false },
        activeCards: []},
    3: {stageCard: {cardID: 8, cardName: "Dragon", imgsrc: "./Assets/Adventure Deck (346x470)/Foe - Dragon.png",bpValue: 50, boostedBpValue: 70, isBoosted: false },
        activeCards: []}
};
const activePlayers = ["player1","player2"];
const questSponsor = "player3";
const playerActiveCards = {
    player1: [], 
    player2: [], 
    player3: [], 
    player4: []
};
let currentStage = 3;
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
    </div>
  );
};

export default QuestDisplay;
