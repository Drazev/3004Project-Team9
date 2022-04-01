import create from 'zustand';
import { produce, current } from 'immer'

export const usePlayerStore = create((set, get) => ({
    pData: [],
    playerIDs: [],
    numPlayers: 0,
    processPlayerUpdate: (playerUpdateData) => set((state) => updatePlayerData(state, playerUpdateData)),
    getPlayer: (playerID) => get().pData.find(pDat => pDat.playerId === playerID) 
}));

function updatePlayerData(state, playerUpdateData) {
    if (playerUpdateData == null || !playerUpdateData.hasOwnProperty('playerId')) {
        throw new Error("Recieved malformed player update data!");
    }
    return produce(state, draft => {
        const player = draft.pData.find(player => player.playerId === playerUpdateData.playerId);

        //Check if no player was found with find
        if (player === undefined) {
            draft.playerIDs.push(playerUpdateData.playerId);
            draft.pData.push(playerUpdateData)
            ++draft.numPlayers;
            console.log("Added new player: " + JSON.stringify(playerUpdateData));
            console.log("Now have " + draft.numPlayers + " players with ID's : " + draft.playerIDs.toString());
        } else {
            player.name = playerUpdateData.name;
            player.rank = playerUpdateData.rank;
            player.rankBattlePoints = playerUpdateData.rankBattlePoints;
            player.shields = playerUpdateData.shields;
            player.rankImgSrc = playerUpdateData.rankImgSrc;
        }
        console.log("Player Updated" + JSON.stringify(draft.pData));
        console.log("New State\n" + JSON.stringify(draft), function replacer(key, value) { return value });
    });

}

/**
 * Getters for non-component use
 */
export const playerStore = () => usePlayerStore.getState();

/**
 * Getters
 */
export const useGetPlayer = (id) => usePlayerStore((state) => state.getPlayer(id));

/**
 * Setters
 */
export const useProcessPlayerUpdate = () => usePlayerStore((state) => state.processPlayerUpdate);
