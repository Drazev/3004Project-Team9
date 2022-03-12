package com.team9.questgame.Entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Entities.cards.AdventureCards;
import com.team9.questgame.Entities.cards.AdventureDecks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Stack;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PlayersTest {

    AdventureDecks deck;

    Players p1;

    Logger LOG;

    ObjectMapper objMap;

    @BeforeEach
    void setUp() {
        p1 = new Players("Player 1");
        LOG = LoggerFactory.getLogger(PlayersTest.class);
        deck = new AdventureDecks();
        objMap = ApplicationContextHolder.getContext().getBean(ObjectMapper.class);
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getPlayerId() {
    }

    @Test
    void getName() {
    }

    @Test
    void setName() {
    }

    @Test
    void getBattlePoints() {
    }

    @Test
    void getShields() {
    }

    @Test
    void isHandOversize() {
    }

    @Test
    void onCardIssued() {
    }

    @Test
    void onGameRest() {
    }

    @Test
    void onEndOfPhase() {
    }

    @Test
    void actionPlayCard() {
    }

    @Test
    void actionDiscardCard() {
    }

    @Test
    void actionEndTurn() {
    }


    @Test
    void testPlayCard() {

    }
    @Test
    void testGeneratePlayerData() throws JsonProcessingException {
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString((p1.generatePlayerData())));
    }

//    @Test
//    void TestOnGameReset() {
//        Stack<AdventureCards> hand = new Stack<>();
//        for(int i=0;i<13;++i) {
//            hand.add(deck.drawCard(p1));
//        }
//        p1.onGameReset();
//
//        assert(p1.getShields()==0);
//    }
}