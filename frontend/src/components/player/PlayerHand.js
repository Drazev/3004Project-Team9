import Card from "../cards/Card";
import { useState } from "react";
import { useHandOversize, useTurn, useId, useIsSponsoring, useSponsorName } from "../../stores/generalStore";
import { useGetPlayer } from "../../stores/playerStore";
import { useGetPlayerPlayArea } from "../../stores/playAreaStore";

import "./PlayerHand.css";
import { useEffect } from "react";

function PlayerHand(props){
    const id = useId();
    const handOversize = useHandOversize();
    const currentTurn = useTurn();
    const player = useGetPlayer(props.playerID);
    const playerPlayArea = useGetPlayerPlayArea(props.playerID);
    const sponsorName = useSponsorName();
    const [newTop, setNewTop] = useState(70);

    useEffect(() => {
        if(props.activeCards.length > 0){
            setNewTop(140);
        } else {
            setNewTop(70);
        }
    }, [props.activeCards]);

    const Rendercards = props.cardsInHand?.map((card) => (
        <Card playerID={props.playerID} card={card} key={card.cardID} cardImage={card.imgSrc} selectedAllowed={(props.isMyHand) || (props.isMyHand && handOversize)} canGrow={props.isMyHand} cardOwner={player.name} isActive={false}></Card>
    ));

    const RenderActiveCards = props.activeCards?.map((card) => (
        <Card playerID={props.playerID} card={card} key={card.cardID} cardImage={card.imgSrc} selectedAllowed={false} canGrow={false} cardOwner={player.name} isActive={true}></Card>
    ));

    const renderName = () => {
        let description = props.playerName;
        if (sponsorName === props.playerName) {
            description += " (sponsoring)";
        } else if (props.playerName === currentTurn){
            description += " (current turn)";
        }
        return <p style={{position:"absolute",top:32,left:68}}>{description}</p>    
    }

    const renderPlayerInfo = () => {
        if (playerPlayArea !== undefined && id === playerPlayArea.id) {
            return (
                <div>
                    <p style={{position:"absolute",top:0,left:150}}>BP x {playerPlayArea.battlePoints}</p>
                    <p style={{position:"absolute",top:0,left:250}}>Free Bids x {playerPlayArea.bids}</p>
                </div>
            )
        }
    }

    return(
        <div>
            <div>
                <Card cardImage={player.rankImgSrc} selectedAllowed={false} canGrow={false} ></Card>
                <img
                    src={props.shield}
                    style={{
                        width:33,
                        height:30,
                        top: 3,
                        left: 58,
                        position: "absolute"
                    }}
                />     
                <p style={{position:"absolute",top:0,left:93}}>{"x  " + player.shields}</p> 
                {renderPlayerInfo()}
                {renderName()}       
            </div>

            <div
            style={{
                position: "relative",
                top: newTop,
                width:900,
                left:-60,
            }}>
                {typeof props.cardsInHand !== 'undefined' && props.cardsInHand.length > 0 &&
                    Rendercards
                }
                <div style={{top: -70, left:60, position:"absolute"}}>
                    {typeof props.cardsInHand !== 'undefined' && props.cardsInHand.length > 0 &&
                        RenderActiveCards
                    }
                </div>
            </div>
        </div>
    );
}
export default PlayerHand;