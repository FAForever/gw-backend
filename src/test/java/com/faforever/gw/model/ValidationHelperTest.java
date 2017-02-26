package com.faforever.gw.model;

import com.faforever.gw.bpmn.services.GwErrorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationHelperTest {
    ValidationHelper validationHelper;

    @Before
    public void setUp() throws Exception {
        validationHelper = new ValidationHelper(mock(GwErrorService.class));
    }

    @Test
    public void validateCharacterInBattle__ExpectTrue_Success() {
        GwCharacter character = mock(GwCharacter.class);
        Battle battle = mock(Battle.class);
        List<BattleParticipant> battleParticipantList = mock(List.class);

        when(character.getBattleParticipantList()).thenReturn(battleParticipantList);

         validationHelper.validateCharacterInBattle(character, battle, true);
    }

    @Test
    public void generateTestUser() {
        MacSigner macSigner = new MacSigner("secret");

        // {"user_id": 1, "user_name": "UEF Alpha", "authorities":["ROLE_USER"], "exp": 4102444740}
        // {"user_id": 2, "user_name": "UEF Bravo", "authorities":["ROLE_USER"], "exp": 4102444740}
        // {"user_id": 3, "user_name": "Cybran Charlie", "authorities":["ROLE_USER"], "exp": 4102444740}
        String tokenData = "{\"user_id\": 2, \"user_name\": \"UEF Bravo\", \"authorities\":[\"ROLE_USER\"], \"exp\": 4102444740}";
        Jwt token = JwtHelper.encode(tokenData, macSigner);
        String stringToken = token.getEncoded();
        System.out.println(stringToken);
        ;
//    public static User getUserFromJwtToken(String stringToken) {
//        try {
//            Jwt token = JwtHelper.decodeAndVerify(stringToken, new MacSigner("secret"));
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode data = objectMapper.readTree(token.getClaims());
//
//            List<String> authorities = objectMapper.readerFor(new TypeReference<List<String>>(){}).readValue(data.get("authorities"));
//            List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
//
//            authorities.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
//
//            return new User(data.get("user_id").asLong(), data.get("user_name").asText(), stringToken, grantedAuthorities);
//        }
//        catch(Exception e) {
//            throw new RuntimeException("user not authorized");
//        }
    }
}
