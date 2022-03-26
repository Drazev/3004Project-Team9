package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.GeneralStateE;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import com.team9.questgame.gamemanager.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HandTest {


    @Autowired
    GeneralGameController game;

    @Autowired
    SessionService session;
    ArrayList<Players> players;
    ArrayList<Hand> hands;
    ArrayList<PlayerPlayAreas> pPlayAreas;
    AdventureDecks aDeck;
    StoryDecks sDeck;

    QuestPhaseController testPhaseController;
    TestPlayArea testStage;


    @JsonBackReference
    Players player;

    Logger LOG;

    ObjectMapper objMap;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        players = new ArrayList<>();
        hands = new ArrayList<>();
        pPlayAreas = new ArrayList<>();
        LOG = LoggerFactory.getLogger(PlayerPlayAreasTest.class);
        objMap = ApplicationContextHolder.getContext().getBean(ObjectMapper.class);
        aDeck = game.getADeck();
        sDeck = game.getSDeck();
        testStage = new TestPlayArea();
        testPhaseController = new QuestPhaseController();
        session.registerPlayer("Player 1");
        session.registerPlayer("Player 2");
        session.registerPlayer("Player 3");
        session.registerPlayer("Player 4");
        players.addAll(session.getPlayerMap().values());
        player = players.get(0);
        for(Players p : players) {
            game.playerJoin(p);
        }
        game.startGame();
        assertThat(game.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        game.drawStoryCard(player);
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        for(int i=0;i<players.size();++i) {
            hands.add(players.get(i).getHand());
            pPlayAreas.add(players.get(i).getPlayArea());
        }
        for(Players p : players) {
            LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(p));
        }
    }
    void setupPlayerPlayAreasToPlayCards() {
        for(Players p : players) {
            p.getPlayArea().registerGamePhase(testPhaseController);
            p.getPlayArea().onStageChanged(testStage);
            p.getPlayArea().setPlayerTurn(true);
        }
    }

    void playAllNonDuplicateCardsFromHand() {
        for(Hand h : hands) {
            HashSet<AllCardCodes> uniqueCardCodes = new HashSet<>();
            for(CardData card : h.generateCardData()) {
                if(!uniqueCardCodes.contains(card.cardCode())) {
                    h.playCard(card.cardID());
                    uniqueCardCodes.add(card.cardCode());
                }
            }
        }
    }


    @Test
    void receiveCard() throws JsonProcessingException {
        Hand hand = player.getHand();
        setupPlayerPlayAreasToPlayCards();
        LOG.info("Hand state before card received.");
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            aDeck.drawCard(hand);
        }
        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
    }

    @Test
    void discardCard() throws JsonProcessingException {
        Hand hand =  player.getHand();
        setupPlayerPlayAreasToPlayCards();
        PlayerPlayAreas pa = player.getPlayArea();
        QuestPhaseController testPhaseController = new QuestPhaseController();
        TestPlayArea testStage = new TestPlayArea();
        pa.registerGamePhase(testPhaseController);
        pa.onStageChanged(testStage);
        pa.setPlayerTurn(true);
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            aDeck.drawCard(hand);
        }
        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
        for(CardData card : hand.generateCardData()) {
            hand.discardCard(card.cardID());
        }
        assert(hand.generateCardData().size()==0);
        LOG.info("Hand Printout: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));

    }

    @Test
    void testDiscardCard() {
    }

    @Test
    void playCard() throws JsonProcessingException {
        setupPlayerPlayAreasToPlayCards();
        PlayerPlayAreas area = player.getPlayArea();
        Hand hand = player.getHand();
//        LOG.info("Hand state before card received.");
//        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
//            aDeck.drawCard(hand);
//        }
        LOG.info("Hand Printout: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));

//        HashSet<AllCardCodes> uniqueCardCodes = new HashSet<>();

//        for(CardData card : hand.generateCardData()) {
//            LOG.info("PLAY CARD: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(card));
//            if(!uniqueCardCodes.contains(card.cardCode())) {
//                if(hand.playCard(card.cardID()))
//                {
//                    uniqueCardCodes.add(card.cardCode());
//                }
//                else {
//                    assert(card.subType()==CardTypes.FOE || card.subType()==CardTypes.TEST);
//                    LOG.info("PLAYCARD: "+card.cardCode()+" REJECTED because it's a Foe or Test and was not accpted by Phase");
//                }
//            }
//            else {
//                LOG.info("CARD WAS DUPLICATE - NOT PLAYED");
//            }
//        }
        playAllNonDuplicateCardsFromHand();
        assert(hand.getHandSize()+area.size()==(Hand.MAX_HAND_SIZE));
        LOG.info("Hand Printout: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
        assertFalse(hand.playCard(141098754));

    }

//    @Test
//    void testGetUniqueCardsCodesBySubType() {
//        Hand hand = player.getHand();
//        setupPlayerPlayAreasToPlayCards();
//        CardFactory cf = CardFactory.getInstance();
//        AdventureDecks testDeck = new AdventureDecks();
//        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
//        deckList.put(AdventureDeckCards.EXCALIBUR,2);
//        deckList.put(AdventureDeckCards.LANCE,2);
//        deckList.put(AdventureDeckCards.TEST_OF_VALOR,1);
//        deckList.put(AdventureDeckCards.TEST_OF_TEMPTATION,1);
//        deckList.put(AdventureDeckCards.ROBBER_KNIGHT,2);
//        deckList.put(AdventureDeckCards.SIR_GALAHAD,1);
//        deckList.put(AdventureDeckCards.SIR_LANCELOT,1);
//        deckList.put(AdventureDeckCards.KING_ARTHUR,1);
//        HashMap<CardTypes,Integer> totals = new HashMap<>();
//        totals.put(CardTypes.FOE,2);
//        totals.put(CardTypes.TEST,2);
//        totals.put(CardTypes.WEAPON,4);
//        totals.put(CardTypes.ALLY,3);
//
//        for(Map.Entry<AdventureDeckCards,Integer> e : deckList.entrySet()) {
//
//            //Create number of cards as proscribed in list
//            for(int i=0;i<e.getValue();++i)
//            {
//                AdventureCards card = cf.createCard(testDeck,e.getKey());
//                if(card==null) {
//                    LOG.error("Adventure Deck factory failure. Card created as null");
//                }
//                else {
//                    hand.receiveCard(card);
//                }
//            }
//        }
//        HashMap<CardTypes,HashMap<AllCardCodes,Integer>> result = hand.getNumberOfEachCardCodeBySubType();
//
//        for(Map.Entry<CardTypes,HashMap<AllCardCodes,Integer>> e : result.entrySet()) {
//            int total = 0;
//            for(Integer n : e.getValue().values()) {
//                total+=n;
//            }
//            assert(total==totals.get(e.getKey()));
//        }
//    }

    @Test
    void testGenerateHandData() throws JsonProcessingException {
        Hand hand = player.getHand();
        setupPlayerPlayAreasToPlayCards();
        LOG.info("Hand state before card received.");
//        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
//            aDeck.drawCard(hand);
//        }
        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
        LOG.info("JSON Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(player.getHand().generateHandData()));
    }

    @Test
    void testGenerateObsfucatedHandData() throws JsonProcessingException {
        Hand hand = player.getHand();
        LOG.info("Hand state before card received.");
//        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
//            aDeck.drawCard(hand);
//        }
        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
        LOG.info("JSON Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(player.getHand().generateObfuscatedHandData()));
    }

    //Cannot unit test since it communicates with other part of program
//    @Test
//    void notifyHandOversize() throws JsonProcessingException {
//        Hand hand = player.getHand();
//        LOG.info("Hand state before card received.");
//        for(int i=0;i<Hand.MAX_HAND_SIZE+5;++i) {
//            aDeck.drawCard(hand);
//            if(i>=Hand.MAX_HAND_SIZE) {
//                assert(hand.isHandOversize());
//            }
//            else {
//                aDeck.drawCard(hand);
//                assert(!hand.isHandOversize());
//            }
//        }
//        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
//    }

}