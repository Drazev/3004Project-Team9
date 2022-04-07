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
     /***
      * body = {"remainingPlayers":[{"playerId":0,"handId":0,"playAreaId":0,"name":"test1","rank":"SQUIRE","rankImgSrc":"./Assets/Rank Deck (327x491)/Rank - Squire.png","rankBattlePoints":5,"shields":0},{"playerId":1,"handId":1,"playAreaId":1,"name":"test2","rank":"SQUIRE","rankImgSrc":"./Assets/Rank Deck (327x491)/Rank - Squire.png","rankBattlePoints":5,"shields":0}]}
      */
     console.log("body: " + JSON.stringify(body))
     let remainingPlayers = body.remainingPlayers;
     console.log("remaining players: " + JSON.stringify(remainingPlayers));
     console.log(generalStore().name);
     for(let i = 0; i < remainingPlayers.length; i++){
         console.log(JSON.stringify(remainingPlayers[i]))
         console.log(remainingPlayers[i].name);
         if(remainingPlayers[i].name === generalStore().name){
            tournamentStore().setTournamentSetup(true);
         }
     }
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
 