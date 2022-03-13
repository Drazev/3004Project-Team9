package com.team9.questgame.game_phases;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.CardTypes;
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

    @Autowired
    @Lazy
    private GeneralGameController controller;

    @Getter
    private GeneralStateE currentState;

    @Getter
    private GeneralStateE previousState;

    @Getter
    @Setter
    private boolean isPhaseEndRequested;

    @Getter
    @Setter
    private boolean isGameStartRequested;

    @Getter
    @Setter
    private boolean isGamePhaseRequested;

    @Getter
    @Setter
    private boolean isHandOversizeRequested;

    public GeneralStateMachine() {
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

    public boolean isInPhases() {
        return currentState == GeneralStateE.QUEST_PHASE || currentState == GeneralStateE.TOURNAMENT_PHASE || currentState == GeneralStateE.EVENT_PHASE;
    }

    /**
     * Switch to the next state if needed
     */
    public void update() {
        GeneralStateE tempState = currentState;

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
            case PLAYER_HAND_OVERSIZE:
                currentState = playerHandOversizeState();
                break;
            default:
                throw new IllegalStateException("Unknown state: " + currentState);
        }

        if (tempState != currentState) {
            // State changed, update previousState
            this.previousState = tempState;
        }

        resetAllRequest();
    }

    /**
     * The following methods returns the next state of the game to be used by update()
     */
    private GeneralStateE notStartedState() {
        // Always switch to SETUP
        return GeneralStateE.SETUP;
    }

    private GeneralStateE setupState() {
        // Start the game
        GeneralStateE nextState;
        if (!isAllHandNotOversize() || isHandOversizeRequested) {
            nextState = GeneralStateE.PLAYER_HAND_OVERSIZE;
        } else if (controller.getPlayers().size() >= GeneralGameController.MIN_PLAYERS && isAllPlayerReady() && isGameStartRequested) {
            nextState = GeneralStateE.DRAW_STORY_CARD;
        } else {
            nextState = GeneralStateE.SETUP;
        }
        return nextState;
    }

    private GeneralStateE drawStoryCardState() {
        GeneralStateE nextState;
        if (!isAllHandNotOversize() || isHandOversizeRequested) {
            nextState = GeneralStateE.PLAYER_HAND_OVERSIZE;
        } else if (controller.getStoryCard() != null && isGamePhaseRequested) {
            switch (controller.getStoryCard().getSubType()) {
                case QUEST:
                    nextState = GeneralStateE.QUEST_PHASE;
                    break;
                case EVENT:
                    nextState = GeneralStateE.EVENT_PHASE;
                    break;
                case TOURNAMENT:
                    nextState = GeneralStateE.TOURNAMENT_PHASE;
                    break;
                default:
                    throw new RuntimeException("Unexpected Story Card type");
            }
        } else {
            nextState = GeneralStateE.DRAW_STORY_CARD;
        }

        return nextState;
    }

    private GeneralStateE questPhaseState() {
        GeneralStateE nextState;
        if (!isAllHandNotOversize() || isHandOversizeRequested) {
            nextState = GeneralStateE.PLAYER_HAND_OVERSIZE;
        } else if (isWinnerFound()) {
            nextState = GeneralStateE.ENDED;
        } else if (this.isPhaseEndRequested) {
            nextState = GeneralStateE.DRAW_STORY_CARD;
        } else {
            nextState = GeneralStateE.QUEST_PHASE;
        }
        return nextState;
    }

    private GeneralStateE eventPhaseState() {
        GeneralStateE nextState;
        if (!isAllHandNotOversize() || isHandOversizeRequested) {
            nextState = GeneralStateE.PLAYER_HAND_OVERSIZE;
        } else if (isWinnerFound()) {
            nextState = GeneralStateE.ENDED;
        } else if (this.isPhaseEndRequested) {
            nextState = GeneralStateE.DRAW_STORY_CARD;
        } else {
            nextState = GeneralStateE.EVENT_PHASE;
        }
        return nextState;
    }

    private GeneralStateE tournamentPhaseState() {
        GeneralStateE nextState;
        if (!isAllHandNotOversize() || isHandOversizeRequested) {
            nextState = GeneralStateE.PLAYER_HAND_OVERSIZE;
        } else if (isWinnerFound()) {
            nextState = GeneralStateE.ENDED;
        } else if (this.isPhaseEndRequested) {
            nextState = GeneralStateE.DRAW_STORY_CARD;
        } else {
            nextState = GeneralStateE.TOURNAMENT_PHASE;
        }
        return nextState;
    }

    private GeneralStateE endedState() {
        return GeneralStateE.ENDED;
    }

    private GeneralStateE playerHandOversizeState() {
        GeneralStateE nextState;
        if (isAllHandNotOversize() || isHandOversizeRequested) {
            // Go back to whatever state that was blocked by HAND_OVERSIZE
            nextState = this.previousState;
        } else {
            nextState = GeneralStateE.PLAYER_HAND_OVERSIZE;
        }
        return nextState;
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

    private boolean isAllPlayerReady() {
        for (Players p : controller.getPlayers()) {
            if (!p.isReady()) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllHandNotOversize() {
        for(Players p: controller.getPlayers()) {
            if (p.getHand().isHandOversize()) {
                return false;
            }
        }
        return true;
    }

    private void resetAllRequest() {
        setHandOversizeRequested(false);
        setGamePhaseRequested(false);
        setGameStartRequested(false);
        setPhaseEndRequested(false);
    }
}
