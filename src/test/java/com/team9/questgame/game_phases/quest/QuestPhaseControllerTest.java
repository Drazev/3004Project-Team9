package com.team9.questgame.game_phases.quest;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.IllegalGameStateException;
import com.team9.questgame.exception.IllegalQuestPhaseStateException;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.exception.PlayerNotFoundException;
import com.team9.questgame.game_phases.quest.QuestPhaseStatesE;
import com.team9.questgame.game_phases.utils.PlayerTurnService;
import com.team9.questgame.gamemanager.service.QuestPhaseInboundService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestPhaseControllerTest {

    @Autowired
    private QuestPhaseController controller;

    @Autowired
    private QuestPhaseInboundService inboundService;

    private ArrayList<Players> players;
    private PlayerTurnService turnService;

    @BeforeEach
    void setUp(){
        players = new ArrayList<>();
        players.add(new Players("Player 1"));
        players.add(new Players("Player 2"));
        players.add(new Players("Player 3"));
        players.add(new Players("Player 4"));
        players.add(new Players("Player 5"));

        turnService = new PlayerTurnService(players);

    }

    @AfterEach
    void tearDown() {
    }

    @Autowired
    void contextLoad() {
        assertThat(controller).isNotNull();

    }

    @Test
    void receiveCard(){
        assertThat(controller.getPlayers().size()).isEqualTo(0);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);

        // Can receive a card when the phase is not started
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));

        // Cannot receive a card the phase is started
        controller.startPhase(turnService);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.receiveCard(questCards.get(1)));
    }

    @Test
    void startPhase() {
        // Cannot start phase when there's no QuestCard
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);
        assertThat(controller.getQuestCard()).isNull();

        assertThrows(RuntimeException.class, () -> controller.startPhase(null)); // Param doesn't matter

        // Can start phase when there's a QuestCard
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));
        assertThat(controller.getQuestCard()).isNotNull();

        controller.startPhase(new PlayerTurnService(players));
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);
        assertThat(controller.getPlayerTurnService()).isNotNull();
        for (Players p: controller.getPlayerTurnService().getPlayers()) {
            assertThat(p.getPlayArea().getPhaseController()).isEqualTo(controller);
            assertThat(p.getPlayArea().getQuestCard()).isEqualTo(controller.getQuestCard());
        }

        // Cannot start phase when the quest has started
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.startPhase(null)); // Param doesn't matter
    }

    @Test
    void checkSponsorResult() {
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);
        assertThrows(IllegalGameStateException.class, () -> controller.checkSponsorResult(players.get(0), false));
    }

//    @Test
//    void getSponsor(){
//        assertThat(controller.getPlayers().size()).isEqualTo(0);
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);
//        // Get a quest card and generate the quest phase
//        ArrayList<QuestCards> questCards = getQuestCards();
//
//        controller.receiveCard(questCards.get(0));
//        controller.startPhase(turnService);
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);
//
//        controller.checkSponsorResult(players.get(0), false);
//        controller.checkSponsorResult(players.get(1), false);
//        controller.checkSponsorResult(players.get(2), true);
//        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.checkSponsorResult(players.get(3), true));
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SETUP);
//    }

    //   @Test
//    void checkJoins(){
//        assertThat(controller.getPlayers().size()).isEqualTo(0);
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);
//        // Get a quest card and generate the quest phase
//        ArrayList<QuestCards> questCards = getQuestCards();
//
//        controller.receiveCard(questCards.get(0));
//        controller.startPhase(turnService);
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);
//
//        controller.checkSponsorResult(players.get(0), false);
//        controller.checkSponsorResult(players.get(1), false);
//        controller.checkSponsorResult(players.get(2), true);
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SETUP);
//
//        controller.setupStage();
//
//        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_JOIN);
//
//        controller.checkJoinResult(players.get(0), false);
//        controller.checkJoinResult(players.get(1), true);
//        controller.checkJoinResult(players.get(2), true);
//
//    }
    ArrayList<QuestCards> getQuestCards() {
        CardFactory cf = CardFactory.getInstance();
        AdventureDecks testDeck = new AdventureDecks();
        HashMap<StoryDeckCards,Integer> deckList = new HashMap<>();
        ArrayList<QuestCards> cards = new ArrayList<>();
        deckList.put(StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL,1);
        deckList.put(StoryDeckCards.TEST_OF_THE_GREEN_KNIGHT,1);
        deckList.put(StoryDeckCards.SEARCH_FOR_THE_QUESTING_BEAST,1);
        deckList.put(StoryDeckCards.DEFEND_THE_QUEENS_HONOR,1);
        deckList.put(StoryDeckCards.RESCUE_THE_FAIR_MAIDEN,1);
        deckList.put(StoryDeckCards.JOURNEY_THROUGH_THE_ENCHANTED_FOREST,1);
        deckList.put(StoryDeckCards.VANQUISH_KING_ARTHURS_ENEMIES,2);
        deckList.put(StoryDeckCards.SLAY_THE_DRAGON,1);
        deckList.put(StoryDeckCards.BOAR_HUNT,2);
        deckList.put(StoryDeckCards.REPEL_THE_SAXON_RAIDERS,2);

        for(Map.Entry<StoryDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for (int i = 0; i < e.getValue(); ++i) {
                QuestCards card = (QuestCards) cf.createCard(testDeck, e.getKey());
                cards.add(card);
            }
        }
        return cards;
    }
}