/**
 * All quest inbound broadcast from the server goes through this module.
 */
 import { generalStore } from "../stores/generalStore"
 import { tournamentStore, useTournamentStageStart } from "../stores/tournamentStore";
 
 export const dispatchTournamentStart = (body) => {
     /**
      * A test stage has begun
      */
     console.log("Tournament stage started " + JSON.stringify(body));
     //generalStore().setActivePlayers(body.remainingPlayers);
     tournamentStore().setTournamentJoinRequest(true);
     tournamentStore().setTournamentStageStart(true);
 }

 export const dispatchTournamentSetup = (body) => {
     console.log("Important " + JSON.stringify(body));
     tournamentStore().setTournamentSetup(true);
     console.log("Tournament setup stage has started");
 }
 
 export const dispatchTournamentEnd = (body) => {
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
     console.log("/topic/tournament/end: " + JSON.stringify(body));
 
     tournamentStore().setTournamentStageStart(false);
     tournamentStore().setTournamentJoinRequest(false);
     generalStore().setStoryCard(null);
     generalStore().setActivePlayers([]);    
 }
 