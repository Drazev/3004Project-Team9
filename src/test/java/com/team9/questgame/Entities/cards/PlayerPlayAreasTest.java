package com.team9.questgame.Entities.cards;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.CardAreaException;
import com.team9.questgame.game_phases.GeneralGameController;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PlayerPlayAreasTest {

    AdventureDecks aDeck;
    @Autowired
    GeneralGameController game;
    @Autowired
    SessionService session;

    ArrayList<Players> players;
    ArrayList<Hand> hands;
    ArrayList<PlayerPlayAreas> pPlayAreas;

    QuestPhaseController testPhaseController;
    TestPlayArea testStage;


    Logger LOG;

    ObjectMapper objMap;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        hands = new ArrayList<>();
        pPlayAreas = new ArrayList<>();
        LOG = LoggerFactory.getLogger(PlayerPlayAreasTest.class);
        objMap = ApplicationContextHolder.getContext().getBean(ObjectMapper.class);
        aDeck = game.getADeck();
        testPhaseController = new QuestPhaseController();
        testStage = new TestPlayArea();
        session.registerPlayer("Player 1");
        session.registerPlayer("Player 2");
        session.registerPlayer("Player 3");
        session.registerPlayer("Player 4");
        players.addAll(session.getPlayerMap().values());
        for(Players p : players) {
            game.playerJoin(p);
        }
        game.startGame();
        game.drawStoryCard(players.get(0));
        for(int i=0;i<players.size();++i) {
            hands.add(players.get(i).getHand());
            pPlayAreas.add(players.get(i).getPlayArea());
        }
        players.addAll(session.getPlayerMap().values());
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        //playAllNonDuplicateCardsFromHand();
    }

    void setupPlayerPlayAreasToPlayCards() {
        for(Players p : players) {
            p.getPlayArea().registerGamePhase(testPhaseController);
            p.getPlayArea().onPlayAreaChanged(testStage);
            p.getPlayArea().onPhaseNextPlayerTurn(p);
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
    void discardAllCards() {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(PlayerPlayAreas pa : pPlayAreas) {
            pa.discardAllCards();
            assert(pa.size()==0);
        }
    }

    @Test
    void discardAllAllies() {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(PlayerPlayAreas pa : pPlayAreas) {
            HashSet<AdventureCards> cards = pa.getCardTypeMap().get(CardTypes.ALLY);
            if(cards==null) {
                break;
            }
            int numOfType = cards.size();
            LOG.info("Card Area had "+numOfType+" Foes BEFORE discard");
            pa.discardAllAllies();
            assert(pa.getCardTypeMap().get(CardTypes.ALLY).size()==0);
        }
    }

    @Test
    void discardAllWeapons() {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(PlayerPlayAreas pa : pPlayAreas) {
            HashSet<AdventureCards> cards = pa.getCardTypeMap().get(CardTypes.WEAPON);
            if(cards==null) {
                break;
            }
            int numOfType = cards.size();
            LOG.info("Card Area had "+numOfType+" Foes BEFORE discard");
            pa.discardAllWeapons();
            assert(pa.getCardTypeMap().get(CardTypes.WEAPON).size()==0);
        }
    }

    @Test
    void discardAllAmour() {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(PlayerPlayAreas pa : pPlayAreas) {
            HashSet<AdventureCards> cards = pa.getCardTypeMap().get(CardTypes.AMOUR);
            if(cards==null) {
                break;
            }
            int numOfType = cards.size();
            LOG.info("Card Area had "+numOfType+" Foes BEFORE discard");
            pa.discardAllAmour();
            assert(pa.getCardTypeMap().get(CardTypes.AMOUR).size()==0);
        }
    }

    @Test
    void receiveCard() throws JsonProcessingException {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(Players p : players) {
            LOG.info(p.getName()+" CARDS IN PLAY:\n"+objMap.writerWithDefaultPrettyPrinter().writeValueAsString(p.getPlayArea()));
            assert(p.getPlayArea().size()>0);
        }
    }

    @Test
    void discardCard() {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(PlayerPlayAreas pa : pPlayAreas) {
            int start=pa.size();
            int numDiscarded=0;
            HashSet<AdventureCards> cards = new HashSet<>();
            for(HashSet<AdventureCards> cardTypeSet: pa.getCardTypeMap().values()) {
                cards.addAll(cardTypeSet);
            }
            for(AdventureCards card : cards) {
                pa.discardCard(card);
                ++numDiscarded;
            }
            assert(pa.size()==0);
            assert(start==numDiscarded);
        }
    }

    @Test
    void playCard() {

        //TODO: Once Phase CardArea's are done
    }

    @Test
    void testBattlePointAndBidCalculation() {
        Players player = players.get(0);
        Hand hand = player.getHand();
        PlayerPlayAreas pa = player.getPlayArea();
        player.onGameReset();
        pa.registerGamePhase(testPhaseController);
        pa.onPlayAreaChanged(testStage);
        pa.onPhaseNextPlayerTurn(player);
        CardFactory cf = CardFactory.getInstance();
        AdventureDecks testDeck = new AdventureDecks();
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
        HashSet<AdventureCards> cards = new HashSet<>();
        deckList.put(AdventureDeckCards.QUEEN_ISEULT,1); //Boosted 4 bids
        deckList.put(AdventureDeckCards.SIR_TRISTAN,1); //Boosted 20 bp
        deckList.put(AdventureDeckCards.AMOUR,1); // 10 bp 1 bid
        deckList.put(AdventureDeckCards.QUEEN_GUINEVERE,1); //3 bids
        deckList.put(AdventureDeckCards.SIR_GALAHAD,1); // 15 bp
        deckList.put(AdventureDeckCards.HORSE,1); //10 bp, only one counted
        deckList.put(AdventureDeckCards.LANCE,1); //20 bp
        deckList.put(AdventureDeckCards.TEST_OF_THE_QUESTING_BEAST,1); // rejected
        deckList.put(AdventureDeckCards.DRAGON,1); // rejected

        final int bp =  20+10+15+10+20+players.get(0).getRank().getRankBattlePointValue();
        final int bids = 4+1+3;
        LOG.info("Building custom deck for test");
        for(Map.Entry<AdventureDeckCards,Integer> e : deckList.entrySet()) {

            //Create number of cards as proscribed in list
            for(int i=0;i<e.getValue();++i)
            {
                AdventureCards card = cf.createCard(testDeck,e.getKey());
                if(card==null) {
                    LOG.error("Adventure Deck factory failure. Card created as null");
                }
                else {
                    cards.add(card);
                    hand.receiveCard(card);
                }
            }
        }

        LOG.info("Playing cards from hand into PlayerPlayArea. Checking that duplicates create correct error");
        HashSet<AllCardCodes> uniqueCardCodes = new HashSet<>();
        for(AdventureCards c : cards) {

                if(!uniqueCardCodes.contains(c.getCardCode())) {
                    if(hand.playCard(c)) {
                        uniqueCardCodes.add(c.getCardCode());
                    }
                    else {
                        assert(c.getSubType()==CardTypes.FOE || c.getSubType()==CardTypes.TEST);
                        LOG.info("PLAYCARD: "+c.getCardCode()+" REJECTED because it's a Foe or Test and was not accepted by Phase");
                    }

                }
                else {
                    assertThrows(CardAreaException.class,()->
                            hand.playCard(c)
                    );
                }
        }

        LOG.info("Checking the play area attributes are correctly calculated.");
        assert(pa.size()+hand.getHandSize()==cards.size());
        assert(pa.getBattlePoints()==bp);
        assert(pa.getBids()==bids);

        LOG.info("Checking if Boost status on cards was correct");
        for(AdventureCards c : cards) {
            if(c.getCardCode()==AdventureDeckCards.SIR_TRISTAN) {
                AllyCards ally = (AllyCards) c;
                assert(ally.isBoosted());
                assert(ally.getBattlePoints()==20);
                LOG.info(AdventureDeckCards.SIR_TRISTAN+" is BOOSTED!");
            }
            else if(c.getCardCode()==AdventureDeckCards.QUEEN_ISEULT) {
                AllyCards ally = (AllyCards) c;
                assert(ally.isBoosted());
                assert(ally.getBids()==4);
                LOG.info(AdventureDeckCards.QUEEN_ISEULT+" is BOOSTED!");
            }
        }

    }

    @Test
    void getPlayAreaData() throws JsonProcessingException {
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(pPlayAreas.get(0).getPlayAreaData()));
    }

    @Test
    void activateCard() {
    }

    @Test
    void registerGamePhase() {
    }

    @Test
    void onGamePhaseEnded() {
    }

    @Test
    void onPlayAreaChanged() {
    }
}