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
    if(isSelected && props.selectedAllowed){
        borderSize = "2px solid #e9eb6e";
    }else{
        borderSize = "";
    }

    //increase size of card when hovering over it and it is allowed to grow
    let size;
    if(isBig && props.canGrow){
        size = {width: "70px", height: "94px"};
    }else{
        size = {width: "50px", height: "68px"};
    }

    //Actual Card component
    return (
        <div 
            id="CardSection"
            style={{height:68,width:73,margin:"0 auto",float:"left",marginBottom:10,marginRight:-13}} 
            onMouseLeave={() => {setIsBig(false); if(isSelected) setSelected(false);}}
        >
          <img 
            id="CardImage"
            src={props.cardImage} 
            style={{
                width:size.width,
                height:size.height,
                border:borderSize,
                borderRadius:10,
            }}
            onMouseOver={() => setIsBig(true)} 
            onClick={() => setSelected(!isSelected)} 
            alt="ohno"
          />
         <div style={{marginTop:-12,}}>
            {isSelected && props.selectedAllowed &&
                <>
                    <Button
                        id="DiscardButton"
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
            {isSelected && props.selectedAllowed &&
                <>
                    <Button 
                        id="PlayButton"
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
