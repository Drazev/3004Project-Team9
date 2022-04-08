import React, { useState } from "react";
import { useStageAreas } from "../../stores/playAreaStore";
import { useIsSponsoring, useId, useName, useSponsorName, useActivePlayers } from "../../stores/generalStore";
import {
    useCardTargetSelectionRequest,
    useSetCardTargetSelectionRequest,
    useCardTargetSelectionRequestBody,
    useSetCardTargetSelectionRequestBody
} from "../../stores/effects/effectRequestStore";
import { Button, DropdownButton, Dropdown } from "react-bootstrap";
import { activateCard, discardCard, playCard, cardTargetSelectionRespond } from "../../services/clientSocket";
import "./Card.css";

function Card(props) {
    const [isBig, setIsBig] = useState(false);
    const [isActiveSelected, setIsActiveSelected] = useState(false);
    const [isSelected, setSelected] = useState(false);
    const numStages = useStageAreas().length;
    const sponsorName = useSponsorName();
    const [cardTargetSelectionRequest, setCardTargetSelectionRequest] = [useCardTargetSelectionRequest(), useSetCardTargetSelectionRequest()];
    const [requestBody, setRequestBody] = [useCardTargetSelectionRequestBody(), useSetCardTargetSelectionRequestBody()];
    const playerId = useId();
    const playerName = useName();
    const activePlayers = useActivePlayers();
    const cardBackSrc = "./Assets/Adventure Deck (346x470)/Adventure Deck Card Back.png";

    //add border around card when selected
    let borderSize;
    if (isSelected && props.selectedAllowed) {
        borderSize = "2px solid #e9eb6e";
    } else {
        borderSize = "";
    }

    //increase size of card when hovering over it and it is allowed to grow
    let size;
    if (isBig && props.canGrow) {
        size = { width: "70px", height: "auto" };
    } else {
        size = { width: "50px", height: "68px" };
    }

    const sendCardTargetSelectionRequest = () => {
        cardTargetSelectionRespond(
            requestBody.requestID,
            requestBody.requestPlayerID,
            props.playerID,
            props.card.cardID
        )
        setCardTargetSelectionRequest(false);
        setRequestBody({});
    }

    const renderDropdownButton = () => {
        if (isSelected && numStages !== 0 && (playerName == sponsorName)) {
            return <DropdownButton
                id="dropdown-basic-button"
                title="Play"
                size="10"
                drop="up"
                autoClose="inside"
                variant="secondary">
                {/* <Dropdown.Item onClick={() => playCard(props.cardOwner, props.playerID, props.card.cardID, -1, -1)}>Your play area</Dropdown.Item> */}
                {renderDropdownItems()}
            </DropdownButton>
        } else {
            return
        }
    }

    const renderDropdownItems = () => {
        const dropdownItems = [];
        for (let i = 0; i < numStages; i++) {
            dropdownItems.push(<Dropdown.Item key={i} onClick={() => { playCard(props.cardOwner, props.playerID, props.card.cardID, -1, i) }}>Stage {i + 1}</Dropdown.Item>);
        }
        return dropdownItems
    }

    const renderPlayButton = () => {
        if (isSelected && (numStages === 0 || (playerName != sponsorName))) {
            return <Button
                id="PlayButton"
                style={playButtonStyle}
                onClick={() => playCard(props.cardOwner, props.playerID, props.card.cardID, -1, -1)}
            >Play
            </Button>
        }
    }

    const renderDiscardButton = () => {
        if (isSelected) {
            return <>
                <Button
                    id="DiscardButton"
                    style={discardButtonStyle}
                    onClick={() => discardCard(props.cardOwner, props.card.cardID)}
                >Discard</Button>{' '}
            </>
        }
    }

    const renderRemoveButton = () => {
        if (isActiveSelected && cardTargetSelectionRequest && playerId !== props.playerID && props.card.imgSrc !== cardBackSrc && props.card.subType == "ALLY") {
            return <Button
                onClick={sendCardTargetSelectionRequest}
                id="Remove"
                style={removeButtonStyle}> Remove
            </Button>
        }
    }

    const renderActivateButton = () => {
        if ( (isSelected || isActiveSelected) && props.card && props.card.hasActiveEffect && props.cardOwner == playerName) {
            return <Button
                onClick={() => activateCard(playerName, playerId, props.card.cardID)}
                id="Activate"
                className="activate-button"> Activate
            </Button>
        }
    }

    //Actual Card component
    return (
        <div
            id="CardSection"
            position="absolute"
            style={{ height: "auto", width: 73, margin: "0 auto", float: "left", marginBottom: 10, marginRight: -13 }}
            onMouseLeave={() => {
                // if (!isSelected)
                setIsBig(false);
                if (isSelected)
                    setSelected(false);
            }}
        >
            <img
                id="CardImage"
                src={props.cardImage}
                style={{
                    width: size.width,
                    height: size.height,
                    border: borderSize,
                    borderRadius: 10,
                }}
                onMouseOver={() => setIsBig(true)}
                onClick={() => {
                    if (props.selectedAllowed) { 
                        setSelected(!isSelected); 
                    }
                    else if (props.isActive && (playerName == props.cardOwner || cardTargetSelectionRequest)) {
                        setIsActiveSelected(!isActiveSelected);
                    } 
                }}
                alt="ohno"
            />
            <div style={{ marginTop: -12, }}>
                {renderDiscardButton()}
                {renderPlayButton()}
                {renderActivateButton()}
                {renderRemoveButton()}
                {renderDropdownButton()}
            </div>
        </div>
    );
}

const removeButtonStyle = {
    width: 30,
    height: 10,
    fontSize: 5,
    marginLeft: 1,
    paddingTop: 0,
    paddingLeft: 5,
    paddingRight: 5,
    backgroundColor: "#c96b6b",
    borderColor: "#c96b6b",
}

const playButtonStyle = {
    width: 30,
    height: 10,
    fontSize: 5,
    marginLeft: 1,
    paddingTop: 0,
    paddingLeft: 5,
    paddingRight: 5,
    backgroundColor: "#77a3c9",
    borderColor: "#77a3c9",
}

const discardButtonStyle = {
    width: 30,
    height: 10,
    fontSize: 5,
    marginRight: 1,
    paddingTop: 0,
    paddingLeft: 5,
    paddingRight: 5,
    backgroundColor: "#c96b6b",
    borderColor: "#c96b6b"
}


export default Card;
