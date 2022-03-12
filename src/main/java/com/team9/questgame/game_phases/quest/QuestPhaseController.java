package com.team9.questgame.game_phases.quest;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class QuestPhaseController implements GamePhases<StoryCards> {
    Logger LOG;
    @Getter
    @Autowired
    private QuestPhaseStateMachine stateMachine;
    @Getter
    private StoryCards storyCard;

    @Autowired
    @Lazy
    private GeneralGameController generalController;

    private PlayerTurnService playerTurnService;


    public QuestPhaseController() {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
    }

    @Override
    public boolean receiveCard(StoryCards card) {
        if (card.getSubType() == CardTypes.QUEST) {
            storyCard = card; // TODO: Remove type casting
            return true;
        }
        return false;
    }

    @Override
    public void discardCard(StoryCards card) {

    }

    @Override
    public boolean playCard(StoryCards card) {
        return false;
    }

    /**
     * Reset the game
     */
    @Override
    public void onGameReset() {

    }

    @Override
    public PlayerRewardData getRewards() {
        return null;
    }
    /**
     * Reset the phase
     */
    @Override
    public void onPhaseReset() {

    }

    @Override
    public void startPhase(PlayerTurnService playerTurnService) {
        if (storyCard == null) {
            throw new RuntimeException("Cannot start quest phase, storyCard is null");
        }
        onPhaseReset();
        stateMachine.setPhaseStartRequested(true);

        stateMachine.update();
        if (stateMachine.getCurrentState() == QuestPhaseStatesE.QUEST_SPONSOR) {
            // TODO: broadcast that quest has started
            //       start sponsorQuest() /topic/quest/sponsor
            //           { id: long, name: string }

            LOG.info("Quest phase started");
            this.playerTurnService = playerTurnService;
        }
    }

}
