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
import com.team9.questgame.game_phases.event.EventPhaseController;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EffectResolverServiceTest {

    @Autowired
    GeneralGameController game;

    EventPhaseController ePhase;

    @Autowired
    SessionService session;
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
//        StoryCards sCard = sDeck.drawCard(testStage);
//        while(sCard.getSubType()!=CardTypes.QUEST) {
//            sCard.discardCard();
//            sCard = sDeck.drawCard(testStage);
//        }
        ePhase = null;
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
        testPhaseController = new QuestPhaseController(game,(QuestCards) CardFactory.getInstance().createCard(sDeck,StoryDeckCards.BOAR_HUNT));
//        testPhaseController = new QuestPhaseController(game,(QuestCards) sCard);
        testStage = new TestPlayArea();
        for(int i=0;i<players.size();++i) {
            hands.add(players.get(i).getHand());
            pPlayAreas.add(players.get(i).getPlayArea());
        }
        objMap.setVisibility(objMap.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

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
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.CHIVALROUS_DEED);
        ePhase = new EventPhaseController(game,card);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),12);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        ePhase = new EventPhaseController(game,card);
        card.playCard(ePhase);
        ePhase.startPhase(game.getPlayerTurnService());
        assert(players.get(3).getShields()==3);
        card.activate(ePhase,players.get(1));
        assert(players.get(2).getShields()==0);
        assert(players.get(2).getRank()==PlayerRanks.KNIGHT);
    }

    @Test
    void testQueensFavorCard() {
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.QUEENS_FAVOR);
        ePhase = new EventPhaseController(game,card);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),12);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.playCard(ePhase);
        ePhase.startPhase(game.getPlayerTurnService());
        assert(players.get(2).getHand().getHandSize()==14); //Was squire
        assert(players.get(3).getHand().getHandSize()==14); //Was squire
    }

    @Test
    void testPoxCard() {
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.POX);
        ePhase = new EventPhaseController(game,card);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),15);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        shieldRewards.put(players.get(3),4);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(3));
        ePhase.startPhase(game.getPlayerTurnService());
        assert(players.get(0).getShields()==0);
        assert(players.get(1).getShields()==2);
        assert(players.get(2).getShields()==1);
        assert(players.get(3).getShields()==4); //This should stay same since drawing player is immune
    }

    @Test
    void testPlagueCard() {
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.PLAGUE);
        ePhase = new EventPhaseController(game,card);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),6);//Knight
        shieldRewards.put(players.get(1),15);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        shieldRewards.put(players.get(3),4);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(3));
        ePhase.startPhase(game.getPlayerTurnService());
        assert(players.get(3).getShields()==2);
        ePhase.onGameReset();
        ePhase = new EventPhaseController(game,card);
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(2));
        ePhase.startPhase(game.getPlayerTurnService());
        assert(players.get(2).getShields()==0);
        assert(players.get(0).getShields()==1);
        assert(players.get(1).getShields()==3);
    }

    @Test
    void testProsperityThroughtTheRelmCard() {
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.PROSPERITY_THROUGHOUT_THE_REALM);
        ePhase = new EventPhaseController(game,card);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),5);//Knight
        shieldRewards.put(players.get(1),12);//Champion Knight
        shieldRewards.put(players.get(2),2);//squire, but now lowest
        effectResolverService.playerAwardedShields(shieldRewards);
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(3));
        ePhase.startPhase(game.getPlayerTurnService());
        assert(players.get(0).getHand().getHandSize()==14); //Was squire
        assert(players.get(1).getHand().getHandSize()==14); //Was squire
        assert(players.get(2).getHand().getHandSize()==14); //Was squire
        assert(players.get(3).getHand().getHandSize()==14); //Was squire
    }

    @Test
    void testCourtCalledToCamelot() {
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.COURT_CALLED_TO_CAMELOT);
        ePhase = new EventPhaseController(game,card);
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
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(0));
        ePhase.startPhase(game.getPlayerTurnService());
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
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.KINGS_RECOGNITION);
        ePhase = new EventPhaseController(game,card);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();
        shieldRewards.put(players.get(0),6);//Knight
        shieldRewards.put(players.get(1),15);//Champion Knight
        shieldRewards.put(players.get(2),4);//squire, but now lowest
        EffectResolverService.getService().playerAwardedShields(shieldRewards);
        HashMap<Players,Integer> playersCompletedQuest = new HashMap<>();
        playersCompletedQuest.put(players.get(0),0);
        playersCompletedQuest.put(players.get(2),0);
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(1));
        ePhase.startPhase(game.getPlayerTurnService());
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

    @Test
    void testKingsCallToArmsCard() {
        HashMap<AdventureDeckCards,Integer> deckList = new HashMap<>();
        CardFactory cf = CardFactory.getInstance();
        EventCards card = (EventCards)cf.createCard(sDeck,StoryDeckCards.KINGS_CALL_TO_ARMS);
        ePhase = new EventPhaseController(game,card);
        HashMap<CardTypes,Integer> p1TestVals = new HashMap<>();
        HashMap<CardTypes,Integer> p2TestVals = new HashMap<>();
        HashMap<CardTypes,Integer> p3TestVals = new HashMap<>();
        HashMap<CardTypes,Integer> p4TestVals = new HashMap<>();
        aDeck.testRebuildDeckWithList(deckList);
        TestPlayArea test = new TestPlayArea();
        for(Players p : players) {
            p.onGameReset();
            p.getPlayArea().registerGamePhase(ePhase);
            p.getPlayArea().setPlayerTurn(true);
        }
        aDeck.onGameReset();
        aDeck.createDeck();


        for(int i=0;i<Hand.MAX_HAND_SIZE;++i) {
            for(Players p : players) {
                aDeck.drawCard(p.getHand());
            }
        }
        //Player 1 has neither Foes or Weapons in hand
        for(CardData cd : players.get(0).getHand().generateCardData()) {
            switch(cd.subType()) {
                case FOE,WEAPON -> players.get(0).actionDiscardCard(cd.cardID());
            }
        }
        p1TestVals.put(CardTypes.FOE,0);
        p1TestVals.put(CardTypes.WEAPON,0);

        //Player 2 has both Foes and Weapons in hand
        HashMap<CardTypes,HashMap<AllCardCodes<AdventureDeckCards>,Integer>> p2List = players.get(1).getHand().getNumberOfEachCardCodeBySubType();
        HashMap<AllCardCodes<AdventureDeckCards>,Integer> p2Weapons = p2List.get(CardTypes.WEAPON);
        HashMap<AllCardCodes<AdventureDeckCards>,Integer> p2Foes = p2List.get(CardTypes.FOE);
        int p2W=0;
        int p2F=0;
        if(p2Weapons!=null) {
            p2W=p2Weapons.size();
        }
        if(p2Foes!=null) {
            p2F=p2Foes.size();
        }
        while(p2W<1 && p2F<2) {
            ArrayList<CardData> p2Cd = players.get(1).getHand().generateCardData();
            for(int i=0;i<p2Cd.size();++i) {
                if(p2Cd.get(i).subType()!=CardTypes.WEAPON || p2Cd.get(i).subType()!=CardTypes.FOE) {
                    players.get(1).getHand().discardCard(p2Cd.get(i).cardID());
                    break;
                }
                else if(p2Cd.get(i).subType()!=CardTypes.WEAPON && p2W>1) {
                    players.get(1).getHand().discardCard(p2Cd.get(i).cardID());
                    --p2W;
                    break;
                }
                else if(p2Cd.get(i).subType()!=CardTypes.FOE && p2F>2) {
                    players.get(1).getHand().discardCard(p2Cd.get(i).cardID());
                    --p2F;
                    break;
                }
            }
            AdventureCards c = aDeck.drawCard(players.get(1).getHand());
            switch(c.getSubType()) {
                case WEAPON ->++p2W;
                case FOE -> ++p2F;
            }
        }
        p2List = players.get(1).getHand().getNumberOfEachCardCodeBySubType();
        p2Weapons = p2List.get(CardTypes.WEAPON);
        p2Foes = p2List.get(CardTypes.FOE);
        int p2w=0;
        int p2f=0;
        if(p2List.containsKey(CardTypes.WEAPON)) {
            for(Integer i : p2List.get(CardTypes.WEAPON).values()) {
                p2w+=i;
            }
        }
        if(p2List.containsKey(CardTypes.FOE)) {
            for(Integer i : p2List.get(CardTypes.FOE).values()) {
                p2f+=i;
            }
        }
        p2TestVals.put(CardTypes.FOE,p2f);
        p2TestVals.put(CardTypes.WEAPON,p2w);

        //Player 3 has Weapons but no Foes
        HashMap<CardTypes,HashMap<AllCardCodes<AdventureDeckCards>,Integer>> p3List = players.get(2).getHand().getNumberOfEachCardCodeBySubType();
        HashMap<AllCardCodes<AdventureDeckCards>,Integer> p3Weapons = p3List.get(CardTypes.WEAPON);
        int p3W=0;
        if(p3Weapons!=null) {
            for(Integer i : p3Weapons.values()) {
                p3W+=i;
            }
        }
        //Discard all Foes
        for(CardData cd : players.get(2).getHand().generateCardData()) {
            switch(cd.subType()) {
                case FOE -> players.get(2).actionDiscardCard(cd.cardID());
            }
        }

        while(p3W<1) {
            ArrayList<CardData> p3Cd = players.get(2).getHand().generateCardData();
            for(int i=0;i<p3Cd.size();++i) {
                if(p3Cd.get(i).subType()!=CardTypes.WEAPON) {
                    players.get(2).getHand().discardCard(p3Cd.get(i).cardID());
                    break;
                }
            }
            AdventureCards c = aDeck.drawCard(players.get(2).getHand());
            switch(c.getSubType()) {
                case WEAPON ->++p3W;
            }
        }
        p3List = players.get(2).getHand().getNumberOfEachCardCodeBySubType();
        p3Weapons = p3List.get(CardTypes.WEAPON);
        p3W=0;
        for(Integer i : p3Weapons.values()) {
            p3W+=i;
        }
        assert(!p3List.containsKey(CardTypes.FOE) || p3List.get(CardTypes.FOE).size()<1);
        p3TestVals.put(CardTypes.FOE,0);
        p3TestVals.put(CardTypes.WEAPON,p3W);


        //Player 4 has Foes but no Weapons
        HashMap<CardTypes,HashMap<AllCardCodes<AdventureDeckCards>,Integer>> p4List = players.get(3).getHand().getNumberOfEachCardCodeBySubType();
        HashMap<AllCardCodes<AdventureDeckCards>,Integer> p4Foes = p4List.get(CardTypes.FOE);
        int p4F=0;
        if(p4Foes!=null) {
            for(Integer i : p4Foes.values()) {
                p4F+=i;
            }
        }
        //Discard all Weapons
        for(CardData cd : players.get(3).getHand().generateCardData()) {
            switch(cd.subType()) {
                case WEAPON -> players.get(3).actionDiscardCard(cd.cardID());
            }
        }

        while(p4F<2) {
            ArrayList<CardData> p4Cd = players.get(3).getHand().generateCardData();
            for(int i=0;i<p4Cd.size();++i) {
                if(p4Cd.get(i).subType()!=CardTypes.FOE) {
                    players.get(3).getHand().discardCard(p4Cd.get(i).cardID());
                    break;
                }
            }
            AdventureCards c = aDeck.drawCard(players.get(3).getHand());
            switch(c.getSubType()) {
                case FOE ->++p4F;
            }
        }
        p4List = players.get(3).getHand().getNumberOfEachCardCodeBySubType();
        p4Foes = p4List.get(CardTypes.FOE);
        p4F=0;
        for(Integer i : p4Foes.values()) {
            p4F+=i;
        }
        assert(!p4List.containsKey(CardTypes.WEAPON) || p4List.get(CardTypes.WEAPON).size()<1);
        p4TestVals.put(CardTypes.FOE,p4F);
        p4TestVals.put(CardTypes.WEAPON,0);
        HashMap<Players,Integer> shieldRewards = new HashMap<>();

        //Make P3 and P4 highest rank
        shieldRewards.put(players.get(2),5);//Knight
        shieldRewards.put(players.get(3),5);//Knight
        EffectResolverService.getService().playerAwardedShields(shieldRewards);
        shieldRewards.clear();
        assert(players.get(2).getRank()==PlayerRanks.KNIGHT);
        assert(players.get(3).getRank()==PlayerRanks.KNIGHT);
        card.playCard(ePhase);
        game.getPlayerTurnService().setPlayerTurn(players.get(0));
        ePhase.startPhase(game.getPlayerTurnService());
        //Discard a single weapon card
        for(CardData cd : players.get(2).getHand().generateCardData()) {
            if(cd.subType()==CardTypes.WEAPON) {
                players.get(2).actionDiscardCard(cd.cardID());
                break;
            }
        }
        //Must discard 2 foes
        int p4discarded=0;
        for(CardData cd : players.get(3).getHand().generateCardData()) {
            if(cd.subType()==CardTypes.FOE) {
                players.get(3).actionDiscardCard(cd.cardID());
                ++p4discarded;
                if(p4discarded>1) {
                    break;
                }
            }
        }
        HashMap<AllCardCodes<AdventureDeckCards>,Integer> p3Weap = players.get(2).getHand().getNumberOfEachCardCodeBySubType().get(CardTypes.WEAPON);
        HashMap<AllCardCodes<AdventureDeckCards>,Integer> p4Foe = players.get(3).getHand().getNumberOfEachCardCodeBySubType().get(CardTypes.FOE);
        int p3TotWeap=0;
        int p4TotFoe=0;
        if(p3Weap!=null) {
            for(Integer i : p3Weap.values()) {
                p3TotWeap+=i;
            }
        }
        if(p4Foe!=null){
            for(Integer i : p4Foe.values()) {
                p4TotFoe+=i;
            }
        }
        assert((p3TestVals.get(CardTypes.WEAPON)==p3TotWeap+1) || p3TotWeap==0);
        assert((p4TestVals.get(CardTypes.FOE)==p4TotFoe+2) || p4TotFoe==0);

        //Make P1 Highest rank

        //Make P2 Highest Rank


    }
}