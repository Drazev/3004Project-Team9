package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.BadRequestException;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HandTest {


    AdventureDecks aDeck;

    @JsonBackReference
    Players player;

    Logger LOG;

    ObjectMapper objMap;

    @BeforeEach
    void setUp() {
        LOG = LoggerFactory.getLogger(PlayerPlayAreasTest.class);
        objMap = ApplicationContextHolder.getContext().getBean(ObjectMapper.class);
        aDeck = new AdventureDecks();
        player= new Players("Player 1");
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    @Test
    void receiveCard() throws JsonProcessingException {
        Hand hand = player.getHand();
        LOG.info("Hand state before card received.");
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            aDeck.drawCard(hand);
        }
        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
    }

    @Test
    void discardCard() throws JsonProcessingException {
        Hand hand =  player.getHand();

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
        PlayerPlayAreas area = player.getPlayArea();
        Hand hand = player.getHand();
        QuestPhaseController phase = new QuestPhaseController();
        TestPlayArea stageStandIn = new TestPlayArea();
        area.registerGamePhase(phase);
//        area.onPlayAreaChanged(stageStandIn); TODO: Enable this
        area.onPhaseNextPlayerTurn(player);
        LOG.info("Hand state before card received.");
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            aDeck.drawCard(hand);
        }
        LOG.info("Hand Printout: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));

        HashSet<AllCardCodes> uniqueCardCodes = new HashSet<>();

        for(CardData card : hand.generateCardData()) {
            LOG.info("PLAY CARD: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(card));
            if(!uniqueCardCodes.contains(card.cardCode())) {
                if(hand.playCard(card.cardID()))
                {
                    uniqueCardCodes.add(card.cardCode());
                }
                else {
                    assert(card.subType()==CardTypes.FOE || card.subType()==CardTypes.TEST);
                    LOG.info("PLAYCARD: "+card.cardCode()+" REJECTED because it's a Foe or Test and was not accpted by Phase");
                }
            }
            else {
                LOG.info("CARD WAS DUPLICATE - NOT PLAYED");
            }
        }

        assert(hand.generateCardData().size()== (Hand.MAX_HAND_SIZE-uniqueCardCodes.size()));
        LOG.info("Hand Printout: \n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
        assertThrows(BadRequestException.class,()->
                hand.playCard(141098754)
        );

    }

    @Test
    void testGetUniqueCardsCodesBySubType() {
        Hand hand = player.getHand();
        CardFactory cf = CardFactory.getInstance();
        AdventureDecks testDeck = new AdventureDecks();
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
        deckList.put(AdventureDeckCards.EXCALIBUR,2);
        deckList.put(AdventureDeckCards.LANCE,2);
        deckList.put(AdventureDeckCards.TEST_OF_VALOR,1);
        deckList.put(AdventureDeckCards.TEST_OF_TEMPTATION,1);
        deckList.put(AdventureDeckCards.ROBBER_KNIGHT,2);
        deckList.put(AdventureDeckCards.SIR_GALAHAD,1);
        deckList.put(AdventureDeckCards.SIR_LANCELOT,1);
        deckList.put(AdventureDeckCards.KING_ARTHUR,1);
        HashMap<CardTypes,Integer> totals = new HashMap<>();
        totals.put(CardTypes.FOE,2);
        totals.put(CardTypes.TEST,2);
        totals.put(CardTypes.WEAPON,4);
        totals.put(CardTypes.ALLY,3);

        for(Map.Entry<AdventureDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for(int i=0;i<e.getValue();++i)
            {
                AdventureCards card = cf.createCard(testDeck,e.getKey());
                if(card==null) {
                    LOG.error("Adventure Deck factory failure. Card created as null");
                }
                else {
                    hand.receiveCard(card);
                }
            }
        }
        HashMap<CardTypes,HashMap<AllCardCodes,Integer>> result = hand.getNumberOfEachCardCodeBySubType();

        for(Map.Entry<CardTypes,HashMap<AllCardCodes,Integer>> e : result.entrySet()) {
            int total = 0;
            for(Integer n : e.getValue().values()) {
                total+=n;
            }
            assert(total==totals.get(e.getKey()));
        }
    }

    @Test
    void testGenerateHandData() throws JsonProcessingException {
        Hand hand = player.getHand();
        LOG.info("Hand state before card received.");
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            aDeck.drawCard(hand);
        }
        LOG.info("Hand Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(hand));
        LOG.info("JSON Printout: "+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(player.getHand().generateHandData()));
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