package com.team9.questgame.game_phases.tournament;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import org.springframework.stereotype.Component;

@Component
public class TournamentPhaseController implements GamePhases<StoryCards> {
    @Override
    public boolean receiveCard(StoryCards card) {
        return false;
    }

    @Override
    public void discardCard(StoryCards card) {

    }

    @Override
    public boolean playCard(StoryCards card) {
        return false;
    }

    @Override
    public void onGameReset() {

    }

    @Override
    public PlayerRewardData getRewards() {
        return null;
    }

    @Override
    public void startPhase(PlayerTurnService playerTurnService) {

    }

    @Override
    public void endPhase() {

    }
}
