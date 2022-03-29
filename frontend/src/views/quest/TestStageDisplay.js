import {React, useState} from "react";
import CardImages from "../../assets/images/index";
import BigCard from "../../components/cards/BigCard";
import { useMaxBid, useMaxBidPlayer, useStoryCard, useCurrentBidder, useTurn } from "../../stores/generalStore";
import { useBidRequest, useSetBidRequest } from "../../stores/questRequestStore";
import Popup from "../../components/popups/Popup"
import "./TestStageDisplay.css";


function TestStageDisplay(props){
    const maxBid = useMaxBid();
    const maxBidPlayer = useMaxBidPlayer();
    const testCard = useStoryCard();
    const currentBidder = useCurrentBidder();
    const [bidRequest,setBidRequest] = [useBidRequest(),useSetBidRequest()];

    return(
        <div>
            
            {(maxBidPlayer === undefined) ? (
                <p>"Minumum bid amount: ${testCard.bids}"</p>
            ) : (
                <p>"${maxBidPlayer.name} has the highest bid of ${maxBid}"</p>
            )
            }
            <img 
                id="CardImage"
                src={testCard.imgSrc} 
                style={{
                    width:"100px",
                    height:"135px",
                    borderRadius:10,
                }}
                alt="nonono"
            />

            {bidRequest &&
                <Popup popupType="BIDREQUEST" setPopup={setBidRequest}></Popup>
            }
        </div>
    )
}
export default TestStageDisplay;