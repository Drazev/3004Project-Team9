package com.team9.questgame.Data;

import java.util.ArrayList;

public record HandData (
        long playerId,
        boolean isHandOversize,
        ArrayList<CardData> hand
){
}
