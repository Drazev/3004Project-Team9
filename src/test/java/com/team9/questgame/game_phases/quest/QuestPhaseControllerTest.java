package com.team9.questgame.game_phases.quest;

import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.exception.*;
import com.team9.questgame.game_phases.GeneralGameController;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestPhaseControllerTest {

    @Autowired
    private QuestPhaseController controller;

    @Autowired
    private GeneralGameController generalGameController;

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
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);

        // Can receive a card when the phase is not started
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));

        // Cannot receive a card the phase is started
        controller.startPhase(turnService);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        assertThat(controller.receiveCard(questCards.get(1))).isFalse();
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
        // Cannot send sponsor result before the Quest Phase starts
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.checkSponsorResult(players.get(0), false));

        // Can send sponsor result when the Quest Phase is in QUEST_SPONSOR state
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));
        controller.startPhase(new PlayerTurnService(players));
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        // Cannot accept sponsor from players that don't hold this turn
        assertThat(controller.getPlayerTurnService().getPlayerTurn()).isEqualTo(players.get(0));
        assertThrows(IllegalGameRequest.class,
                    () -> controller.checkSponsorResult(players.get(1), false));
        assertThrows(IllegalGameRequest.class,
                    () -> controller.checkSponsorResult(players.get(2), false));
        assertThrows(IllegalGameRequest.class,
                    () -> controller.checkSponsorResult(players.get(3), false));
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        // Current turn holder declines to sponsor, the quest should seek out for more sponsors
        controller.checkSponsorResult(players.get(0), false);
        assertThat(controller.getSponsor()).isNull();
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);
        controller.checkSponsorResult(players.get(1), false);
        assertThat(controller.getSponsor()).isNull();
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);
        controller.checkSponsorResult(players.get(2), false);
        assertThat(controller.getSponsor()).isNull();
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        // If all players decline to sponsor then the quest should end
        controller.checkSponsorResult(players.get(3), false);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.NOT_STARTED);

        // Restart the quest
        controller.receiveCard(questCards.get(1));
        controller.startPhase(new PlayerTurnService(players));
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);

        // If a sponsor is found, the quest should start to setting up the stages
        controller.checkSponsorResult(players.get(0), true);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SETUP);
        assertThat(controller.getSponsor()).isEqualTo(players.get(0));
    }

    @Test
    void questSetupComplete() {
        // Cannot call stageSetupComplete when not in SETUP stage
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.questSetupComplete(players.get(0)));

        // Can call when in SETUP stage
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));
        controller.startPhase(new PlayerTurnService(players));
        controller.checkSponsorResult(players.get(0), true);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SETUP);

        // Cannot complete a stage setup when there's no battle point in that stage (No foe)
        // TODO: This probably has to change with Test card
        assertThrows(RuntimeException.class, () -> controller.questSetupComplete(players.get(0)));

    }

    @Test
    void playerPlayCard(){
        // Doesn't allow when not in quest
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.sponsorPlayCard(null, 0,0,0));

        sponsorQuest(); // Move to SETUP state

        // The sponsor setup the quest
        Players sponsor = controller.getSponsor();
        AdventureDecks aDeck = new AdventureDecks();

        AdventureCards drawnCard;

        // Draw 1 foe card
        do {
            drawnCard = aDeck.drawCard(sponsor.getHand());
        } while (drawnCard.getSubType() != CardTypes.FOE);
        int prevHandSize = sponsor.getHand().getHandSize();

        long cardId = drawnCard.getCardID();

        // Play card from sponsor's hand into stage 0
        assertThat(controller.getStages().get(0).size()).isEqualTo(0);
        controller.sponsorPlayCard(sponsor, drawnCard.getCardID(), -1, 0);

        assertThat(controller.getStages().get(0).getAllCards().values().stream().toList().get(0)).isEqualTo(drawnCard);
        assertThat(sponsor.getHand().getHand().size()).isEqualTo(prevHandSize - 1);

        controller.sponsorPlayCard(sponsor, drawnCard.getCardID(), 0, 1);
        assertThat(controller.getStages().get(0).size()).isEqualTo(0);
        assertThat(controller.getStages().get(1).getAllCards().values().stream().toList().get(0)).isEqualTo(drawnCard);

        controller.sponsorPlayCard(sponsor, drawnCard.getCardID(), 1, -1);
        assertThat(sponsor.getHand().getHand().size()).isEqualTo(prevHandSize);
        assertThat(controller.getStages().get(1).size()).isEqualTo(0);

        assertThrows(UnsupportedOperationException.class, () -> controller.sponsorPlayCard(sponsor, cardId, -1, -1));
    }

    @Test
    void checkJoins() {


    }

    @Test
    void checkJoinResult() {

    }

    @Test
    void endPhase() {
        // Cannot end when not in ENDED state
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.NOT_STARTED);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));

        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.QUEST_SPONSOR);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.QUEST_SETUP);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.QUEST_JOIN);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.STAGE_ONE);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.STAGE_TWO);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.STAGE_THREE);
        assertThrows(IllegalQuestPhaseStateException.class, () -> controller.endPhase());

        // Can end in ENDED state and should result in NOT_STARTED state
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.ENDED);
        controller.endPhase();
        assertThat(controller.getQuestCard()).isNull();
        controller.getStateMachine().setCurrentState(QuestPhaseStatesE.NOT_STARTED);
    }

    /**
     * Set up the game to reach QUEST_SPONSOR state
     */
    private void startQuest() {
        ArrayList<QuestCards> questCards = getQuestCards();
        controller.receiveCard(questCards.get(0));
        assertThat(controller.getQuestCard()).isNotNull();

        controller.startPhase(new PlayerTurnService(players));
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SPONSOR);
    }

    /**
     * Set up the game to reach QUEST_SETUP state
     */
    private void sponsorQuest() {
        startQuest();
        controller.checkSponsorResult(players.get(0), true);
        assertThat(controller.getStateMachine().getCurrentState()).isEqualTo(QuestPhaseStatesE.QUEST_SETUP);
        assertThat(controller.getSponsor()).isEqualTo(players.get(0));
        assertThat(controller.getPlayerTurnService().getPlayerTurn()).isEqualTo(players.get(0));
    }

    private ArrayList<QuestCards> getQuestCards() {
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