package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2ClientInfo;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OAuth2ClientInfoTest extends OAuth2AbstractTest {

    private Root.Response rootLinks;
    private String username = "user";
    private TokenId token;
    private String name = "client";
    private String callback = "http://localhost/callback";
    private OAuth2Client.Id id;
    private OAuth2Client.Secret secret;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);

        OAuth2ClientAdd.Response info = addClient(token, username, name, callback);
        id = new OAuth2Client.Id(info.id);
        secret = new OAuth2Client.Secret(info.secret);

        rootLinks = new Root.Request().response(BASE_URL);
    }

    @Test
    public void correctName() throws Exception {
        OAuth2ClientInfo.Response response = clientInfo(token, username, id);
        assertEquals(name, response.name);
    }

    @Test
    public void correctId() throws Exception {
        OAuth2ClientInfo.Response response = clientInfo(token, username, id);
        assertEquals(id, new OAuth2Client.Id(response.id));
    }

    @Test
    public void correctSecret() throws Exception {
        OAuth2ClientInfo.Response response = clientInfo(token, username, id);
        assertEquals(secret, new OAuth2Client.Secret(response.secret));
    }

    @Test
    public void correctCallback() throws Exception {
        OAuth2ClientInfo.Response response = clientInfo(token, username, id);
        assertEquals(callback, response.callback);
    }

    @Test
    public void missingToken() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo, null, id);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo, invalidToken, id);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void inexistentClient() throws Exception {
        OAuth2Client.Id inexistentId = new OAuth2Client.Id(id + "inexistentId");

        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo, token, inexistentId);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserClient() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);

        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo, token2, id);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}
