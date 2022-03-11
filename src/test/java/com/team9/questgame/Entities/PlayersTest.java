//package com.team9.questgame.Entities;
//
//import com.team9.questgame.Entities.cards.AdventureCards;
//import com.team9.questgame.Entities.cards.AdventureDecks;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Stack;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest
//class PlayersTest {
//
//    AdventureDecks deck;
//
//    Players p1;
//
//    Logger LOG;
//
//    @BeforeEach
//    void setUp() {
//        p1 = new Players("Player 1");
//        LOG = LoggerFactory.getLogger(PlayersTest.class);
//        deck = new AdventureDecks();
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void getPlayerId() {
//    }
//
//    @Test
//    void getName() {
//    }
//
//    @Test
//    void setName() {
//    }
//
//    @Test
//    void getBattlePoints() {
//    }
//
//    @Test
//    void getShields() {
//    }
//
//    @Test
//    void isHandOversize() {
//    }
//
//    @Test
//    void onCardIssued() {
//    }
//
//    @Test
//    void onGameRest() {
//    }
//
//    @Test
//    void onEndOfPhase() {
//    }
//
//    @Test
//    void actionPlayCard() {
//    }
//
//    @Test
//    void actionDiscardCard() {
//    }
//
//    @Test
//    void actionEndTurn() {
//    }
//
//    @Test
//    void testReceiveCard() {
//        AdventureCards card=null;
//        for(int i=0;i<12;++i) {
//            card=(AdventureCards) deck.drawCard(p1);
//            assert(p1.getHandSize()==i+1);
//            assert(p1.isHandOversize()==false);
//            System.out.println("Draw Card: "+card);
//            System.out.println("Hand Size: "+p1.getHandSize());
//        }
//
//        card=(AdventureCards) deck.drawCard(p1);
//        assert(p1.isHandOversize()==true); //True
//    }
//
//    @Test
//    void testDiscardCard() {
//        Stack<AdventureCards> hand = new Stack<>();
//        for(int i=0;i<13;++i) {
//            hand.add(deck.drawCard(p1));
//        }
//
//        for(int i=12;i>=0;--i) {
//            p1.discardCard(hand.pop());
//            assert(p1.isHandOversize()==(p1.getHandSize()>p1.MAX_HAND_SIZE));
//            assert(p1.getHandSize()==i);
//        }
//    }
//
//    @Test
//    void testPlayCard() {
//
//    }
//
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
//}