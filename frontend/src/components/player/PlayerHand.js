import Card from "../cards/Card";
import CardImages from "../../assets/images/index";
import { useHandOversize, useTurn } from "../../stores/generalStore";

import "./PlayerHand.css";

function PlayerHand(props){
    const handOversize = useHandOversize();
    const currentTurn = useTurn();
    console.log(`handOversize=${handOversize}`);

    const Rendercards = props.cardsInHand?.map((card) => (
        <Card playerID={props.playerID} card={card} key={card.cardID} cardImage={card.imgSrc} selectedAllowed={(props.isMyHand) || (props.isMyHand && handOversize)} canGrow={props.isMyHand} cardOwner={props.playerName} isActive={false}></Card>
    ));

    const RenderActiveCards = props.activeCards?.map((card) => (
        <Card card={card} key={card.cardID} cardImage={card.imgSrc} selectedAllowed={false} canGrow={false} cardOwner={props.playerName} isActive={true}></Card>
    ));

    const RenderName = () => {
        if(props.playerName == currentTurn){
            return <p style={{position:"absolute",top:32,left:68}}>{props.playerName + " (current turn)"}</p>    
        }else{
            return <p style={{position:"absolute",top:32,left:68}}>{props.playerName}</p>    
        }
    }

    let newTop = 70;
    if(props.activeCards.length > 0){
        newTop += 70;
    }

    return(
        <div>
            <div>
                <Card cardImage={props.rank} selectedAllowed={false} canGrow={false} ></Card>
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
                <p style={{position:"absolute",top:0,left:93}}>{"x  " + props.numShields}</p> 
                {RenderName()}       
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