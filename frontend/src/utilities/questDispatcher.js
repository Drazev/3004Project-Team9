/**
 * All quest inbound broadcast from the server goes through this module.
 */
import { questRequestStore } from "../stores/quest/questRequestStore"
import { generalStore } from "../stores/generalStore"
import { playAreaStore } from "../stores/playAreaStore";

export const dispatchSponsorSearchRequest = (body) => {
    /**
     * Server is querying for quest sponsor
     * {
     *   "playerID": 0,
     *   "name": "Bob",
     *   "PlayerRanks": "squire",
     *   "rankBattlePoints": 5,
     *   "shields": 3
     * }
     */
    console.log("/topic/quest/sponsor-search: " + JSON.stringify(body));
    if (generalStore().name === body.name) {
        // When this player holds the current turn
        questRequestStore().setSponsorSearchRequest(true);
    }
}

export const dispatchSponsorFound = (body) => {
    /**
     * Server is sending quest sponsor
     * {
     *   "playerID": 0,
     *   "name": "Bob",
     *   "PlayerRanks": "squire",
     *   "rankBattlePoints": 5,
     *   "shields": 3
     * }
    */
    console.log("/topic/quest/sponsor-found: " + JSON.stringify(body));
    generalStore().setSponsorName(body.name);
    if (generalStore().name === body.name) {
        // When this player is the sponsor
        generalStore().setIsSponsoring(true);
    }
}

export const dispatchQuestJoinRequest = (body) => {
    /**
     * Server is seeking for players to join a quest
     * {
     *   "playerID": 0,
     *   "name": "Bob",
     *   "PlayerRanks": "squire",
     *   "rankBattlePoints": 5,
     *   "shields": 3
     * }
     */
    console.log("/topic/quest/join-request: " + JSON.stringify(body));
    if (generalStore().sponsorName !== generalStore().name) {
        // When this player is not the quest sponsor
        questRequestStore().setQuestJoinRequest(true);
    }
}

export const dispatchFoeStageStart = (body) => {
    /**
     * Server is broadcasting that the foe stage has started
     * {
     *   "remainingPlayers": [
     *      {
     *        "playerID": 0,
     *        "name": "Bob",
     *        "PlayerRanks": "squire",
     *        "rankBattlePoints": 5,
     *        "shields": 3
     *      }
     *   ],
     * }
     */
    console.log("/topic/quest/foe-stage-start: " + JSON.stringify(body));

    generalStore().setActivePlayers(body.remainingPlayers);
    generalStore().setFoeStageStart(true);
}

export const dispatchTestStageStart = (body) => {
    /**
     * A test stage has begun
     */
    console.log("Test stage started " + JSON.stringify(body));
    generalStore().setActivePlayers(body.remainingPlayers);
    generalStore().setTestStageStart(true);

}


export const dispatchBidRequest = (body) => {
    /**
     * The server is requesting the client to place a bid
     * Payload:
     * 
       {
        "player": {"playerID": string, "name": string},
        "maxBid": Int,
        "maxBidPlayer": {"playerID": string, "name": string}
        }
     */
    console.log("/topic/quest/request-bid" + JSON.stringify(body));
    generalStore().setCurrentBidder(body.player);
    generalStore().setMaxBid(body.maxBid);
    generalStore().setMaxBidPlayer(body.setMaxBidPlayer);
    if(body.player.name === generalStore().name){
        questRequestStore().setBidRequest(true);
    }
}

export const dispatchStageEnd = (body) => {
    /**
     * Server is broadcasting the end of a stage
     * {
     *   "remainingPlayers": [
     *      {
     *        "playerID": 0,
     *        "name": "Bob",
     *        "PlayerRanks": "squire",
     *        "rankBattlePoints": 5,
     *        "shields": 3
     *      }
     *   ],
     * }
     */
    console.log("/topic/quest/stage-end: " + JSON.stringify(body));

    generalStore().setActivePlayers(body.remainingPlayers);
    generalStore().setFoeStageStart(false);
    generalStore().setTestStageStart(false);
}

export const dispatchQuestEnd = (body) => {
    /**
     * Server is broadcasting that the quest has ended
     * {
     *   "winners": [
     *     {
     *       "playerID": 0,
     *       "name": "Bob",
     *       "PlayerRanks": "squire",
     *       "rankBattlePoints": 5,
     *       "shields": 3
     *     }
     *   ]
     * }
     */
    console.log("/topic/quest/end: " + JSON.stringify(body));

    generalStore().setFoeStageStart(false);
    generalStore().setTestStageStart(false);
    generalStore().setStoryCard(null);
    generalStore().setIsSponsoring(false);
    generalStore().setSponsorName("");
    generalStore().setActivePlayers([]);
    playAreaStore().setStageAreas([]);
    
}
