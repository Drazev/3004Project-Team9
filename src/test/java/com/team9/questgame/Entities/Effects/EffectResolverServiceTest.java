package com.team9.questgame.Entities.Effects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Entities.PlayerRanks;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.*;
import com.team9.questgame.game_phases.GeneralGameController;
import com.team9.questgame.game_phases.GeneralStateE;
import com.team9.questgame.game_phases.quest.QuestPhaseController;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EffectResolverServiceTest {

    @Autowired
    GeneralGameController game;
    @Autowired
    EffectResolverService effectResolverService;
    AdventureDecks aDeck;
    StoryDecks sDeck;

    ArrayList<Players> players;
    ArrayList<Hand> hands;
    ArrayList<PlayerPlayAreas> pPlayAreas;

    QuestPhaseController testPhaseController;
    TestPlayArea testStage;

    Logger LOG;

    ObjectMapper objMap;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        players = new ArrayList<>();
        hands = new ArrayList<>();
        pPlayAreas = new ArrayList<>();
        LOG = LoggerFactory.getLogger(EffectResolverServiceTest.class);
        objMap = ApplicationContextHolder.getContext().getBean(ObjectMapper.class);
        aDeck = game.getADeck();
        sDeck = game.getSDeck();
        testPhaseController = new QuestPhaseController();
        testStage = new TestPlayArea();
        players.add(new Players("Player 1"));
        players.add(new Players("Player 2"));
        players.add(new Players("Player 3"));
        players.add(new Players("Player 4"));
        for(Players p : players) {
            game.playerJoin(p);
        }
        game.startGame();
        assertThat(game.getStateMachine().getCurrentState()).isEqualTo(GeneralStateE.DRAW_STORY_CARD);
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        for(int i=0;i<players.size();++i) {
            hands.add(players.get(i).getHand());
            pPlayAreas.add(players.get(i).getPlayArea());
        }

        //setupPlayerPlayAreasToPlayCards();
        //playAllNonDuplicateCardsFromHand();
        for(Players p : players) {
            LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(p));
        }

