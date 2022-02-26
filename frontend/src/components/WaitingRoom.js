import React, { useState } from "react";
import { sendMessage } from "../ClientSocket";
//import { sendMessage } from "../ClientSocket";
import { useName } from "../Store";
import Header from "./Header"

const WaitingRoom = () => {
    const name = useName();
    const players = ["bob"] // this should be an api call to api/players to get a list of players
    const test=(e) =>{
        sendMessage(name, "button clicked")
    };
    return (
        <div className="WaitingRoom">
            <t2>Welcome to the pregame lobby, {name}</t2>
            {players.length >= 2 ? (
                <>
                <p>There Are Enough Players to Begin</p>
                <button
                onClick={test}
                className="btn btn-primary"
                >Ready
                </button>
                </>
                
                
                ) : (
                    <>
                    <p> Not Enough Players! only {players.length} / 4</p>
                    <button
                    disabled="true"
                    onClick={test}
                    className="btn btn-primary"
                    >Ready
                    </button>
                    </>
                )}
            
        </div>
    )

};
export default WaitingRoom;