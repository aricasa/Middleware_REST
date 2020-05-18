package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserInfo;
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
    private Root.Response rootLinks;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        rootLinks = new Root.Request().response(BASE_URL);
    }

    @Test
    public void validIdCreated() throws Exception {
        OAuth2ClientAdd.Response response = addClient(token, username, name, callback);
        assertNotNull(response.id);
    }

    @Test
    public void validSecretCreated() throws Exception {
        OAuth2ClientAdd.Response response = addClient(token, username, name, callback);
        assertNotNull(response.secret);
    }

    @Test
    public void correctNameRegistered() throws Exception {
        OAuth2ClientAdd.Response response = addClient(token, username, name, callback);
        assertEquals(name, response.name);
    }

    @Test
    public void correctCallbackRegistered() throws Exception {
        OAuth2ClientAdd.Response response = addClient(token, username, name, callback);
        assertEquals(callback, response.callback);
    }

    @Test
    public void missingToken() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(userInfo, null, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(userInfo, invalidToken, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUser() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);

        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token2, user2).response(BASE_URL);
        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(userInfo, token, name, callback);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}