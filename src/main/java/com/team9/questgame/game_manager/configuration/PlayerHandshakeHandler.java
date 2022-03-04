package com.team9.questgame.game_manager.configuration;

import com.sun.security.auth.UserPrincipal;
import com.team9.questgame.exception.UnauthorizedException;
import com.team9.questgame.game_manager.service.SessionService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;

import java.util.Map;

@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHandshakeHandler extends DefaultHandshakeHandler {
    private final Logger LOG = LoggerFactory.getLogger(PlayerHandshakeHandler.class);

    @Autowired
    private SessionService sessionService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LOG.info(request.getURI().getQuery());
        String name = UriComponentsBuilder.newInstance().query(request.getURI().getQuery()).build().getQueryParams().getFirst("name");
        String randomId = sessionService.getPlayerSessionId(name);

        if (randomId == null)
            throw new UnauthorizedException("Player not registered");

        LOG.info("User named '{}' with ID '{}' connected", name, randomId);
        return new UserPrincipal(randomId);
    }

}
