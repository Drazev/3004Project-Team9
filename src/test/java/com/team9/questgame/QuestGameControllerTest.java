package com.team9.questgame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.questgame.Data.CardData;
import com.team9.questgame.Data.PlayerData;
import com.team9.questgame.Entities.Players;
import com.team9.questgame.exception.PlayerJoinException;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.OutboundService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class QuestGameControllerTest {
    QuestGameController game;
    ArrayList<Players> players;

    @Autowired
    ObjectMapper objMap;

    Logger LOG;

    @BeforeEach
    void setUp() {
        game = new QuestGameController();
        LOG = LoggerFactory.getLogger(QuestGameControllerTest.class);
        players = new ArrayList<>();
        players.add(new Players("Player 1"));
        players.add(new Players("Player 2"));
        players.add(new Players("Player 3"));
        players.add(new Players("Player 4"));
        players.add(new Players("Player 5"));
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void startGame() {

    }

    @Test
    void playerJoin() {
        for(int i=0;i<game.MAX_PLAYERS;++i) {
            game.playerJoin(players.get(i));
        }

        game.playerJoin(players.get(0)); //Should not add any new players and return nothing.

        PlayerJoinException error = null;
        try {
            game.playerJoin(players.get(4)); //This should trigger a PlayerJoinException
        }
        catch (PlayerJoinException e){
            error=e;
        }
        finally {
            assert(error!=null);
        }

        //TODO: Start Game then try to add a player. It should produce an error
    }

    @Test
    void removePlayer() {
        for(int i=0;i<game.MAX_PLAYERS;++i) {
            game.playerJoin(players.get(i));
        }

        PlayerJoinException error = null;
        try {
            game.playerJoin(players.get(4)); //This should trigger a PlayerJoinException
        }
        catch (PlayerJoinException e){
            error=e;
        }
        finally {
            assert(error!=null);
        }
    }

    @Test
    void receiveCard() {
    }

    @Test
    void discardCard() {
    }

    @Test
    void playCard() {
    }

    @Test
    void onGameReset() {
    }

    @Test
    void testGeneratePlayerData_AsJSON() throws JsonProcessingException {

        for(int i=0;i<game.MAX_PLAYERS;++i) {
            game.playerJoin(players.get(i));
        }

        System.out.println("DRAWING CARDS");
        System.out.println();
        //Deal card to each player up to max hand size
        for(int i=0;i<Players.MAX_HAND_SIZE;++i) {
            for (Players p : players) {
                game.dealCard(p);
            }
        }


        //generate Player data
        HashMap<Players,PlayerData> pData = new HashMap<>();
        for(Players p : players) {
            PlayerData pd = p.generatePlayerData();
            pData.put(p,pd);
            System.out.println("Player as Json data");
            System.out.println(objMap.writeValueAsString(p));

            System.out.println("\nPlayerData as Json");
            System.out.println(objMap.writeValueAsString(pd));
            System.out.println();
        }

        System.out.println("DISCARDING CARDS");
        System.out.println();
        //Discard all cards
        for( Map.Entry<Players,PlayerData> entry : pData.entrySet()) {
                for(CardData cd : entry.getValue().hand()) {
                    entry.getKey().actionDiscardCard(cd.cardID());
                }
        }

        for(Players p : players) {
            PlayerData pd = p.generatePlayerData();
            System.out.println("Player as Json data");
            System.out.println(objMap.writeValueAsString(p));

            System.out.println("\nPlayerData as Json");
            System.out.println(objMap.writeValueAsString(pd));
            System.out.println();
        }
    }
}