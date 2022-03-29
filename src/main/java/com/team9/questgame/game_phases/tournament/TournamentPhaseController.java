package com.team9.questgame.game_phases.tournament;

import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.Entities.cards.TournamentCards;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.utils.PlayerTurnService;

public class TournamentPhaseController implements GamePhases<TournamentCards,TournamentPhaseStatesE> {
    private final GeneralGameController gameController;
    private final TournamentCards card;

    public TournamentPhaseController(GeneralGameController gameController, TournamentCards card) {
        this.gameController = gameController;
        this.card=card;
    }


    @Override
    public TournamentPhaseStatesE getCurrState() {
        return null;
    }

    @Override
    public void startPhase(PlayerTurnService playerTurnService) {

    }

    @Override
    public void endPhase() {

    }

    @Override
    public TournamentCards getCard() {
        return card;
    }
}
