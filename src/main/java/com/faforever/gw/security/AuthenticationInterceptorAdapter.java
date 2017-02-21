package com.faforever.gw.security;

import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.service.CharacterService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationInterceptorAdapter extends ChannelInterceptorAdapter {
    private final CharacterService characterService;

    @Inject
    public AuthenticationInterceptorAdapter(CharacterService characterService) {
        this.characterService = characterService;
        ;
    }

    @SneakyThrows
    private User getUserFromJwtToken(String stringToken) {
        Jwt token = JwtHelper.decodeAndVerify(stringToken, new MacSigner("secret"));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.readTree(token.getClaims());

        List<String> authorities = objectMapper.readerFor(new TypeReference<List<String>>() {
        }).readValue(data.get("authorities"));
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

        authorities.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));

        int fafId = data.get("user_id").asInt();
        GwCharacter character = characterService.getByFafId(fafId); // TODO: Check for the !one! active character of the user!

        return new User(fafId, character, data.get("user_name").asText(), stringToken, grantedAuthorities);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String stringToken = accessor.getFirstNativeHeader("X-Authorization");
            Principal user = getUserFromJwtToken(stringToken);
            accessor.setUser(user);
        }

        return message;
    }
}