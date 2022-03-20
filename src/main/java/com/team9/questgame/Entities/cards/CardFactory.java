package com.team9.questgame.Entities.cards;

import com.team9.questgame.Entities.Effects.CardEffects.*;
import com.team9.questgame.Entities.Effects.Effects;


public class CardFactory {
    private static CardFactory instance=null;
    private static final String adventureCardImgSrc = "./Assets/Adventure Deck (346x470)/Adventure Deck Card Back.png";
    private static final String storyCardImgSrc = "./Assets/Story Deck (327x491)/Story Deck Card Back.png";

    private CardFactory()
    {

    }

    public static CardFactory getInstance() {
        if(instance==null)
        {
            instance = new CardFactory();
        }
        return instance;
    }

    public Cards createCard(Decks assignedDeck,Enum cardEnumId) {
        return null;
    }

    public AdventureCards createCard(Decks assignedDeck,AdventureDeckCards cardEnumId) {
        AdventureCards card=null;
        Effects effect;

        switch(cardEnumId)
        {
            case EXCALIBUR:
                card = new WeaponCards(
                        assignedDeck,
                        null,
                        "Excalibur",
                        cardEnumId.getSubType(),
                        "Weapon - Excalibur.png",
                        cardEnumId,
                        30
                );
                break;
            case LANCE:
                card = new WeaponCards(
                        assignedDeck,
                        null,
                        "Lance",
                        cardEnumId.getSubType(),
                        "Weapon - Lance.png",
                        cardEnumId,
                        20
                );
                break;
            case BATTLE_AX:
                card = new WeaponCards(
                        assignedDeck,
                        null,
                        "Battle-ax",
                        cardEnumId.getSubType(),
                        "Weapon - Battle-ax.png",
                        cardEnumId,
                        15
                );
                break;
            case SWORD:
                card = new WeaponCards(
                        assignedDeck,
                        null,
                        "Sword",
                        cardEnumId.getSubType(),
                        "Weapon - Sword.png",
                        cardEnumId,
                        10
                );
                break;
            case HORSE:
                card = new WeaponCards(
                        assignedDeck,
                        null,
                        "Horse",
                        cardEnumId.getSubType(),
                        "Weapon - Horse.png",
                        cardEnumId,
                        10
                );
                break;
            case DAGGER:
                card = new WeaponCards(
                        assignedDeck,
                        null,
                        "Dagger",
                        cardEnumId.getSubType(),
                        "Weapon - Dagger.png",
                        cardEnumId,
                        5
                );
                break;
            case TEST_OF_THE_QUESTING_BEAST:
                card = new TestCards(
                        assignedDeck,
                        "Minimum 4 Bid on the Search for the Questing Best Quest",
                        "Test of the Questing Beast",
                        cardEnumId.getSubType(),
                        "Test - Test of the Questing Beast.png",
                        cardEnumId,
                        0,
                        4,
                        StoryDeckCards.SEARCH_FOR_THE_QUESTING_BEAST
                );
                break;
            case TEST_OF_TEMPTATION:
                card = new TestCards(
                        assignedDeck,
                        null,
                        "Test of Temptation",
                        cardEnumId.getSubType(),
                        "Test - Test of Temptation.png",
                        cardEnumId,
                        0
                );
                break;
            case TEST_OF_VALOR:
                card = new TestCards(
                        assignedDeck,
                        null,
                        "Test of Valor",
                        cardEnumId.getSubType(),
                        "Test - Test of Valor.png",
                        cardEnumId,
                        0
                );
                break;
            case TEST_OF_MORGAN_LE_FEY:
                card = new TestCards(
                        assignedDeck,
                        null,
                        "Test of Morgan Le Fey",
                        cardEnumId.getSubType(),
                        "Test - Test of Morgan Le Fey.png",
                        cardEnumId,
                        3
                );
                break;
            case QUEEN_ISEULT:
                card = new AllyCards(
                        assignedDeck,
                        "4 Bids when Tristan is in play",
                        "Queen Iselut",
                        cardEnumId.getSubType(),
                        "Ally - Queen Iseult.png",
                        cardEnumId,
                        0,
                        2,
                        0,
                        4,
                        AdventureDeckCards.SIR_TRISTAN
                );
                break;
            case SIR_LANCELOT:
                card = new AllyCards(
                        assignedDeck,
                        "+25 when on the Quest to Defend the Queen's Honor",
                        "Sir Lancelot",
                        cardEnumId.getSubType(),
                        "Ally - Sir Lancelot.png",
                        cardEnumId,
                        15,
                        0,
                        25,
                        0,
                        StoryDeckCards.DEFEND_THE_QUEENS_HONOR
                );
                break;
            case SIR_GALAHAD:
                card = new AllyCards(
                        assignedDeck,
                        null,
                        "Sir Galahad",
                        cardEnumId.getSubType(),
                        "Ally - Sir Galahad.png",
                        cardEnumId,
                        15,
                        0
                );
                break;
            case SIR_GAWAIN:
                card = new AllyCards(
                        assignedDeck,
                        "+20 on the Test of the Green Knight Quest",
                        "Sir Gawain",
                        cardEnumId.getSubType(),
                        "Ally - Sir Gawain.png",
                        cardEnumId,
                        10,
                        0,
                        20,
                        0,
                        StoryDeckCards.TEST_OF_THE_GREEN_KNIGHT
                );
                break;
            case KING_PELLINORE:
                card = new AllyCards(
                        assignedDeck,
                        "4 Bids on the Search for the Questing Beast Quest",
                        "King Pellinore",
                        cardEnumId.getSubType(),
                        "Ally - King Pellinore.png",
                        cardEnumId,
                        10,
                        0,
                        0,
                        4,
                        StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL
                );
                break;
            case SIR_PERCIVAL:
                card = new AllyCards(
                        assignedDeck,
                        "+20 on the Search for the Holy Grail Quest",
                        "Sir Percival",
                        cardEnumId.getSubType(),
                        "Ally - Sir Percival.png",
                        cardEnumId,
                        5,
                        0,
                        20,
                        0,
                        StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL
                );
                break;
            case SIR_TRISTAN:
                card = new AllyCards(
                        assignedDeck,
                        "+20 when Queen Iseult is in Play",
                        "Sir Tristan",
                        cardEnumId.getSubType(),
                        "Ally - Sir Tristan.png",
                        cardEnumId,
                        10,
                        0,
                        20,
                        0,
                        AdventureDeckCards.QUEEN_ISEULT
                );
                break;
            case KING_ARTHUR:
                card = new AllyCards(
                        assignedDeck,
                        "2 Bids",
                        "King Arthur",
                        cardEnumId.getSubType(),
                        "Ally - King Arthur.png",
                        cardEnumId,
                        10,
                        2
                );
                break;
            case QUEEN_GUINEVERE:
                card = new AllyCards(
                        assignedDeck,
                        "3 Bids",
                        "Queen Guinevere",
                        cardEnumId.getSubType(),
                        "Ally - Queen Guinevere.png",
                        cardEnumId,
                        0,
                        3
                );
                break;
            case MERLIN:
                card = new AllyCards(
                        assignedDeck,
                        "Player may preview any one stage per quest",
                        "Merlin",
                        cardEnumId.getSubType(),
                        "Ally - Merlin.png",
                        cardEnumId,
                        0,
                        0
                );
                break;
            case AMOUR:
                card = new AmourCards(assignedDeck,"Amour.png");
                break;
            case MORDRED:
                card = new FoeCards(
                        assignedDeck,
                  "Use as a Foe or sacrifice at any time to remove any player's Ally from play",
                  "Mordred",
                  cardEnumId.getSubType(),
                        "Foe - Mordred.png",
                        cardEnumId,
                        30
                );
                break;
            case GIANT:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Giant",
                        cardEnumId.getSubType(),
                        "Foe - Giant.png",
                        cardEnumId,
                        40
                );
                break;
            case DRAGON:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Dragon",
                        cardEnumId.getSubType(),
                        "Foe - Dragon.png",
                        cardEnumId,
                        50,
                        70
                );
                break;
            case THIEVES:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Thieves",
                        cardEnumId.getSubType(),
                        "Foe - Thieves.png",
                        cardEnumId,
                        5
                );
                break;
            case BOAR:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Boar",
                        cardEnumId.getSubType(),
                        "Foe - Boar.png",
                        cardEnumId,
                        5,
                        15
                );
                break;
            case SAXONS:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Saxons",
                        cardEnumId.getSubType(),
                        "Foe - Saxons.png",
                        cardEnumId,
                        10,
                        20
                );
                break;
            case ROBBER_KNIGHT:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Robber Knight",
                        cardEnumId.getSubType(),
                        "Foe - Robber Knight.png",
                        cardEnumId,
                        15
                );
                break;
            case GREEN_KNIGHT:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Green Knight",
                        cardEnumId.getSubType(),
                        "Foe - Green Knight.png",
                        cardEnumId,
                        25,
                        40
                );
                break;
            case BLACK_KNIGHT:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Black Knight",
                        cardEnumId.getSubType(),
                        "Foe - Black Knight.png",
                        cardEnumId,
                        25,
                        35
                );
                break;
            case EVIL_KNIGHT:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Evil Knight",
                        cardEnumId.getSubType(),
                        "Foe - Evil Knight.png",
                        cardEnumId,
                        20,
                        30
                );
                break;
            case SAXON_KNIGHT:
                card = new FoeCards(
                        assignedDeck,
                        null,
                        "Saxon Knight",
                        cardEnumId.getSubType(),
                        "Foe - Saxon Knight.png",
                        cardEnumId,
                        15,
                        25
                );
                break;
        }

        return card;
    }

    public StoryCards createCard(Decks assignedDeck,StoryDeckCards cardEnumId) {
        StoryCards card=null;
        Effects effect;

        switch(cardEnumId) {
            case CHIVALROUS_DEED:
                effect = new ChivalrousDeedEffect();
                card = new EventCards(
                        assignedDeck,
                  "Player(s) with both lowest rank and least amount of shields, recieves 3 shields.",
                  "Chivalrous Deed",
                  cardEnumId.getSubType(),
                  "Event - Chivalrous Deed.png",
                        cardEnumId,
                        effect
                );
                effect.setSource(card);
                break;
            case POX:
                effect = new PoxEffect();
                card = new EventCards(
                        assignedDeck,
                        "All players except the player drawing this card loose 1 shield.",
                        "Pox",
                        cardEnumId.getSubType(),
                        "Event - Pox.png",
                        cardEnumId,
                        effect //bind event
                );
                effect.setSource(card);
                break;
            case PLAGUE:
                effect = new PlagueEffect();
                card = new EventCards(
                        assignedDeck,
                        "Drawer looses 2 shields if possible.",
                        "Plague",
                        cardEnumId.getSubType(),
                        "Event - Plague.png",
                        cardEnumId,
                        effect //bind event
                );
                effect.setSource(card);
                break;
            case KINGS_RECOGNITION:
                effect = new KingsRecognitionEffect();
                card = new EventCards(
                        assignedDeck,
                        "The next player(s) to complete a Quest will receive 2 extra shields.",
                        "King's Recognition",
                        cardEnumId.getSubType(),
                        "Event - Kings Recognition.png",
                        cardEnumId,
                        effect //bind event
                );
                effect.setSource(card);
                break;
            case QUEENS_FAVOR:
                effect = new QueensFavorEffect();
                card = new EventCards(
                        assignedDeck,
                        "The lowest ranked player(s) immediately receives 2 adventure cards",
                        "Queen's Favor",
                        cardEnumId.getSubType(),
                        "Event - Queens Favor.png",
                        cardEnumId,
                        effect
                );
                effect.setSource(card);
                break;
            case COURT_CALLED_TO_CAMELOT:
                effect = new CourtCalledToCamelotEffect();
                card = new EventCards(
                        assignedDeck,
                        "All Allies in play must be discarded.",
                        "Court Called to Camelot",
                        cardEnumId.getSubType(),
                        "Event - Court Called to Camelot.png",
                        cardEnumId,
                        effect //bind event
                );
                effect.setSource(card);
                break;
            case KINGS_CALL_TO_ARMS:
                card = new EventCards(
                        assignedDeck,
                        "The highest ranked player(s) must place 1 weapon in the discard pile. If unable to do so, 2 Foe Cards must be discarded.",
                        "King's Call to Arms",
                        cardEnumId.getSubType(),
                        "Event - Kings Call to Arms.png",
                        cardEnumId,
                        null //bind event
                );
                break;
            case PROSPERITY_THROUGHOUT_THE_REALM:
                effect = new ProsperityThroughtTheRealmEffect();
                card = new EventCards(
                        assignedDeck,
                        "All players may immediately draw 2 Adventure Cards.",
                        "Chivalrous Deed",
                        cardEnumId.getSubType(),
                        "Event - Chivalrous Deed.png",
                        cardEnumId,
                        effect //bind event
                );
                effect.setSource(card);
                break;
            case JOURNEY_THROUGH_THE_ENCHANTED_FOREST:
                card = new QuestCards (
                        assignedDeck,
                        null,
                        "Journey through the Enchanted Forest",
                        cardEnumId.getSubType(),
                        "Event - Journey through the Enchanted Forest.png",
                        cardEnumId,
                        3,
                        AdventureDeckCards.EVIL_KNIGHT
                );
                break;
            case VANQUISH_KING_ARTHURS_ENEMIES:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Vanquish King Arthur's Enemies",
                        cardEnumId.getSubType(),
                        "Event - Vanquish King Arthur's Enemies.png",
                        cardEnumId,
                        3
                );
                break;
            case REPEL_THE_SAXON_RAIDERS:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Repel the Saxon Raiders",
                        cardEnumId.getSubType(),
                        "Event - Repel the Saxon Raiders.png",
                        cardEnumId,
                        2,
                        GlobalCardTargets.ALL_SAXONS
                );
                break;
            case BOAR_HUNT:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Boar Hunt",
                        cardEnumId.getSubType(),
                        "Event - Boar Hunt.png",
                        cardEnumId,
                        2,
                        AdventureDeckCards.BOAR
                );
                break;
            case SEARCH_FOR_THE_QUESTING_BEAST:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Search for the Questing Beast",
                        cardEnumId.getSubType(),
                        "Event - Search for the Questing Beast.png",
                        cardEnumId,
                        4
                );
                break;
            case DEFEND_THE_QUEENS_HONOR:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Defend the Queen's Honor",
                        cardEnumId.getSubType(),
                        "Event - Defend the Queen's Honor.png",
                        cardEnumId,
                        4,
                        GlobalCardTargets.ALL_FOES
                );
                break;
            case SLAY_THE_DRAGON:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Slay the Dragon",
                        cardEnumId.getSubType(),
                        "Event - Slay the Dragon.png",
                        cardEnumId,
                        3,
                        AdventureDeckCards.DRAGON
                );
                break;
            case RESCUE_THE_FAIR_MAIDEN:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Rescue the Fair Maiden",
                        cardEnumId.getSubType(),
                        "Event - Rescue the Fair Maiden.png",
                        cardEnumId,
                        3,
                        AdventureDeckCards.BLACK_KNIGHT
                );
                break;
            case SEARCH_FOR_THE_HOLY_GRAIL:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Search for the Holy Grail",
                        cardEnumId.getSubType(),
                        "Event - Search for the Holy Grail.png",
                        cardEnumId,
                        5,
                        GlobalCardTargets.ALL_FOES
                );
                break;
            case TEST_OF_THE_GREEN_KNIGHT:
                card = new QuestCards(
                        assignedDeck,
                        null,
                        "Test of the Green Knight",
                        cardEnumId.getSubType(),
                        "Event - Test of the Green Knight.png",
                        cardEnumId,
                        4,
                        AdventureDeckCards.GREEN_KNIGHT
                );
                break;
            case TOURNAMENT_AT_CAMELOT:
                card = new TournamentCards(
                        assignedDeck,
                        null,
                        "Tournament At Camelot",
                        cardEnumId.getSubType(),
                        "Tournament - At Camelot.png",
                        cardEnumId,
                        3
                );
                break;
            case TOURNAMENT_AT_ORKNEY:
                card = new TournamentCards(
                        assignedDeck,
                        null,
                        "Tournament At Orkney",
                        cardEnumId.getSubType(),
                        "Tournament - At Orkney.png",
                        cardEnumId,
                        2
                );
                break;
            case TOURNAMENT_AT_TINTAGEL:
                card = new TournamentCards(
                        assignedDeck,
                        null,
                        "Tournament At Tintagel",
                        cardEnumId.getSubType(),
                        "Tournament - At Tintagel.png",
                        cardEnumId,
                        0
                );
                break;
            case TOURNAMENT_AT_YORK:
                card = new TournamentCards(
                        assignedDeck,
                        null,
                        "Tournament At York",
                        cardEnumId.getSubType(),
                        "Tournament - At York.png",
                        cardEnumId,
                        0
                );
                break;
        }

        return card;
    }

    public static String getAdventureCardImageURI() { return adventureCardImgSrc; }

    public static String getStoryCardImageURI() { return storyCardImgSrc; }


/**
    public Cards createCard(AllCardCodes cardEnumId) {
        System.out.println("Card enum type is not recognized "+cardEnumId);
        return null;
    }**/
}
