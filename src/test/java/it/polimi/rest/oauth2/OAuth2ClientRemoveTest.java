package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientInfoMessage;
import it.polimi.rest.messages.OAuth2ClientRemoveMessage;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OAuth2ClientRemoveTest extends OAuth2AbstractTest {

    private String username = "user";
    private TokenId token;
    private OAuth2Client.Id id;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        id = new OAuth2Client.Id(addClient(token, username, "client", "http://localhost/callback").id);
    }

    @Test
    public void response() throws Exception {
        OAuth2ClientInfoMessage.Response clientInfo = clientInfo(token, username, id);
        OAuth2ClientRemoveMessage.Request request = new OAuth2ClientRemoveMessage.Request(clientInfo, token);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws Exception {
        OAuth2ClientInfoMessage.Response clientInfo = clientInfo(token, username, id);
        OAuth2ClientRemoveMessage.Request request = new OAuth2ClientRemoveMessage.Request(clientInfo, null);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        OAuth2ClientInfoMessage.Response clientInfo = clientInfo(token, username, id);
        OAuth2ClientRemoveMessage.Request request = new OAuth2ClientRemoveMessage.Request(clientInfo, invalidToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserClient() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);

        OAuth2ClientInfoMessage.Response clientInfo = clientInfo(token, username, id);
        OAuth2ClientRemoveMessage.Request request = new OAuth2ClientRemoveMessage.Request(clientInfo, token2);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}