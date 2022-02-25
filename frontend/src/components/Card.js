import CardImages from "../Images/index";
import React,{useState} from "react";
import {Button} from "react-bootstrap";

function Card(props){
    const [isBig, setIsBig]=useState(false);
    const [isSelected, setSelected]=useState(false);

    //depriciated for now
    //const [activeDiscard, setDiscard]=useState(true);
    //const [activePlay, setPlay]=useState(true);

    //add border around card when selected
    let borderSize;
    if(isSelected){
        borderSize = "1px solid #e9eb6e";
    }else{
        borderSize = "";
    }

    //increase size of card when hovering over it and it is allowed to grow
    let size;
    if(isBig && props.canGrow){
        size = {width: "80px", height: "120px"};
    }else{
        size = {width: "70px", height: "105px"};
    }

    //Actual Card component
    return (
        <div 
            style={{height:120,width:100,margin:"0 auto"}} 
            onMouseLeave={() => {setIsBig(!isBig); if(isSelected) setSelected(!isSelected);}}
        >
          <img 
            src={CardImages.Foe_RobberKnight} 
            style={{
                width:size.width,
                height:size.height,
                border:borderSize,
                borderRadius:10,
            }}
            onMouseOver={() => setIsBig(!isBig)} 
            onClick={() => setSelected(!isSelected)} 
            alt="ohno"
            />
         <div style={{marginTop:-12,}}>
            {isSelected && props.isInHand && props.isTurn &&
                <>
                    <Button
                        style={{
                            width: 30,
                            height: 10,
                            fontSize: 5,
                            marginRight: 1,
                            paddingTop: 0,
                            paddingLeft: 5,
                            paddingRight: 5,
                            backgroundColor:"#c96b6b",
                            borderColor:"#c96b6b",}}
                        //onClick={() => setDiscard(!activeDiscard)}
                    >Discard</Button>{' '}
                </>
            }
            {isSelected && props.isInHand && props.isTurn &&
                <>
                    <Button 
                        style={{
                            width: 30,
                            height: 10,
                            fontSize: 5,
                            marginLeft: 1,
                            paddingTop: 0,
                            paddingLeft: 5,
                            paddingRight: 5,
                            backgroundColor:"#77a3c9",
                            borderColor:"#77a3c9",}}
                        //onClick={() => setPlay(!activePlay)}
                    >Play</Button>{' '}
                </>
            }
         </div>
        </div>
      );
}

export default Card;
