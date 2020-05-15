package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAdd;
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
    public void response() throws Exception {
        OAuth2ClientAdd.Response response = addClient(token, username, name, callback);

        assertNotNull(response.id);
        assertNotNull(response.secret);
        assertEquals(name, response.name);
        assertEquals(callback, response.callback);
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(wrongToken, username, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");

        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(token, user2, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}