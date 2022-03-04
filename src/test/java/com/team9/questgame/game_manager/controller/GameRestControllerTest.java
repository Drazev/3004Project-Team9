package com.team9.questgame.game_manager.controller;

import com.team9.questgame.game_manager.record.rest.RegistrationRequest;
import com.team9.questgame.game_manager.service.InboundService;
import com.team9.questgame.game_manager.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class GameRestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private InboundService inboundService;

    @Test
    public void handleRegister() {
        assertThat(
                this.restTemplate.postForObject(String.format("http://localhost:%d/api/register", port),
                new RegistrationRequest("Tom"), String.class))
            .contains("{\"confirmed\":true,\"name\":\"Tom\"}");

        // Register with the same name
        for (int i = 0; i < 10; i++) {
            assertThat(
                    this.restTemplate.postForObject(String.format("http://localhost:%d/api/register", port),
                    new RegistrationRequest("Tom"),
                    String.class))
                .contains("{\"confirmed\":false,\"name\":\"Tom\"}");
        }

        // Register with a different name
        for (int i = 0; i < 10; i++) {
            assertThat(
                    this.restTemplate.postForObject(String.format("http://localhost:%d/api/register", port),
                    new RegistrationRequest(String.format("Tom%d", i)),
                    String.class))
                .contains(String.format("{\"confirmed\":true,\"name\":\"Tom%d\"}", i));
        }

        assertThat(sessionService.getSessionMap().size()).isEqualTo(11);
    }

    @Test
    public void handleDeregister() {
        for (int i = 0; i < 10; i++) {
            sessionService.getSessionMap().put("" + i, "");
            this.restTemplate.delete(String.format("http://localhost:%d/api/register?name=%s", port, "" + i));
            assertThat(sessionService.getSessionMap().contains("" + i)).isEqualTo(false);
        }
    }

    @Test
    public void handleGetPlayer() {
        for (int i = 0; i < 5; i++) {
            sessionService.getSessionMap().put("" + i, "");
        }
        assertThat(
                this.restTemplate.getForObject(String.format("http://localhost:%d/api/player", port),
                String.class))
            .contains("{\"0\":\"\",\"1\":\"\",\"2\":\"\",\"3\":\"\",\"4\":\"\"}");
    }

    @Test
    public void handleGameStart() {
        // Not enough player
        assertThat(
                this.restTemplate.postForObject(String.format("http://localhost:%d/api/start", port),
                null,
                String.class))
            .contains("{\"gameStarted\":false}");

        sessionService.getSessionMap().put("1", "");
        sessionService.getSessionMap().put("2", "");

        // Enough player
        assertThat(this.restTemplate.postForObject(String.format("http://localhost:%d/api/start", port),
                null,
                String.class))
            .contains("{\"gameStarted\":true}");

        // Already started
        assertThat(this.restTemplate.postForObject(String.format("http://localhost:%d/api/start", port),
               null,
                String.class))
            .contains("{\"gameStarted\":true}");
    }

    @Test
    public void handleGameStatus() {
        assertThat(this.restTemplate.getForObject(String.format("http://localhost:%d/api/start", port), String.class))
                .contains("{\"gameStarted\":false}");


        sessionService.getSessionMap().put("1", "");
        sessionService.getSessionMap().put("2", "");
        inboundService.startGame();
        assertThat(this.restTemplate.getForObject(String.format("http://localhost:%d/api/start", port), String.class))
                .contains("{\"gameStarted\":true}");

    }

}
