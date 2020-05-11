package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientRemove;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OAuth2ClientRemoveTest extends OAuth2AbstractTest {

    private String username = "user";
    private TokenId token;
    private String name = "client";
    private String callback = "http://localhost/callback";
    private OAuth2Client.Id id;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        id = new OAuth2Client.Id(addClient(token, username, name, callback).id);
    }

    @Test
    public void response() throws Exception {
        OAuth2ClientRemove.Request request = new OAuth2ClientRemove.Request(token, username, id);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        OAuth2ClientRemove.Request request = new OAuth2ClientRemove.Request(wrongToken, username, id);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void inexistentClient() throws Exception {
        OAuth2Client.Id wrongId = new OAuth2Client.Id(id + "wrongId");
        OAuth2ClientRemove.Request request = new OAuth2ClientRemove.Request(token, username, wrongId);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserClient() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        OAuth2ClientRemove.Request request = new OAuth2ClientRemove.Request(token2, username, id);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongUsername() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");

        OAuth2ClientRemove.Request request = new OAuth2ClientRemove.Request(token, user2, id);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

}