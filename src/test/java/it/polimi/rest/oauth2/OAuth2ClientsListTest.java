package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientsList;
import it.polimi.rest.messages.RootLinks;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OAuth2ClientsListTest extends OAuth2AbstractTest {

    private String username = "user";
    private TokenId token;
    private int count = 10;
    private RootLinks.Response rootLinks;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);

        for (int i = 0; i < count; i++) {
            addClient(token, username, "client" + i, "http://localhost/callback");
        }
       rootLinks = new RootLinks.Request().response(BASE_URL);
    }

    @Test
    public void response() throws Exception {
        OAuth2ClientsList.Response response = clientsList(token, username);
        assertEquals(count, Integer.valueOf(response.count).intValue());
    }

    @Test
    public void missingToken() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(userInfo,null);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(userInfo, wrongToken);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserClients() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(userInfo, token2);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}
