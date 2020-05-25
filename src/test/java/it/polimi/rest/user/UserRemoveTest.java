package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserRemoveTest extends AbstractTest {

    private String username = "user";
    private String password = "pass";
    private TokenId token;

    @Before
    public void setUp() throws Exception {
        addUser(username, password);
        token = new TokenId(login(username, password).id);
    }

    @Test
    public void response() throws Exception {
        UserInfoMessage.Response userInfo = userInfo(token, username);
        UserRemoveMessage.Request request = new UserRemoveMessage.Request(userInfo, token);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void loginNotPossibleAnymore() throws Exception {
        removeUser(token, username);

        RootMessage.Response rootLinks = rootLinks();
        LoginMessage.Request request = new LoginMessage.Request(rootLinks, username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void signUpAgain() throws Exception {
        removeUser(token, username);

        RootMessage.Response rootLinks = rootLinks();
        UserAddMessage.Request request = new UserAddMessage.Request(rootLinks, username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void infoNotAccessibleAnymore() throws Exception {
        removeUser(token, username);

        RootMessage.Response rootLinks = rootLinks();
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, token, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        UserInfoMessage.Response userInfo = userInfo(token, username);
        UserRemoveMessage.Request request = new UserRemoveMessage.Request(userInfo, invalidToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

}