//        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
//            for(Players p:players) {
//                aDeck.drawCard(p.getHand());
//            }
//        }

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
    void loadTargetSelector() {
        LOG.info("success");
    }

    @Test
    void playerAwardedShields() throws JsonProcessingException {
        Players p1 = players.get(0);
        HashMap<Players,Integer> temp = new HashMap<>();
        temp.put(p1,4);
        effectResolverService.playerAwardedShields(temp);
        assert(p1.getShields()==4);
        assert(p1.getRank()== PlayerRanks.SQUIRE);
        effectResolverService.playerAwardedShields(temp); //shields = 8-5 for rank - 3,  rankup to KNIGHT
        assert(p1.getShields()==3);
        assert(p1.getRank()==PlayerRanks.KNIGHT);
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(p1));

    }

    @Test
    void playerLoosesShieldsHashMap() throws JsonProcessingException {
        Players p1 = players.get(0);
        HashMap<Players,Integer> temp = new HashMap<>();
        temp.put(p1,4);
        effectResolverService.playerAwardedShields(temp);
        assert(p1.getShields()==4);
        assert(p1.getRank()== PlayerRanks.SQUIRE);
        effectResolverService.playerAwardedShields(temp); //shields = 8-5 for rank - 3,  rankup to KNIGHT
        assert(p1.getShields()==3);
        assert(p1.getRank()==PlayerRanks.KNIGHT);
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(p1));

        HashMap<Players,Boolean> results = effectResolverService.playerLoosesShieldsHashMap(temp); //3-4=0 shields, rank still KNIGHT
        assert(p1.getShields()==0);
        assert(p1.getRank()==PlayerRanks.KNIGHT);
        assert(results.get(p1)); //True because at least one shield was removed
        effectResolverService.playerLoosesShieldsHashMap(temp); //Fails because no shields to remove
        assert(p1.getShields()==0);
        assert(p1.getRank()==PlayerRanks.KNIGHT);
        assert(results.get(p1)); //False because at least one shield could NOT  be removed
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(p1));
    }

    @Test
    void forcePlayerDiscards() {

    }

    @Test
    void drawAdventureCards() throws JsonProcessingException {
        int startSize = players.get(0).getHand().getHandSize();
        HashMap<Players,Integer> drawList = new HashMap<>();
        drawList.put(players.get(0),2);
        drawList.put(players.get(1),0);
        game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        GeneralStateE currentState = game.getStateMachine().getCurrentState();
        assert(currentState == GeneralStateE.QUEST_PHASE
                || currentState == GeneralStateE.TOURNAMENT_PHASE
                || currentState == GeneralStateE.EVENT_PHASE);
        effectResolverService.drawAdventureCards(drawList);
        assert(players.get(0).getHand().isHandOversize()); //Should be oversized
        assert(players.get(0).getHand().getHandSize()==14); //12+2=14 cards
        assert(!players.get(1).getHand().isHandOversize()); //Not oversize because draw 0
        assert(players.get(1).getHand().getHandSize()==12); //started with 12
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(players.get(0)));
        LOG.info(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(players.get(1)));

    }

    @Test
    void playerDiscardsAllCardsFromPlay() {
        //game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
        deckList.put(AdventureDeckCards.EXCALIBUR,2);
        deckList.put(AdventureDeckCards.LANCE,6);
        deckList.put(AdventureDeckCards.BATTLE_AX,8);
        deckList.put(AdventureDeckCards.SWORD,16);
        deckList.put(AdventureDeckCards.HORSE,11);
        deckList.put(AdventureDeckCards.DAGGER,6);

        //Foes
        deckList.put(AdventureDeckCards.DRAGON,1);
        deckList.put(AdventureDeckCards.GIANT,2);
        deckList.put(AdventureDeckCards.MORDRED,4);
        deckList.put(AdventureDeckCards.GREEN_KNIGHT,2);
        deckList.put(AdventureDeckCards.BLACK_KNIGHT,3);
        deckList.put(AdventureDeckCards.EVIL_KNIGHT,6);
        deckList.put(AdventureDeckCards.SAXON_KNIGHT,8);
        deckList.put(AdventureDeckCards.ROBBER_KNIGHT,7);
        deckList.put(AdventureDeckCards.SAXONS,5);
        deckList.put(AdventureDeckCards.BOAR,4);
        deckList.put(AdventureDeckCards.THIEVES,8);

        //Allies
        deckList.put(AdventureDeckCards.SIR_GALAHAD,1);
        deckList.put(AdventureDeckCards.SIR_LANCELOT,1);
        deckList.put(AdventureDeckCards.KING_ARTHUR,1);
        deckList.put(AdventureDeckCards.SIR_TRISTAN,1);
        deckList.put(AdventureDeckCards.KING_PELLINORE,1);
        deckList.put(AdventureDeckCards.SIR_GAWAIN,1);
        deckList.put(AdventureDeckCards.SIR_PERCIVAL,1);
        deckList.put(AdventureDeckCards.QUEEN_GUINEVERE,1);
        deckList.put(AdventureDeckCards.QUEEN_ISEULT,1);
        deckList.put(AdventureDeckCards.MERLIN,1);
        aDeck.testRebuildDeckWithList(deckList);
        for(Players p : players) {
            p.onGameReset();
        }
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            for(Players p : players) {
                aDeck.drawCard(p.getHand());
            }
        }
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(Players p : players) {
            assert(p.getPlayArea().getCardTypeMap().size()>0);
        }
        HashSet<CardTypes> test1 = new HashSet<>();
        HashSet<CardTypes> test2 = new HashSet<>();
        test2.add(CardTypes.ALLY);
        test2.add(CardTypes.WEAPON);
        test1.add(CardTypes.WEAPON);
        test1.add(CardTypes.AMOUR);
        HashSet<Players> pTest = new HashSet<>();
        pTest.add(players.get(0));
        pTest.add(players.get(1));
        pTest.add(players.get(2));
        HashMap<Players,HashMap<CardTypes,HashSet<AdventureCards>>> map = new HashMap<>();

        for(Players p : players) {
            HashMap<CardTypes,HashSet<AdventureCards>> toStore = new HashMap<>();
            HashMap<CardTypes,HashSet<AdventureCards>> temp = p.getPlayArea().getCardTypeMap();
            for(Map.Entry<CardTypes,HashSet<AdventureCards>> e : temp.entrySet()) {
                HashSet<AdventureCards> tmp2 = new HashSet<>(e.getValue());
                toStore.put(e.getKey(),tmp2);
            }
            map.put(p,toStore);
        }
        HashMap<Players,Boolean> t1Result = effectResolverService.playerDiscardsAllCardsFromPlay(pTest,test1);
        for(Map.Entry<Players,Boolean> e : t1Result.entrySet()) {
            HashMap<CardTypes,HashSet<AdventureCards>> ctm = e.getKey().getPlayArea().getCardTypeMap();
            boolean cardsDiscarded = false;
            for(CardTypes ct : test1) {
                //If there were cards of this type before the test, assert there are none now
                HashMap<CardTypes,HashSet<AdventureCards>> cardTypeMap = map.get(e.getKey());
                HashSet<AdventureCards> cardsOfType = cardTypeMap.get(ct);
                if(cardsOfType!=null && cardsOfType.size()>0) {
                    assert(ctm.get(ct).size()==0);
                    cardsDiscarded=true;
                }
            }
            //Make sure the return boolean is true if cards were discarded for any type in the list
            assert(cardsDiscarded==e.getValue());
        }

        //test 2
        map.clear();
        for(Players p : players) {
            HashMap<CardTypes,HashSet<AdventureCards>> toStore = new HashMap<>();
            HashMap<CardTypes,HashSet<AdventureCards>> temp = p.getPlayArea().getCardTypeMap();
            for(Map.Entry<CardTypes,HashSet<AdventureCards>> e : temp.entrySet()) {
                HashSet<AdventureCards> tmp2 = new HashSet<>(e.getValue());
                toStore.put(e.getKey(),tmp2);
            }
            map.put(p,toStore);
        }

        HashMap<Players,Boolean> t2Result = effectResolverService.playerDiscardsAllCardsFromPlay(pTest,test2);
        for(Map.Entry<Players,Boolean> e : t2Result.entrySet()) {
            HashMap<CardTypes,HashSet<AdventureCards>> ctm = e.getKey().getPlayArea().getCardTypeMap();
            boolean cardsDiscarded = false;
            for(CardTypes ct : test2) {
                //If there were cards of this type before the test, assert there are none now
                HashMap<CardTypes,HashSet<AdventureCards>> cardTypeMap = map.get(e.getKey());
                HashSet<AdventureCards> cardsOfType = cardTypeMap.get(ct);
                if(cardsOfType!=null && cardsOfType.size()>0) {
                    assert(ctm.get(ct).size()==0);
                    cardsDiscarded=true;
                }
            }
            //Make sure the return boolean is true if cards were discarded for any type in the list
            assert(cardsDiscarded==e.getValue());
        }
    }

    @Test
    void registerEffectTriggeredOnQuestCompleted() {

    }

    @Test
    void unregisterEffectTriggeredOnQuestCompleted() {
    }

    @Test
    void onQuestCompleted() {
    }

    @Test
    void testChivalrousDeedCard() {
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.CHIVALROUS_DEED);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),12);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.activate(players.get(0));;
        assert(players.get(3).getShields()==3);
        card.activate(players.get(1));
        assert(players.get(2).getShields()==0);
        assert(players.get(2).getRank()==PlayerRanks.KNIGHT);
    }

    @Test
    void testQueensFavorCard() {
        game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        GeneralStateE currentState = game.getStateMachine().getCurrentState();
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.QUEENS_FAVOR);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),12);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.activate(players.get(0));;
        assert(players.get(2).getHand().getHandSize()==14); //Was squire
        assert(players.get(3).getHand().getHandSize()==14); //Was squire
    }

    @Test
    void testPoxCard() {
        game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        GeneralStateE currentState = game.getStateMachine().getCurrentState();
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.POX);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),15);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        shieldRewards.put(players.get(3),4);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.activate(players.get(3)); //Player 4 drew card
        assert(players.get(0).getShields()==0);
        assert(players.get(1).getShields()==2);
        assert(players.get(2).getShields()==1);
        assert(players.get(3).getShields()==4); //This should stay same since drawing player is immune
    }

    @Test
    void testPlagueCard() {
        game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        GeneralStateE currentState = game.getStateMachine().getCurrentState();
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.PLAGUE);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),6);//Knight
        shieldRewards.put(players.get(1),15);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        shieldRewards.put(players.get(3),4);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.activate(players.get(3));
        assert(players.get(3).getShields()==2);
        card.activate(players.get(2));
        assert(players.get(2).getShields()==0);
        assert(players.get(0).getShields()==1);
        assert(players.get(1).getShields()==3);
    }

    @Test
    void testProsperityThroughtTheRelmCard() {
        game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        GeneralStateE currentState = game.getStateMachine().getCurrentState();
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.PROSPERITY_THROUGHOUT_THE_REALM);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),12);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.activate(players.get(0));;
        assert(players.get(0).getHand().getHandSize()==14); //Was squire
        assert(players.get(1).getHand().getHandSize()==14); //Was squire
        assert(players.get(2).getHand().getHandSize()==14); //Was squire
        assert(players.get(3).getHand().getHandSize()==14); //Was squire
    }

    @Test
    void testCourtCalledToCamelot() {
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.COURT_CALLED_TO_CAMELOT);
        deckList.put(AdventureDeckCards.EXCALIBUR,2);
        deckList.put(AdventureDeckCards.LANCE,6);
        deckList.put(AdventureDeckCards.BATTLE_AX,8);
        deckList.put(AdventureDeckCards.SWORD,16);
        deckList.put(AdventureDeckCards.HORSE,11);
        deckList.put(AdventureDeckCards.DAGGER,6);
        //Allies
        deckList.put(AdventureDeckCards.SIR_GALAHAD,5);
        deckList.put(AdventureDeckCards.SIR_LANCELOT,5);
        deckList.put(AdventureDeckCards.KING_ARTHUR,5);
        deckList.put(AdventureDeckCards.SIR_TRISTAN,5);
        deckList.put(AdventureDeckCards.KING_PELLINORE,5);
        deckList.put(AdventureDeckCards.SIR_GAWAIN,5);
        deckList.put(AdventureDeckCards.SIR_PERCIVAL,5);
        deckList.put(AdventureDeckCards.QUEEN_GUINEVERE,5);
        deckList.put(AdventureDeckCards.QUEEN_ISEULT,5);
        deckList.put(AdventureDeckCards.MERLIN,5);
        aDeck.testRebuildDeckWithList(deckList);
        for(Players p : players) {
            p.onGameReset();
        }
        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            for(Players p : players) {
                aDeck.drawCard(p.getHand());
            }
        }
        setupPlayerPlayAreasToPlayCards();
        playAllNonDuplicateCardsFromHand();
        for(Players p : players) {
            assert(p.getPlayArea().getCardTypeMap().size()>0);
        }
        HashSet<CardTypes> test1 = new HashSet<>();
        HashSet<CardTypes> test2 = new HashSet<>();
        test2.add(CardTypes.ALLY);
        test2.add(CardTypes.WEAPON);
        test1.add(CardTypes.WEAPON);
        test1.add(CardTypes.AMOUR);
        HashSet<Players> pTest = new HashSet<>();
        pTest.add(players.get(0));
        pTest.add(players.get(1));
        pTest.add(players.get(2));
        HashMap<Players,Integer> map = new HashMap<>();

       for(Players p : players) {
           HashSet<AdventureCards> list = p.getPlayArea().getCardTypeMap().get(CardTypes.ALLY);
           if(list!=null) {
               map.put(p,list.size());
           }
       }

       card.activate(players.get(0));
        for(Players p : players) {
            HashSet<AdventureCards> list = p.getPlayArea().getCardTypeMap().get(CardTypes.ALLY);
            if(list!=null && map.get(p)>0) {
                assert(list.size()==0);
            }
            else {
                assert(map.get(p)>0);
            }
        }
    }

    @Test
    void testKingsRecognitionCard() {
        game.drawStoryCard(game.getPlayerTurnService().getPlayerTurn());
        CardFactory cf = CardFactory.getInstance();
        CardWithEffect card = (EventCards)cf.createCard(sDeck,StoryDeckCards.KINGS_RECOGNITION);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),6);//Knight
        shieldRewards.put(players.get(1),15);//Champion Knight
        shieldRewards.put(players.get(2),4);//squire, but now lowest
        EffectResolverService.getService().playerAwardedShields(shieldRewards);
        HashMap<Players,Integer> playersCompletedQuest = new HashMap<>();
        playersCompletedQuest.put(players.get(0),0);
        playersCompletedQuest.put(players.get(2),0);
        card.activate(players.get(1));
        assert(players.get(0).getShields()==1);
        assert(players.get(1).getShields()==3);
        assert(players.get(2).getShields()==4);
        assert(players.get(3).getShields()==0);
        EffectResolverService.getService().onQuestCompleted(playersCompletedQuest);
        assert(players.get(0).getShields()==3); //+2 shields
        assert(players.get(1).getShields()==3);
        assert(players.get(2).getShields()==1); //+2 shields, rank up\
        assert(players.get(2).getRank()==PlayerRanks.KNIGHT);
        assert(players.get(3).getShields()==0);
    }
}