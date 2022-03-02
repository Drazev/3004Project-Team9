import React, { useEffect, useState } from "react";
import { sendMessage, startGame } from "../ClientSocket";
//import { sendMessage } from "../ClientSocket";
import { useName, useConnected } from "../Store";
import { usePlayers, useSetPlayers } from "../Store";
import useStore from "../Store";
import { gPlayers } from "../ClientSocket";

const WaitingRoom = () => {
    const name = useName();
    const loadPlayers = useStore(state => state.loadPlayers);
    const setPlayers = useSetPlayers();
    const connected = useConnected();
    const players = useStore((state) => state.players);
    useEffect(() => {
                loadPlayers(setPlayers);
            
        }, [connected]
    )
     const players2 = Object.keys(players);
     console.log(players2);

    const test=(e) =>{
        startGame();
    };
    return (
        <div className="WaitingRoom">
            <h2>Welcome to the pregame lobby, {name}</h2>
            {players2.length >= 2 ? (
                <>
                <p>There Are Enough Players to Begin: {players2.length}/4</p>
                <button
                onClick={test}
                className="btn btn-primary"
                >Ready
                </button>
                </>
                
                
                ) : (
                    <>
                    <p> Not Enough Players! only {players2.length} / 4</p>
                    
                    <button
                    disabled={true}
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