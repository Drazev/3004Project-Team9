package com.team9.questgame.gamemanager.record.socket;

import javax.swing.text.html.Option;
import java.util.Optional;

public record PlayerPlayCardInbound(String name, long cardId, long playerID) {
}
