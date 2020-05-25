package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.RootMessage;
import it.polimi.rest.messages.UserInfoMessage;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserInfoTest extends AbstractTest {

    private String username = "user";
    private TokenId token;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
    }

    @Test
    public void validId() throws Exception {
        UserInfoMessage.Response response = userInfo(token, username);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void correctUsername() throws Exception {
        UserInfoMessage.Response response = userInfo(token, username);
        assertEquals(username, response.username);
    }

    @Test
    public void validSelfLink() throws Exception {
        UserInfoMessage.Response response = userInfo(token, username);
        assertNotNull(response.selfLink());
    }

    @Test
    public void missingToken() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, null, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, invalidToken, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void inexistentUser() throws Exception {
        String inexistentUsername = username + "2";

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, token, inexistentUsername);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

}