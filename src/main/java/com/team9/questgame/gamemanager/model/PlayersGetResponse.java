package com.team9.questgame.gamemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class PlayersGetResponse {
    private ArrayList<String> players;

}
