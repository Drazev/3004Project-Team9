import PlayerHand from "./PlayerHand";
import CardImages from "../Images/index";
import Card from "./Card";
import {drawCard} from "../ClientSocket";
import {useName} from "../Store";
import {Button} from "react-bootstrap";

function GameBoard(props){
    let init = 130;
    let jump = 175;
    const name = useName();
    //const turn = useTurn();
    const turn = "PlayerName";

    props = {player1:{name:"Test1",isTurn:true,cardsInHand:[{cardId:1,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:54},
             player2:{name:"Test2",isTurn:true,cardsInHand:[{cardId:2,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:5},
             player3:{name:"Test3",isTurn:true,cardsInHand:[{cardId:3,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:5},
             player4:{name:"Test4",isTurn:true,cardsInHand:[{cardId:4,cardImage:CardImages.Ally_KingArthur}],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:5}
            };
    
    if(props.player1.name == name){
        props.player1.isMyHand=true;
    }else if(props.player2.name == name){
        props.player2.isMyHand=true;
    }else if(props.player3.name == name){
        props.player3.isMyHand=true;
    }else if(props.player4.name == name){
        props.player4.isMyHand=true;
    }

    /*
    props.player1Turn = false;
    props.player2Turn = false;
    props.player3Turn = false;
    props.player4Turn = false;
    if(turn == props.player1.name){
        props.player1Turn = true;
    }else if(turn == props.player2.name){
        props.player2Turn = true;
    }else if(turn == props.player3.name){
        player3Turn = true;
    }else if(turn == props.player4.name){
        player4Turn = true;
    }*/

    return (
        <div id="GameBoard">
            <div id="allHands">
                <PlayerHand 
                    playerName={props.player1.name} 
                    isTurn={props.player1.isTurn} 
                    isMyHand={props.player1.isMyHand} 
                    cardsInHand={props.player1.cardsInHand} 
                    cardsInPlay={props.player1.cardsInPlay}
                    rank={props.player1.rank}
                    numShields={props.player1.shields}
                    top={init}
                    left={0}
                    shield={CardImages.Shield_3}
                    style={{

                    }}>
                </PlayerHand>
                <PlayerHand 
                    playerName={props.player2.name} 
                    isTurn={props.player2.isTurn} 
                    isMyHand={props.player2.isMyHand} 
                    cardsInHand={props.player2.cardsInHand} 
                    cardsInPlay={props.player2.cardsInPlay}
                    rank={props.player2.rank}
                    numShields={props.player2.shields}
                    top={init+jump}
                    left={0}
                    shield={CardImages.Shield_1}
                    style={{

                    }}>
                </PlayerHand>
                {props.hasOwnProperty('player3') &&
                    <PlayerHand 
                        playerName={props.player3.name} 
                        isTurn={props.player3.isTurn} 
                        isMyHand={props.player3.isMyHand} 
                        cardsInHand={props.player3.cardsInHand} 
                        cardsInPlay={props.player3.cardsInPlay}
                        rank={props.player3.rank}
                        numShields={props.player3.shields}
                        top={init+jump*2}
                        left={0}
                        shield={CardImages.Shield_8}
                        style={{

                        }}>
                    </PlayerHand>
                }
                {props.hasOwnProperty('player4') &&
                    <PlayerHand 
                        playerName={props.player4.name} 
                        isTurn={props.player4.isTurn} 
                        isMyHand={props.player4.isMyHand} 
                        cardsInHand={props.player4.cardsInHand} 
                        cardsInPlay={props.player4.cardsInPlay}
                        rank={props.player4.rank}
                        numShields={props.player4.shields}
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
            <div id="stageDisplay">

            </div>
            <Button onClick={() => drawCard(name,0)} >Draw</Button>
            <Button>End Turn</Button>
        </div>
    );
}
export default GameBoard;