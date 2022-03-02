package com.team9.questgame.gamemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Payload for Card discard /
 */
@Data
@AllArgsConstructor
public class CardEvent {
    String name;
    String cardId;
}
