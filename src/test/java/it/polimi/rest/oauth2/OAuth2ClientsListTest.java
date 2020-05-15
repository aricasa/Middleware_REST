package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientsList;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OAuth2ClientsListTest extends OAuth2AbstractTest {

    private String username = "user";
    private TokenId token;
    private int count = 10;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);

        for (int i = 0; i < count; i++) {
            addClient(token, username, "client" + i, "http://localhost/callback");
        }
    }

    @Test
    public void response() throws Exception {
        OAuth2ClientsList.Response response = clientsList(token, username);
        assertEquals(count, Integer.valueOf(response.count).intValue());
    }

    @Test
    public void missingToken() throws Exception {
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(null, username);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(wrongToken, username);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserClients() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(token2, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}
