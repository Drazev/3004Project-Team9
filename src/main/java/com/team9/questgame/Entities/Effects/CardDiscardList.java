package com.team9.questgame.Entities.Effects;

import com.team9.questgame.ApplicationContextHolder;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.Entities.cards.Cards;
import com.team9.questgame.exception.IllegalEffectStateException;

public class CardDiscardList {
    private final Players target;
    private final Effects source;
    private final int foes;
    private final int allies;
    private final int tests;
    private final int amour;
    private final int weapons;
    private final int anyCard;
    int discardedFoes;
    int discardedAllies;
    int discardedTests;
    int discardedAmour;
    int discardedWeapons;
    int discardedAnyCard;
    boolean isResolved;
    EffectResolverService resolverService;

    public CardDiscardList(Players target, Effects source, int foes, int allies, int tests, int amour, int weapons,int anyCard) {
        this.target = target;
        this.source = source;
        this.foes = foes < 1 ? 0 : foes;
        this.allies = allies < 1 ? 0 : allies;
        this.tests = tests < 1 ? 0 : tests;
        this.amour = amour < 1 ? 0 : amour;
        this.weapons = weapons < 1 ? 0 : weapons;
        this.anyCard = anyCard < 1 ? 0 : anyCard;
        discardedAmour=0;
        discardedAllies=0;
        discardedFoes=0;
        discardedTests=0;
        discardedWeapons=0;
        discardedAnyCard=0;
        isResolved=false;
        if(target==null || source==null) {
            throw new IllegalEffectStateException("CardDiscardList cannot have Player or Source set to null",source,source!=null ? source.getCardSource() : null);
        }
        this.resolverService = ApplicationContextHolder.getContext().getBean(EffectResolverService.class);
    }

    public Players getTarget() {
        return target;
    }

    public Effects getSource() {
        return source;
    }

    public int getFoes() {
        return foes;
    }

    public int getAllies() {
        return allies;
    }

    public int getTests() {
        return tests;
    }

    public int getAmour() {
        return amour;
    }

    public int getAnyCard() {
        return anyCard;
    }

    public int getWeapons() {
        return weapons;
    }

    public void reportDiscardedCard(Cards card) {
        switch(card.getSubType()) {
            case FOE -> ++discardedFoes;
            case ALLY -> ++discardedAllies;
            case AMOUR -> ++discardedAmour;
            case WEAPON -> ++discardedWeapons;
            case TEST -> ++discardedTests;
        }
        ++discardedAnyCard;
        if(
                foes >= discardedFoes &&
                allies >= discardedAllies &&
                amour >= discardedAmour &&
                weapons >= discardedWeapons &&
                tests >= discardedTests &&
                anyCard >= discardedAnyCard
        )
        {
            isResolved=true;
            resolverService.onCardDiscardListResolved(this);
        }
    }
}
