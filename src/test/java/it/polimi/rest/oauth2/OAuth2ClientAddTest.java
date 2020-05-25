package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAddMessage;
import it.polimi.rest.messages.UserInfoMessage;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OAuth2ClientAddTest extends OAuth2AbstractTest {

    private String username = "user";
    private TokenId token;
    private String name = "client";
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
    }

    @Test
    public void validId() throws Exception {
        OAuth2ClientAddMessage.Response response = addClient(token, username, name, callback);
        assertNotNull(response.id);
    }

    @Test
    public void validSecret() throws Exception {
        OAuth2ClientAddMessage.Response response = addClient(token, username, name, callback);
        assertNotNull(response.secret);
    }

    @Test
    public void correctName() throws Exception {
        OAuth2ClientAddMessage.Response response = addClient(token, username, name, callback);
        assertEquals(name, response.name);
    }

    @Test
    public void correctCallback() throws Exception {
        OAuth2ClientAddMessage.Response response = addClient(token, username, name, callback);
        assertEquals(callback, response.callback);
    }

    @Test
    public void validSelfLink() throws Exception {
        OAuth2ClientAddMessage.Response response = addClient(token, username, name, callback);
        assertNotNull(response.selfLink());
    }

    @Test
    public void missingToken() throws Exception {
        UserInfoMessage.Response userInfo = userInfo(token, username);
        OAuth2ClientAddMessage.Request request = new OAuth2ClientAddMessage.Request(userInfo, null, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        UserInfoMessage.Response userInfo = userInfo(token, username);
        OAuth2ClientAddMessage.Request request = new OAuth2ClientAddMessage.Request(userInfo, invalidToken, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        UserInfoMessage.Response userInfo = userInfo(token2, user2);
        OAuth2ClientAddMessage.Request request = new OAuth2ClientAddMessage.Request(userInfo, token, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}