package com.team9.questgame.game_phases.tournament;

import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.Effects.EffectResolverService;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameRequest;
import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.GeneralStateE;
import com.team9.questgame.game_phases.GeneralStateMachine;
import com.team9.questgame.game_phases.event.EventPhaseController;
import com.team9.questgame.game_phases.quest.QuestPhaseStatesE;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.game_phases.utils.StateMachineObserver;
import com.team9.questgame.gamemanager.record.socket.NotificationOutbound;
import com.team9.questgame.gamemanager.record.socket.TournamentPlayersOutbound;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.NotificationOutboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TournamentPhaseController implements GamePhases<TournamentCards,TournamentPhaseStatesE>, StateMachineObserver<GeneralStateE> {
    private final Logger LOG;
    private final GeneralGameController gameController;
    private TournamentCards card;
    private TournamentPhaseStatesE state;
    private TournamentPhaseStatesE previousState;
    private Map<Players, Integer> competitors;
    private ArrayList<Players> winners;
    private PlayerTurnService turnService;
    private int joinAttempts;
    private int participantSetupResponses;
    @Getter
    private boolean isTiebreaker;
    private boolean isWinnersTieBreaker;
    private int oldCompetitorOffset;

    public TournamentPhaseController(GeneralGameController gameController, TournamentCards card) {
        LOG = LoggerFactory.getLogger(EventPhaseController.class);
        GeneralStateMachine.getService().registerObserver(this);
        competitors = new HashMap<>();
        winners = new ArrayList<>();
        this.gameController = gameController;
        this.card=card;
        joinAttempts = 0;
        participantSetupResponses = 0;
        isTiebreaker = false;
        oldCompetitorOffset = 0;
        this.state = TournamentPhaseStatesE.READY;
    }

    public TournamentPhaseController(GeneralGameController gameController, ArrayList<Players> tiedCompetitors) {
        LOG = LoggerFactory.getLogger(EventPhaseController.class);
        GeneralStateMachine.getService().registerObserver(this);
        competitors = new HashMap<>();
        for(Players player : tiedCompetitors){
            competitors.put(player, player.getRank().getRankBattlePointValue() - player.getPlayArea().getBattlePoints());
        }
        winners = new ArrayList<>();
        this.gameController = gameController;

        CardFactory cf = CardFactory.getInstance();
        this.card=(TournamentCards) cf.createCard(new AdventureDecks(), StoryDeckCards.TOURNAMENT_AT_YORK);
        joinAttempts = 0;
        participantSetupResponses = 0;
        isTiebreaker = true;
        oldCompetitorOffset = 0;
        isWinnersTieBreaker = true;
        this.state = TournamentPhaseStatesE.JOIN;
    }


    @Override
    public TournamentPhaseStatesE getCurrState() {
        return state;
    }

    @Override
    public void startPhase(PlayerTurnService playerTurnService) {
        InboundService.getService().registerTournamentPhaseController(this);
        if(this.state != TournamentPhaseStatesE.READY && this.state != TournamentPhaseStatesE.JOIN) {
            LOG.error("The Tournament Phase Controller is in state " +
                    state + "when it should be in state READY. Returning");
            return;
        }
        turnService = playerTurnService;
        nextState();

    }


    /**
     * Player's decision to join a Tournament or not
     * @param player the player who sent this request
     * @param joined true if they want to join this stage, false otherwise
     */
    public void checkJoinResult(Players player, boolean joined){
        if (state != TournamentPhaseStatesE.JOIN) {
            throw new IllegalStateException("Players can only join a tournament when in JOIN state");
        } else if (competitors.containsKey(player)) {
            throw new IllegalGameRequest("This player is already in the tournament", player);
        }

        // Increment this counter so that the stateMachine knows when all players replied
        this.joinAttempts++;

        if(joined){
            player.getPlayArea().registerGamePhase(this);
            player.getPlayArea().setPlayerTurn(true);
            //starting bp must not include already in play allies
            competitors.put(player,
                    player.getRank().getRankBattlePointValue() - player.getPlayArea().getBattlePoints());
            player.getPlayArea().setPlayerTurn(true);
        }

        if(joinAttempts == turnService.getPlayers().size()) {
            //if only one person joined, go to resolution
            if(competitors.size() == 1){
                this.state = TournamentPhaseStatesE.PLAYER_SETUP;
            }else if(competitors.size() == 0){
                this.state = TournamentPhaseStatesE.REWARDS;
            }
            nextState();
        }
    }

    /**
     * Deal an adventure card to each competitor
     */
    private void dealAdventureCard(){
        HashMap<Players,Integer> drawList = new HashMap<>();
        for(Players player : competitors.keySet()){
            drawList.put(player,1);
        }
        EffectResolverService.getService().drawAdventureCards(drawList);
        nextState();
    }

    /**
     * Set up tournament for tiebreaker match
     */
    public void tiebreakerSetup(){
        oldCompetitorOffset = competitors.size() - winners.size();
        competitors.clear();
        for(Players player : winners){
            NotificationOutboundService.getService().sendGoodNotification(
                    player,
                    new NotificationOutbound("Tournament Tie",
                            "You met your match! Prepare for another round of combat!",
                            "", ""),
                    null
            );
            //in play allies and amours are counted
            competitors.put(player,0);
            player.getPlayArea().setPlayerTurn(true);
        }
        winners.clear();
        participantSetupResponses = 0;
        nextState();
    }

    /**
     * A Tournament participant informs that their stage setup is complete
     * their battlepoints(minus already existing ally cards points) are recorded
     */
    public void checkParticipantSetup(Players player){
        if (state != TournamentPhaseStatesE.PLAYER_SETUP) {
            throw new IllegalStateException("Players can only join a tournament when in PLAYER_SETUP state");
        }
        if(!competitors.containsKey(player)){
            throw new IllegalGameRequest(
                    "A player can only complete tournament setup if they agreed to participate", player);
        }
        participantSetupResponses++;
        player.getPlayArea(). setPlayerTurn(false);
        competitors.put(player,
                competitors.get(player) + player.getPlayArea().getBattlePoints());
        if(participantSetupResponses == competitors.size()) {
            nextState();
        }
    }

    /**
     * A Tournament participant informs that their stage setup is complete
     */
    public void resolveTournament(){
        int max = Collections.max(competitors.values());
        for(Players competitor : competitors.keySet()){
            if(competitors.get(competitor) == max){
                winners.add(competitor);
            }
        }
        for(Players competitor : competitors.keySet()){
            if(!winners.contains(competitor)){
                NotificationOutboundService.getService().sendBadNotification(
                        competitor,
                        new NotificationOutbound("Tournament Loss",
                                "You proved no match for the competition!",
                                "", ""),
                        null
                );
            }
        }
        nextState();
    }

    /**
     * Distribute rewards to winner(s)
     */
    public void distributeRewards(){
        if(isWinnersTieBreaker){
            //TODO: notify game controller of winners
        }
        if(winners.size() > 1 && !isTiebreaker){
            this.state = TournamentPhaseStatesE.TIEBREAKER;
            for(Players player : competitors.keySet()){
                player.getPlayArea().discardAllWeapons();
            }
            nextState();
            return;
        }
        int rewards = card.getBonusShields() + competitors.size() + oldCompetitorOffset;
        HashMap<Players, Integer> participantRewards = new HashMap<>();
        for(Players player : winners){
            participantRewards.put(player, rewards);
            NotificationOutboundService.getService().sendGoodNotification(
                    player,
                    new NotificationOutbound("Tournament Victory",
                            "You proved yourself worthy and came out of the tournament victorious!",
                            "", ""),
            null
            );
        }
        EffectResolverService.getService().playerAwardedShields(participantRewards);
        nextState();
    }

    @Override
    public void endPhase() {
        for(Players player : competitors.keySet()){
            //discard amour and weapons
            player.getPlayArea().onGamePhaseEnded();
        }

        InboundService.getService().unregisterTournamentPhaseController();
        OutboundService.getService().broadcastTournamentPhaseEnded(
                new TournamentPlayersOutbound(getWinnerData())
        );
        onGameReset();
        gameController.requestPhaseEnd();
    }

    private ArrayList<PlayerData> getWinnerData(){
        ArrayList<PlayerData> winnerData = new ArrayList<>();
        for(Players player : winners){
            winnerData.add(player.generatePlayerData());
        }
        return winnerData;
    }
    private ArrayList<PlayerData> getPlayerData(){
        ArrayList<PlayerData> playerData = new ArrayList<>();
        for(Players player : competitors.keySet()){
            playerData.add(player.generatePlayerData());
        }
        return playerData;
    }

    public void onGameReset() {
        this.state = TournamentPhaseStatesE.READY;
        if(card!=null) {
            card.discardCard();
            this.card = null;
        }
        GeneralStateMachine.getService().unregisterObserver(this);
        joinAttempts = 0;
        participantSetupResponses = 0;
        competitors.clear();
        winners.clear();
        isTiebreaker = false;
    }



    public void nextState(){
        switch(state){
            case READY -> {
                this.state = TournamentPhaseStatesE.JOIN;
                OutboundService.getService().broadcastTournamentPhaseStart();
            }
            case JOIN -> {
                this.state = TournamentPhaseStatesE.DRAW_CARD;
                dealAdventureCard();
            }
            case DRAW_CARD -> {
                this.state = TournamentPhaseStatesE.PLAYER_SETUP;
                OutboundService.getService().broadcastTournamentSetup(
                        new TournamentPlayersOutbound(getPlayerData())
                );
            }
            case PLAYER_SETUP -> {
                this.state = TournamentPhaseStatesE.RESOLUTION;
                resolveTournament();
            }
            case RESOLUTION -> {
                this.state = TournamentPhaseStatesE.REWARDS;
                distributeRewards();
            }
            case REWARDS -> {
                this.state = TournamentPhaseStatesE.ENDED;
                endPhase();
            }
            case TIEBREAKER -> {
                this.state = TournamentPhaseStatesE.DRAW_CARD;
                isTiebreaker = true;
                tiebreakerSetup();
            }

        }
    }




    @Override
    public TournamentCards getCard() {
        return card;
    }

    @Override
    public void observerStateChanged(GeneralStateE newState) {
        if(newState==GeneralStateE.PLAYER_HAND_OVERSIZE) {
            this.previousState = this.state;
            this.state = TournamentPhaseStatesE.BLOCKED.BLOCKED;
//            LOG.info(String.format("Moved from state %s to state %s", previousState, currentState));
        }
        else if(this.state==TournamentPhaseStatesE.BLOCKED) {
            this.state = this.previousState;
            this.previousState = TournamentPhaseStatesE.BLOCKED;
            nextState();
        }
    }
}
