import React,{useState} from "react";
import { useStageAreas } from "../../stores/playAreaStore";
import { useIsSponsoring } from "../../stores/generalStore";
import { Button, DropdownButton, Dropdown } from "react-bootstrap";
import {discardCard, playCard} from "../../services/clientSocket";
import "./Card.css";

function Card(props){
    const [isBig, setIsBig]=useState(false);
    const [isActiveSelected, setIsActiveSelected]=useState(false);
    const [isSelected, setSelected] = useState(false);
    const numStages = useStageAreas().length;
    const isSponsoring = useIsSponsoring();

    //add border around card when selected
    let borderSize;
    if (isSelected && props.selectedAllowed) {
        borderSize = "2px solid #e9eb6e";
    } else if (props.isActive && isActiveSelected) {
        borderSize = "2px solid red";
    } else {
        borderSize = "";
    }

    //increase size of card when hovering over it and it is allowed to grow
    let size;
    if(isBig && props.canGrow){
        size = {width: "90px", height: "auto"};
    }else{
        size = {width: "50px", height: "68px"};
    }

    const renderDropdownItems = () => {
        const dropdownItems = [];
        for (let i=0; i<numStages; i++){
            dropdownItems.push(<Dropdown.Item key={i} onClick={() => {playCard(props.cardOwner, props.playerID, props.card.cardID, -1, i)} }>Stage {i+1}</Dropdown.Item>);
        }
        return dropdownItems
    }

    //Actual Card component
    return (
        <div 
            id="CardSection"
            position="absolute"
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
            onClick={() => {if(props.selectedAllowed) {setSelected(!isSelected);} if(props.isActive) { setIsActiveSelected(!isActiveSelected) }}} 
            alt="ohno"
          />
         <div style={{marginTop:-12,}}>
            {isSelected &&
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
                        onClick={() => discardCard(props.cardOwner,props.card.cardID)}
                    >Discard</Button>{' '}
                </>
            }
            {isSelected && (numStages === 0 || !isSponsoring) &&
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
                    onClick={() => playCard(props.cardOwner, props.playerID, props.card.cardID, -1, -1)}
                >Play</Button>
            } 
            {isSelected && numStages !== 0 && isSponsoring &&
                <DropdownButton 
                id="dropdown-basic-button" 
                title="Play"
                size="10"
                drop="up"
                autoClose="inside"
                variant="secondary">
                    <Dropdown.Item onClick={() => playCard(props.cardOwner, props.playerID, props.card.cardID, -1, -1)}>Your play area</Dropdown.Item>
                    {renderDropdownItems()}
                </DropdownButton>
            }
            {isActiveSelected &&
                <>
                    <Button 
                        id="Remove"
                        style={{
                            width: 30,
                            height: 10,
                            fontSize: 5,
                            marginLeft: 1,
                            paddingTop: 0,
                            paddingLeft: 5,
                            paddingRight: 5,
                            backgroundColor:"#c96b6b",
                            borderColor:"#c96b6b",}}
                    >Remove</Button>{' '}
                </>
            }
         </div>
        </div>
      );
}

export default Card;
