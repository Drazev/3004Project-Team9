import React, { useEffect } from "react";
import { startGame } from "../../services/clientSocket";
import "./WaitingRoom.css";
import {
    useName,
    useConnected,
    useLoadPlayers,
    usePlayers,
    useSetPlayers
} from "../../stores/generalStore";

const WaitingRoom = () => {
    const name = useName();
    var players = usePlayers();
    const loadPlayers = useLoadPlayers();
    const setPlayers = useSetPlayers();
    const connected = useConnected();
    useEffect(() => {
        loadPlayers(setPlayers);

    }, [connected]
    )

    if (!(Array.isArray(players))) {
        players = Object.keys(players);
    }


    const test = (e) => {
        startGame();
    };
    return (
        <div className="WaitingRoom">
            <h2>Welcome to the pregame lobby, {name}</h2>
            {players.length >= 2 ? (
                <>
                    <p>There Are Enough Players to Begin: {players.length}/4</p>
                    <button
                        onClick={test}
                        className="ReadyButton"
                    >Ready</button>
                </>
            ) : (
                <>
                    <p> Not Enough Players! only {players.length} / 4</p>
                    <button
                        disabled={true}
                        onClick={test}
                        className="NotReadyButton"
                    >Ready</button>
                </>
            )}
            <h3>Players in lobby</h3>
                {players.map((player, index) => (
                    <p className="PlayerName" key={index}>
                        {player}
                    </p>
                ))}

        </div>
    );

};
export default WaitingRoom;