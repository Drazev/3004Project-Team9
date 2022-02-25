package com.team9.questgame.Entities.cards;

public class CardFactory {
    private static CardFactory instance=null;

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

    public Cards createCard(DeckCards cardEnumId) {
        System.out.println("Card enum type is not recognized "+cardEnumId);
        return null;
    }

    public AdventureCards createCard(AdventureDeckCards cardEnumId) {
        AdventureCards card=null;

        switch(cardEnumId)
        {
            case EXCALIBUR:
                card = new WeaponCards(
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
                        "4 Bids when Tristan is in play",
                        "Queen Iselut",
                        cardEnumId.getSubType(),
                        "Ally - Queen Iselut.png",
                        cardEnumId,
                        0,
                        2,
                        0,
                        4,
                        AdventureDeckCards.SIR_TRISTAN,
                        null
                );
                break;
            case SIR_LANCELOT:
                card = new AllyCards(
                        "+25 when on the Quest to Defend the Queen's Honor",
                        "Sir Lancelot",
                        cardEnumId.getSubType(),
                        "Ally - Sir Lancelot.png",
                        cardEnumId,
                        15,
                        0,
                        25,
                        0,
                        null,
                        StoryDeckCards.DEFEND_THE_QUEENS_HONOR
                );
                break;
            case SIR_GALAHAD:
                card = new AllyCards(
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
                        "+20 on the Test of the Green Knight Quest",
                        "Sir Gawain",
                        cardEnumId.getSubType(),
                        "Ally - Sir Gawain.png",
                        cardEnumId,
                        10,
                        0,
                        20,
                        0,
                        null,
                        StoryDeckCards.TEST_OF_THE_GREEN_KNIGHT
                );
                break;
            case KING_PELLINORE:
                card = new AllyCards(
                        "4 Bids on the Search for the Questing Beast Quest",
                        "King Pellinore",
                        cardEnumId.getSubType(),
                        "Ally - King Pellinore.png",
                        cardEnumId,
                        10,
                        0,
                        0,
                        4,
                        null,
                        StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL
                );
                break;
            case SIR_PERCIVAL:
                card = new AllyCards(
                        "+20 on the Search for the Holy Grail Quest",
                        "Sir Percival",
                        cardEnumId.getSubType(),
                        "Ally - Sir Percival.png",
                        cardEnumId,
                        5,
                        0,
                        20,
                        0,
                        null,
                        StoryDeckCards.SEARCH_FOR_THE_HOLY_GRAIL
                );
                break;
            case SIR_TRISTAN:
                card = new AllyCards(
                        "+20 when Queen Iseult is in Play",
                        "Sir Tristan",
                        cardEnumId.getSubType(),
                        "Ally - Sir Tristan.png",
                        cardEnumId,
                        10,
                        0,
                        20,
                        0,
                        AdventureDeckCards.QUEEN_ISEULT,
                        null
                );
                break;
            case KING_ARTHUR:
                card = new AllyCards(
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
                card = new AmourCards();
                break;
            case MORDRED:
                card = new FoeCards(
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

    public StoryDeckCards createCard(StoryDeckCards cardEnumId) {
        StoryDeckCards card=null;

        switch(cardEnumId) {
            case CHIVALROUS_DEED:

                break;
            case POX:

                break;
            case PLAGUE:

                break;
            case KINGS_RECOGNITION:

                break;
            case QUEENS_FAVOR:

                break;
            case COURT_CALLED_TO_CAMELOT:

                break;
            case KINGS_CALL_TO_ARMS:

                break;
            case PROSPERITY_THROUGHOUT_THE_REALM:

                break;
            case JOURNEY_THROUGH_THE_ENCHANTED_FOREST:

                break;
            case VANQUISH_KING_ARTHURS_ENEMIES:

                break;
            case REPEL_THE_SAXON_RAIDERS:

                break;
            case BOAR_HUNT:

                break;
            case SEARCH_FOR_THE_QUESTING_BEAST:

                break;
            case DEFEND_THE_QUEENS_HONOR:

                break;
            case SLAY_THE_DRAGON:

                break;
            case RESCUE_THE_FAIR_MAIDEN:

                break;
            case SEARCH_FOR_THE_HOLY_GRAIL:

                break;
            case TEST_OF_THE_GREEN_KNIGHT:

                break;
            case TOURNAMENT_AT_CAMELOT:

                break;
            case TOURNAMENT_AT_ORKNEY:

                break;
            case TOURNAMENT_AT_TINTAGEL:

                break;
            case TOURNAMENT_AT_YORK:

                break;
        }

        return card;
    }
}
