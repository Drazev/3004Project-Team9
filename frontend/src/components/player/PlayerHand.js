import Card from "../cards/Card";
// import CardImages from "../../assets/images/index";
import { useHandOversize, useTurn } from "../../stores/generalStore";
import { useGetPlayer } from "../../stores/playerStore";

import "./PlayerHand.css";

function PlayerHand(props){
    const handOversize = useHandOversize();
    const currentTurn = useTurn();
    const player = useGetPlayer(props.playerID);
    // console.log("Player state at Hand \n"+JSON.stringify(state,object =>JSON.stringify(object)));
    // const player = usePlayerStore(state => state.pData.find(p => p.playerId===props.playerID));

    if(player===undefined) {
        console.log("player is undefined");
    } else {
        console.log("Player Data "+player.playerId,player.shields,player.rank);
    }

    const Rendercards = props.cardsInHand?.map((card) => (
        <Card playerID={props.playerID} card={card} key={card.cardID} cardImage={card.imgSrc} selectedAllowed={(props.isMyHand) || (props.isMyHand && handOversize)} canGrow={props.isMyHand} cardOwner={player.name} isActive={false}></Card>
    ));

    const RenderActiveCards = props.activeCards?.map((card) => (
        <Card card={card} key={card.cardID} cardImage={card.imgSrc} selectedAllowed={false} canGrow={false} cardOwner={player.name} isActive={true}></Card>
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