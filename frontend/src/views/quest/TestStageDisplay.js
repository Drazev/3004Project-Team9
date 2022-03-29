import {React, useState} from "react";
import CardImages from "../../assets/images/index";
import BigCard from "../../components/cards/BigCard";
import { useMaxBid, useMaxBidPlayer, useStoryCard, useCurrentBidder, useTurn, useHandOversize } from "../../stores/generalStore";
import { useBidRequest, useSetBidRequest } from "../../stores/questRequestStore";
import Popup from "../../components/popups/Popup"
import "./TestStageDisplay.css";


function TestStageDisplay(props){
    const handOversize = useHandOversize()
    const maxBid = useMaxBid();
    const maxBidPlayer = useMaxBidPlayer();
    const currentBidder = useCurrentBidder();
    const [bidRequest,setBidRequest] = [useBidRequest(),useSetBidRequest()];

    console.log("is oversize: " + handOversize);
    return(
        <div style={{position:"absolute",left:40,top:250,width:300}}>
            {(maxBidPlayer === undefined) ? (
                <p float="left">Minumum bid amount: {maxBid}</p>
            ) : (
                <p float="left">{maxBidPlayer.name} has the highest bid of {maxBid}</p>
            )
            }
            <img 
                id="CardImage"
                src={props.currentStage.stageCard.imgSrc} 
                style={{
                    width:"100px",
                    height:"135px",
                    float:"left",
                    borderRadius:10,
                }}
                alt="nonono"
            />

            {bidRequest && !handOversize &&
                <Popup popupType="BIDREQUEST" setPopup={setBidRequest}></Popup>
            }
        </div>
    )
}
export default TestStageDisplay;