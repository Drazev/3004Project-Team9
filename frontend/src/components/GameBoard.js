import PlayerHand from "./PlayerHand";
import CardImages from "../Images/index";
import Card from "./Card";
import {Button} from "react-bootstrap";

function GameBoard(props){
    props = {player1:{name:"Shelly",isTurn:true,isMyHand:true,cardsInHand:[CardImages.Ally_KingArthur,CardImages.Weapon_Excalibur,CardImages.Weapon_Lance,CardImages.Ally_SirPercival,CardImages.Ally_QueenGuinevere,CardImages.Amour,CardImages.Event_Plague,CardImages.Ally_SirTristan,CardImages.Weapon_Sword,CardImages.Weapon_BattleAx,CardImages.Weapon_Dagger,CardImages.Quest_BoarHunt,CardImages.Weapon_Lance],cardsInPlay:[],rank:CardImages.Rank_Squire,shields:54},
             player2:{name:"Sarah",isTurn:true,isMyHand:true,cardsInHand:[CardImages.Ally_KingArthur,CardImages.Weapon_Excalibur,CardImages.Weapon_Lance,CardImages.Ally_SirPercival,CardImages.Ally_QueenGuinevere,CardImages.Amour,CardImages.Event_Plague,CardImages.Ally_SirTristan,CardImages.Weapon_Sword,CardImages.Weapon_BattleAx,CardImages.Weapon_Dagger,CardImages.Quest_BoarHunt,CardImages.Weapon_Lance],cardsInPlay:[CardImages.Ally_KingPellinore,CardImages.Ally_SirGalahad],rank:CardImages.Rank_Squire,shields:5},
             player3:{name:"Tony",isTurn:true,isMyHand:true,cardsInHand:[CardImages.Ally_KingArthur,CardImages.Weapon_Excalibur,CardImages.Weapon_Lance,CardImages.Ally_SirPercival,CardImages.Ally_QueenGuinevere,CardImages.Amour,CardImages.Event_Plague,CardImages.Ally_SirTristan,CardImages.Weapon_Sword,CardImages.Weapon_BattleAx,CardImages.Weapon_Dagger,CardImages.Quest_BoarHunt,CardImages.Weapon_Lance],cardsInPlay:[CardImages.Ally_KingPellinore],rank:CardImages.Rank_Squire,shields:5},
             player4:{name:"Jack",isTurn:true,isMyHand:true,cardsInHand:[CardImages.Ally_KingArthur,CardImages.Weapon_Excalibur,CardImages.Weapon_Lance,CardImages.Ally_SirPercival,CardImages.Ally_QueenGuinevere,CardImages.Amour,CardImages.Event_Plague,CardImages.Ally_SirTristan,CardImages.Weapon_Sword,CardImages.Weapon_BattleAx,CardImages.Weapon_Dagger,CardImages.Quest_BoarHunt,CardImages.Weapon_Lance],cardsInPlay:[CardImages.Event_ChivalrousDeed],rank:CardImages.Rank_Squire,shields:5}
            };
    let init = 130;
    let jump = 175;

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
            <Button>Draw</Button>
            <Button>End Turn</Button>
        </div>
    );
}
export default GameBoard;

/*
<div>
                    <Card cardImage={CardImages.Amour} selectedAllowed={false} canGrow={false}></Card>
                    <Card cardImage={CardImages.Amour} selectedAllowed={false} canGrow={false}></Card>
                </div>
                <div>
                    <Card cardImage={CardImages.Amour} selectedAllowed={false} canGrow={false}></Card>
                    <Card cardImage={CardImages.Amour} selectedAllowed={false} canGrow={false}></Card>
                </div>
*/