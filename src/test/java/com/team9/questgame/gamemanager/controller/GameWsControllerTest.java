package com.team9.questgame.gamemanager.controller;

import com.team9.questgame.gamemanager.record.rest.EmptyJsonReponse;
import com.team9.questgame.gamemanager.record.socket.CardUpdateInbound;
import com.team9.questgame.gamemanager.record.socket.HandUpdateOutbound;
import com.team9.questgame.gamemanager.service.InboundService;
import com.team9.questgame.gamemanager.service.SessionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameWsControllerTest implements StompFrameHandler {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private InboundService inboundService;
    @Autowired
    private GameWsController wsController;
    private WebSocketStompClient stompClientA;
    private WebSocketStompClient stompClientB;
    private StompSession stompSessionA;
    private StompSession stompSessionB;
    @LocalServerPort
    private Integer port;

    ArrayBlockingQueue<Object> blockingQueue;

    private final Logger LOG = LoggerFactory.getLogger(GameWsControllerTest.class);

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        blockingQueue = new ArrayBlockingQueue<>(10);
        assertThat(sessionService.registerPlayer("A")).isTrue();
        assertThat(sessionService.registerPlayer("B")).isTrue();

        stompClientA = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClientB = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        stompClientA.setMessageConverter(new MappingJackson2MessageConverter());
        stompClientB.setMessageConverter(new MappingJackson2MessageConverter());

        stompSessionA = stompClientA.connect(String.format("http://localhost:%d/quest-game-websocket?name=A", port), new CustomStompSessionHandler()).get(1, SECONDS);
        stompSessionB = stompClientB.connect(String.format("http://localhost:%d/quest-game-websocket?name=B", port), new CustomStompSessionHandler()).get(1, SECONDS);

        stompSessionA.subscribe("/topic/general/player-connect", this);
        stompSessionB.subscribe("/topic/general/player-connect", this);

        stompSessionA.subscribe("/topic/general/game-start", this);
        stompSessionB.subscribe("/topic/general/game-start", this);

        stompSessionA.subscribe("/topic/player/hand-update", this);
        stompSessionB.subscribe("/topic/player/hand-update", this);

        assertThat(inboundService.startGame()).isTrue();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        LOG.info("Printing blocking queue");
        while(!blockingQueue.isEmpty()) {
            LOG.info("Received broadcast " + blockingQueue.poll(2, SECONDS));
        }
    }

    @Test
    void contextLoad() {
        assertThat(port).isNotNull();
        assertThat(stompClientA).isNotNull();
        assertThat(stompClientB).isNotNull();
        assertThat(sessionService).isNotNull();
        assertThat(inboundService).isNotNull();
        assertThat(stompSessionA).isNotNull();
        assertThat(stompSessionB).isNotNull();
        assertThat(wsController).isNotNull();
    }

    @Test
    void handlePlayerDrawCard() throws InterruptedException {
        int dummyCardId = 0;
        String payload = "{\"name\":\"A\",\"cardId\":32}";
        stompSessionA.send("/app/general/player-draw-card", new CardUpdateInbound("A", dummyCardId));
        LOG.info("Received broadcast " + blockingQueue.poll(1, SECONDS));
    }

    @Test
    void handlePlayerDiscardCard() {
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        LOG.info("header: " + headers.toString());
        try {
            if (headers.getDestination().equals("/topic/general/game-start")) {
                return EmptyJsonReponse.class;
            } else if (headers.getDestination().equals("/topic/player/hand-update")) {
                return HandUpdateOutbound.class;
            } else if (headers.getDestination().equals("/topic/general/player-connect")) {
                return Map.class;
            }
        } catch (NullPointerException e) {
            LOG.info("Destination not provided");
        }
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
//        LOG.info("Received broadcast from server : " + payload);
        blockingQueue.add(payload);
    }

}