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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PlayerPlayAreasTest {

    StoryDecks sDeck;
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
        sDeck = game.getSDeck();
//        StoryCards sCard = sDeck.drawCard(testStage);
//        testStage = new TestPlayArea();
//        while(sCard.getSubType()!=CardTypes.QUEST) {
//            sCard.discardCard();
//            sCard = sDeck.drawCard(testStage);
//        }
        game.getAllowedStoryCardTypes().clear();
        game.getAllowedStoryCardTypes().add(CardTypes.QUEST);

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
        testPhaseController = (QuestPhaseController) game.getCurrPhase();
        for(int i=0;i<players.size();++i) {
            hands.add(players.get(i).getHand());
            pPlayAreas.add(players.get(i).getPlayArea());
        }
//        players.addAll(session.getPlayerMap().values());
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        //playAllNonDuplicateCardsFromHand();
    }

    void setupPlayerPlayAreasToPlayCards() {
        //Vanquish Arthur's enemies is the only quest that doesn't boost anyone
        CardFactory cf = CardFactory.getInstance();
        StoryCards storyCard = cf.createCard(aDeck,StoryDeckCards.VANQUISH_KING_ARTHURS_ENEMIES);
        for(Players p : players) {
            p.getPlayArea().registerGamePhase(testPhaseController);
            p.getPlayArea().onQuestStarted(storyCard);
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
    void discardAllCards() {
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(PlayerPlayAreas pa : pPlayAreas) {
            pa.discardAllCards();
            assert(pa.size()==0);
        }
    }

//    @Test
//    void discardAllFoes() {
//        for(PlayerPlayAreas pa : pPlayAreas) {
//            HashSet<AdventureCards> cards = pa.getCardTypeMap().get(CardTypes.FOE);
//            if(cards==null) {
//                break;
//            }
//            int numOfType = cards.size();
//            LOG.info("Card Area had "+numOfType+" Foes BEFORE discard");
//            pa.discardAllFoes();
//            assert(pa.getCardTypeMap().get(CardTypes.FOE).size()==0);
//        }
//
//    }


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


//    @Test
//    void discardAllTests() {
//        for(PlayerPlayAreas pa : pPlayAreas) {
//            HashSet<AdventureCards> cards = pa.getCardTypeMap().get(CardTypes.TEST);
//            if(cards==null) {
//                break;
//            }
//            int numOfType = cards.size();
//            LOG.info("Card Area had "+numOfType+" Foes BEFORE discard");
//            pa.discardAllTests();
//            assert(pa.getCardTypeMap().get(CardTypes.TEST).size()==0);
//        }
//    }


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
        CardFactory cf = CardFactory.getInstance();
        StoryCards storyCard = cf.createCard(aDeck,StoryDeckCards.VANQUISH_KING_ARTHURS_ENEMIES);
        pa.onQuestStarted(storyCard);
        pa.onStageChanged(testStage);
        pa.setPlayerTurn(true);
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
        assertThat(pa.size()+hand.getHandSize()).isEqualTo(cards.size());
        assertThat(pa.getBattlePoints()).isEqualTo(bp);
        assertThat(pa.getBids()).isEqualTo(bids);

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

    @Test
    void getPlayAreaData() throws JsonProcessingException {
        setupPlayerPlayAreasToPlayCards();
        int i=0;
        boolean isReset=false;
        for(Hand h : hands) {
            HashSet<AllCardCodes> uniqueCardCodes = new HashSet<>();
            for(CardData card : h.generateCardData()) {
                if(!uniqueCardCodes.contains(card.cardCode())) {
                    if(!isReset && i>1) { //first two cards will not be hidden
                        pPlayAreas.get(0).setPlayerTurn(false);
                        pPlayAreas.get(0).setPlayerTurn(true);
                        isReset=true;
                    }
                    if(h.playCard(card.cardID())) {
                        ++i;
                    }
                    uniqueCardCodes.add(card.cardCode());
                }
            }
        }
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(pPlayAreas.get(0).getObfuscatedPlayAreaData()));
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