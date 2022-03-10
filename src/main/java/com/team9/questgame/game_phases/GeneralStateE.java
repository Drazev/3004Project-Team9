package com.team9.questgame.game_phases;

public enum GeneralStateE {
    NOT_STARTED,      // Not started (not necessary but good for maintaining consistency)
    SETUP,            // Player registration
    DRAW_STORY_CARD,  // Drawing story card
    QUEST_PHASE,            // Quest phase
    EVENT_PHASE,            // Event phase
    TOURNAMENT_PHASE,       // Tournament phase
    ENDED
}
