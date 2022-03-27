import {React, useState} from "react";
import CardImages from "../../assets/images/index";
import BigCard from "../../components/cards/BigCard";
import { useMaxBid, useMaxBidPlayer, useStoryCard, useCurrentBidder, useTurn } from "../../stores/generalStore";
import Popup from "../../components/popups/Popup"
import "./TestStageDisplay.css";


function TestStageDisplay(props){
    const [popup, setPopup] = useState(true);
    const curTurn = useTurn();
    const maxBid = useMaxBid();
    const maxBidPlayer = useMaxBidPlayer();
    const testCard = useStoryCard();
    const currentBidder = useCurrentBidder();

    return(
        <div>
            
            {(maxBidPlayer === {}) ? (
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

            {popup && (currentBidder.name === curTurn) &&
                <Popup popupType="BIDREQUEST" setPopup={setPopup}></Popup>
            }
        </div>
    )
}
export default TestStageDisplay;