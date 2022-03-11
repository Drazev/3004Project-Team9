package com.team9.questgame.game_phases;

import com.team9.questgame.Entities.Players;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handle state switch for GeneralGameController
 */
@Component
public class GeneralStateMachine implements StateMachineI<GeneralStateE> {

    // Type of Story Cards
    private final byte NOT_DRAWN = 0;
    private final byte QUEST_CARD = 1;
    private final byte EVENT_CARD = 2;
    private final byte TOURNAMENT_CARD = 3;

    @Autowired
    @Lazy
    private GeneralGameController controller;

    private byte drawnStoryCardType;

    @Getter
    private GeneralStateE previousState;

    @Setter
    private boolean isPhaseEnded;

    @Getter
    @Setter
    private boolean isGameStartRequested;

    @Getter
    private GeneralStateE currentState;

    public GeneralStateMachine() {
        drawnStoryCardType = NOT_DRAWN;
        previousState = null;
        currentState = GeneralStateE.SETUP;
        isGameStartRequested = false;
    }

    /**
     * Public method for access or set important state
     */

    public boolean isGameStarted() {
        return currentState != GeneralStateE.SETUP && currentState != GeneralStateE.NOT_STARTED;
    }

    /**
     * Player drawn a Quest story card
     */
    public void drawnQuestCard() {
        drawnStoryCardType = QUEST_CARD;
    }

    /**
     * Player drawn an Event story card
     */
    public void drawnEventCard() {
        drawnStoryCardType = EVENT_CARD;
    }

    /**
     * Player drawn a Tournament story card
     */
    public void drawnTournamentCard() {
        drawnStoryCardType = TOURNAMENT_CARD;
    }

    /**
     * Switch to the next state if needed
     */
    public void update() {
        previousState = currentState;
        switch (currentState) {
            case NOT_STARTED:
                currentState = notStartedState();
                break;
            case SETUP:
                currentState = setupState();
                break;
            case DRAW_STORY_CARD:
                currentState = drawStoryCardState();
                break;
            case QUEST_PHASE:
                currentState = questPhaseState();
                break;
            case TOURNAMENT_PHASE:
                currentState = tournamentPhaseState();
                break;
            case EVENT_PHASE:
                currentState = eventPhaseState();
                break;
            case ENDED:
                currentState = endedState();
                break;
            default:
                throw new IllegalStateException("Unknow state: " + currentState);
        }
    }

    /**
     * The following methods returns the next state of the game to be used by update()
     */
    private GeneralStateE notStartedState() {
        // Always switch to SETUP
        return GeneralStateE.SETUP;
    }

    private GeneralStateE setupState() {
        // Start the game when there are enough players and all of them are ready
        if (controller.getPlayers().size() > GeneralGameController.MIN_PLAYERS && isAllPlayerReady() && isGameStartRequested)
            return GeneralStateE.DRAW_STORY_CARD;
        else isGameStartRequested = false;
        return GeneralStateE.SETUP;
    }

    private GeneralStateE drawStoryCardState() {
        switch (drawnStoryCardType) {
            case QUEST_CARD:
                return GeneralStateE.QUEST_PHASE;
            case EVENT_CARD:
                return GeneralStateE.EVENT_PHASE;
            case TOURNAMENT_CARD:
                return GeneralStateE.TOURNAMENT_PHASE;
            default:
                break;
        }
        return GeneralStateE.DRAW_STORY_CARD;
    }

    private GeneralStateE questPhaseState() {
        if (isWinnerFound()) {
            return GeneralStateE.ENDED;
        } else if (this.isPhaseEnded) {
            resetDrawnCard();
            return GeneralStateE.DRAW_STORY_CARD;
        } else {
            return GeneralStateE.QUEST_PHASE;
        }
    }

    private GeneralStateE eventPhaseState() {
        if (isWinnerFound()) {
            return GeneralStateE.ENDED;
        } else if (this.isPhaseEnded) {
            resetDrawnCard();
            return GeneralStateE.DRAW_STORY_CARD;
        } else {
            return GeneralStateE.EVENT_PHASE;
        }
    }

    private GeneralStateE tournamentPhaseState() {
        if (isWinnerFound()) return GeneralStateE.ENDED;
        else if (this.isPhaseEnded) return GeneralStateE.DRAW_STORY_CARD;
        else return GeneralStateE.TOURNAMENT_PHASE;
    }

    private GeneralStateE endedState() {
        return GeneralStateE.ENDED;
    }

    /**
     * Private helper methods
     */
    private boolean isWinnerFound() {
        for (Players p : controller.getPlayers()) {
            if (p.getRank() == controller.getVictoryCondtion()) {
                return true;
            }
        }
        return false;
    }

    private void resetDrawnCard() {
        drawnStoryCardType = NOT_DRAWN;
    }

    private boolean isAllPlayerReady() {
        for (Players p : controller.getPlayers()) {
            if (!p.isReady()) {
                return false;
            }
        }
        return true;
    }
}
