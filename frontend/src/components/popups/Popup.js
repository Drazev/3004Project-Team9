import React from "react";
import NumericInput from 'react-numeric-input';
import {Button, InputGroup} from "react-bootstrap";
import { sponsorRespond, joinRespond } from "../../services/clientSocket";
import { useName, useSetIsSponsoring, useSetJoinRequest, useMaxBid } from "../../stores/generalStore";
import "./Popup.css"
 
const Popup = props => {
  let name = useName();
  const maxBid = useMaxBid();
  const setIsSponsoring = useSetIsSponsoring();
  const setJoinRequest = useSetJoinRequest();
  const handleYes = () => {
    if (props.popupType === "JOINQUEST") {
      joinRespond(name, true)
      setJoinRequest(false);
    } else if (props.popupType === "SPONSORQUEST") {
      sponsorRespond(name, true);
      setIsSponsoring(true);
    }
    props.setPopup(false);
  }
  const handleNo = () => {
    if (props.popupType === "JOINQUEST") {
      joinRespond(name, false)
    } else if (props.popupType === "SPONSORQUEST") {
      sponsorRespond(name, false);
    }
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
          {props.popupType === "BIDREQUEST" &&
            <div>
              <h6>Place your bid!</h6>
              <p>Minimum Next Bid: {maxBid}</p>
              <NumericInput
                className="form-control"
                value="0" 
                min={ -1 } 
                max={ 25 } 
                step={ 1 } 
                precision={ 0 } 
                size={ 6 } 
                maxLength={ 2 } 
                mobile
                inputmode="numeric" 
                strict
                style={{
                  wrap: {
                    background: '#E2E2E2',
                    boxShadow: '0 0 1px 1px #fff inset, 1px 1px 5px -1px #000',
                    padding: '0.5px',
                    borderRadius: '6px 3px 3px 6px',
                    fontSize: 23,
                    width: 170,
                    left: "27%",
                  },
                  input: {
                    borderRadius: '4px 2px 2px 4px',
                    color: '#988869',
                    padding: '0.1ex',
                    border: '1px solid #ccc',
                    fontWeight: 100,
                  },
                  arrowUp: {
                    borderBottomColor: 'rgba(66, 54, 0, 0.63)'
                  },
                  arrowDown: {
                    borderTopColor: 'rgba(66, 54, 0, 0.63)'
                  }
                }}
              ></NumericInput>
              <button type="submit" className="SubmitBidButton">Place Bid</button>
            </div>
          }
      </div>
    </div>
  );
};
 
export default Popup;