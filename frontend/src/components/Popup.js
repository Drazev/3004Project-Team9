import React from "react";
import {Button} from "react-bootstrap";
import "./Popup.css"
 
const Popup = props => {

  return (
    <div className="popup-box">
      <div className="box">
          {props.popupType === "JOINQUEST" && 
            <div>
                <h4>Will you join this quest?</h4>
                <Button onClick={props.handleYes} style={{backgroundColor: "red", marginRight: "10px"}}>Aye</Button>
                <Button onClick={props.handleNo} style={{backgroundColor: "green", marginLeft: "10px"}}>Nay</Button>
            </div>
          }
          {props.popupType === "SPONSORQUEST" && 
            <div>
                <h4>Will you sponsor this quest?</h4>
                <Button onClick={props.handleYes} style={{backgroundColor: "red", marginRight: "10px"}}>Aye</Button>
                <Button onClick={props.handleNo} style={{backgroundColor: "green", marginLeft: "10px"}}>Nay</Button>
            </div>
          }
          {props.popupType === "HANDOVERFLOW" && 
            <div>
                <h4>You have too many cards in your hand!</h4>
                <Button onClick={props.handleYes} style={{backgroundColor: "green", marginRight: "10px"}}>Okay!</Button>
            </div>
          }
      </div>
    </div>
  );
};
 
export default Popup;