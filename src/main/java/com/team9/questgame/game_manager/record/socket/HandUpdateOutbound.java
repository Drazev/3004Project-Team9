package com.team9.questgame.game_manager.record.socket;

import com.team9.questgame.Data.CardData;

import java.util.ArrayList;

public record HandUpdateOutbound(String name, ArrayList<CardData> hand) {
}
