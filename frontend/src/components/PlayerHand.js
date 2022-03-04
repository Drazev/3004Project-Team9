import Card from "./Card"
import CardImages from "../Images/index"

function PlayerHand(props){

    const Rendercards = props.cardsInHand?.map((card) => (
        props.isMyHand ? (
           <Card cardId={card.cardID} key={card.cardID} cardImage={card.cardImage} selectedAllowed={props.isTurn && props.isMyHand} canGrow={props.isMyHand} cardOwner={props.name}></Card>
        ) : (
            <Card cardId={card.cardID} key={card.cardID} cardImage={CardImages.Back_Adventure} selectedAllowed={props.isTurn && props.isMyHand} canGrow={props.isMyHand} cardOwner={props.name}></Card>
        )
    ));

    const RenderInPlay = props.cardsInPlay?.map((card) => (
        <>
            <Card cardImage={card} selectedAllowed={false} canGrow={props.isMyHand}></Card>
        </>
    ));

    return(
        <div>
            <div
            style={{
                position: "absolute",
                top: props.top-70,
                left: props.left
            }}>
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
                <p style={{position:"absolute",top:10,left:85,fontSize:13}}>{"x" + props.numShields}</p> 
                <p style={{position:"absolute",top:43,left:65,fontSize:13}}>{props.playerName}</p>           
            </div>
            <div
            style={{
                position: "absolute",
                top: props.top,
                left: props.left
            }}>
                {typeof props.cardsInHand !== 'undefined' && props.cardsInHand.length > 0 &&
                    Rendercards
                }
            </div>
        </div>
    );
}
export default PlayerHand;