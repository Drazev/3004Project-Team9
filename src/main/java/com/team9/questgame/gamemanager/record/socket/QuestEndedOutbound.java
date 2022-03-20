package com.team9.questgame.gamemanager.record.socket;

import com.team9.questgame.Data.PlayerData;

import java.util.ArrayList;

public record QuestEndedOutbound(ArrayList<PlayerData> winners) {
}