package com.team9.questgame.game_phases.utils;

import com.team9.questgame.Entities.Players;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PlayerTurnServiceTest {

    PlayerTurnService playerTurnService;

    @BeforeEach
    void setUp() {
        playerTurnService = new PlayerTurnService(new ArrayList<>(Arrays.asList(new Players("A"),
                                                                                new Players("B"),
                                                                                new Players("C"),
                                                                                new Players("D"))));
    }

    @Test
    void nextPlayerTurn() {
        assertThat(playerTurnService.nextPlayerTurn().getName()).isEqualTo("A");
        assertThat(playerTurnService.nextPlayerTurn().getName()).isEqualTo("B");
        assertThat(playerTurnService.nextPlayerTurn().getName()).isEqualTo("C");
        assertThat(playerTurnService.nextPlayerTurn().getName()).isEqualTo("D");
        assertThat(playerTurnService.nextPlayerTurn().getName()).isEqualTo("A");
    }

    @Test
    void nextPlayerIndexTurn() {
        assertThat(playerTurnService.nextPlayerIndexTurn()).isEqualTo(0);
        assertThat(playerTurnService.nextPlayerIndexTurn()).isEqualTo(1);
        assertThat(playerTurnService.nextPlayerIndexTurn()).isEqualTo(2);
        assertThat(playerTurnService.nextPlayerIndexTurn()).isEqualTo(3);
        assertThat(playerTurnService.nextPlayerIndexTurn()).isEqualTo(0);
    }
}