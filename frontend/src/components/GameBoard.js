import PlayerHand from "./PlayerHand";
import QuestDisplay from "./QuestDisplay";
import CardImages from "../Images/index";
import Card from "./Card";
import {drawCard} from "../ClientSocket";
import {useName, usePlayerHands, usePlayers} from "../Stores/GeneralStore";
import { useUpdatePlayArea } from "../Stores/PlayAreaStore";
import {Button} from "react-bootstrap";
const x = useUpdatePlayArea;

function GameBoard(props){
    let init = 80;
    let jump = 240;
    const name = useName();
    const allPlayers = usePlayers();
    let hands = usePlayerHands();
    //const turn = useTurn();
    //console.log("Hands: " + JSON.stringify(hands) + "\n");
    const turn = "PlayerName";


    // hands = [{name:"Test1",isTurn:true,hand:[{cardId:1,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:54},
    // {name:"Test2",isTurn:true,hand:[{cardId:2,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:5}];

    let myHandArr = [false,false,false,false];
    for(let i = 0; i < hands.length; i++){
        if(hands[i].name === name){
            myHandArr[i] = true;
        }
    }

    return (
        <div id="GameBoard">
            <div id="allHands">
                <PlayerHand 
                    playerName={hands[0].name} 
                    isTurn={true /*hands[0].isTurn*/} 
                    isMyHand={myHandArr[0]} 
                    cardsInHand={hands[0].hand} 
                    activeCards={hands[0].hand}
                    rank={ CardImages.Rank_Squire/*hands[0].rank*/}
                    numShields={5/*hands[0].shields*/}
                    top={init}
                    left={0}
                    shield={CardImages.Shield_3}
                    style={{

                    }}>
                </PlayerHand>
                <PlayerHand 
                    playerName={hands[1].name} 
                    isTurn={true /*hands[1].isTurn*/} 
                    isMyHand={myHandArr[1]} 
                    cardsInHand={hands[1].hand} 
                    activeCards={hands[1].hand}
                    rank={ CardImages.Rank_Squire/*hands[1].rank*/}
                    numShields={5/*hands[1].shields*/}
                    top={init+jump}
                    left={0}
                    shield={CardImages.Shield_1}
                    style={{

                    }}>
                </PlayerHand>
                {hands.length > 2 &&
                    <PlayerHand 
                        playerName={hands[2].name} 
                        isTurn={true /*hands[2].isTurn*/} 
                        isMyHand={myHandArr[2]} 
                        cardsInHand={hands[2].hand} 
                        activeCards={hands[2].hand}
                        rank={ CardImages.Rank_Squire/*hands[2].rank*/}
                        numShields={5/*hands[2].shields*/}
                        top={init+jump*2}
                        left={0}
                        shield={CardImages.Shield_8}
                        style={{

                        }}>
                    </PlayerHand>
                }
                {hands.length > 3 &&
                    <PlayerHand 
                        playerName={hands[3].name} 
                        isTurn={true /*hands[3].isTurn*/} 
                        isMyHand={myHandArr[3]} 
                        cardsInHand={hands[3].hand} 
                        activeCards={hands[3].hand}
                        rank={ CardImages.Rank_Squire/*hands[3].rank*/}
                        numShields={5/*hands[3].shields*/}
                        top={init+jump*3}
                        left={0}
                        shield={CardImages.Shield_6}
                        style={{

                        }}>
                    </PlayerHand>
                }
            </div>
            <div id="storyDisplay">

            </div>
            <div id="decks" style={{
                position: "absolute",
                left: 1080,
                top: 30
            }}>
            </div>
            <QuestDisplay></QuestDisplay>
            <Button onClick={() => drawCard(name,0)} >Draw</Button>
            <Button onClick={() => x({ playerId: -1, isHandOversize: false, hand: [
                {
                    "cardID" : 418,
                    "cardCode" : "THIEVES",
                    "cardName" : "Thieves",
                    "subType" : "FOE",
                    "imgSrc" : "./Assets/Adventure Deck (346x470)/Foe - Thieves.png",
                    "bids" : 0,
                    "battlePoints" : 5,
                    "effectDescription" : null,
                    "hasActiveEffect" : false
                  }, {
                    "cardID" : 419,
                    "cardCode" : "THIEVES",
                    "cardName" : "Thieves",
                    "subType" : "FOE",
                    "imgSrc" : "./Assets/Adventure Deck (346x470)/Foe - Thieves.png",
                    "bids" : 0,
                    "battlePoints" : 5,
                    "effectDescription" : null,
                    "hasActiveEffect" : false
                  }, {
                    "cardID" : 406,
                    "cardCode" : "EVIL_KNIGHT",
                    "cardName" : "Evil Knight",
                    "subType" : "FOE",
                    "imgSrc" : "./Assets/Adventure Deck (346x470)/Foe - Evil Knight.png",
                    "bids" : 0,
                    "battlePoints" : 20,
                    "effectDescription" : null,
                    "hasActiveEffect" : false
                  }
            ] })}>End Turn</Button>
        </div>
    );
}

export default GameBoard;