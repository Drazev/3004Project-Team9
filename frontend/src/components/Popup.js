import React from "react";
import {Button} from "react-bootstrap";
import { sponsorRespond, } from "../ClientSocket";
import { useName, useSetIsSponsoring, useSetJoinRequest } from "../Stores/GeneralStore";
import "./Popup.css"
 
const Popup = props => {
  let name = useName();
  const setIsSponsoring = useSetIsSponsoring();
  const handleYes = () => {
    sponsorRespond(name, true);
    props.setPopup(false);
    setIsSponsoring(true);
  }
  const handleNo = () => {
    sponsorRespond(name, false);
    props.setPopup(false);
  }
  return (
    <div className="popup-box">
      <div className="box">
          {props.popupType === "JOINQUEST" && 
            <div>
                <h4>Will you join this quest?</h4>
                <Button onClick={handleYes} style={{backgroundColor: "green", marginRight: "10px"}}>Aye</Button>
                <Button onClick={handleNo} style={{backgroundColor: "red", marginLeft: "10px"}}>Nay</Button>
            </div>
          }
          {props.popupType === "SPONSORQUEST" && 
            <div>
                <h4>Will you sponsor this quest?</h4>
                <Button onClick={handleYes} style={{backgroundColor: "green", marginRight: "10px"}}>Aye</Button>
                <Button onClick={handleNo} style={{backgroundColor: "red", marginLeft: "10px"}}>Nay</Button>
            </div>
          }
          {props.popupType === "HANDOVERFLOW" && 
            <div>
                <h4>You have too many cards in your hand!</h4>
                <Button onClick={handleYes} style={{backgroundColor: "green", marginRight: "10px"}}>Okay!</Button>
            </div>
          }
      </div>
    </div>
  );
};
 
export default Popup;