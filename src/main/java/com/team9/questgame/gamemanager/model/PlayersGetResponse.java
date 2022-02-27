package com.team9.questgame.gamemanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayersGetResponse {
    private Map<String, String> players;
}
