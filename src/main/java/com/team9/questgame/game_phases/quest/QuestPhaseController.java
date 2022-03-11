package com.team9.questgame.game_phases.quest;

import com.team9.questgame.Data.PlayerRewardData;
import com.team9.questgame.Entities.cards.CardTypes;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.Entities.cards.StoryCards;
import com.team9.questgame.game_phases.GamePhases;
import com.team9.questgame.game_phases.GeneralGameController;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class QuestPhaseController implements GamePhases {
    Logger LOG;
    @Getter
    @Autowired
    private QuestPhaseStateMachine stateMachine;
    @Getter
    private StoryCards storyCard;

    @Autowired
    @Lazy
    private GeneralGameController generalController;


    public QuestPhaseController() {
        LOG = LoggerFactory.getLogger(QuestPhaseController.class);
    }

    @Override
    public boolean receiveCard(Cards card) {
        if (card.getSubType() == CardTypes.QUEST) {
            storyCard = (StoryCards) card; // TODO: Remove type casting
            return true;
        }
        return false;
    }

    @Override
    public void discardCard(Cards card) {

    }

    @Override
    public boolean playCard(Cards card) {
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
    public void startPhase() {
        if (storyCard == null) {
            throw new RuntimeException("Cannot start quest phase, storyCard is null");
        }
        onGameReset();
        stateMachine.setPhaseStartRequested(true);
        stateMachine.update();
        if (stateMachine.getCurrentState() == QuestPhaseStatesE.QUEST_SPONSOR) {
            // broadcast that quest has started
            LOG.info("Quest phase started");
        }
    }
}
