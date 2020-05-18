package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserInfo;
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
    public void validIdRetrieved() throws Exception {
        UserInfo.Response response = userInfo(token, username);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void correctUsernameRetrieved() throws Exception {
        UserInfo.Response response = userInfo(token, username);
        assertEquals(username, response.username);
    }

    @Test
    public void missingToken() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Request request = new UserInfo.Request(rootLinks, null, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Request request = new UserInfo.Request(rootLinks, invalidToken, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

}