package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Request;
import it.polimi.rest.messages.Login;
import it.polimi.rest.messages.Logout;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserLogoutTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private String session;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        session = login(username, "pass").id;
        token = new TokenId(session);
    }

    @Test
    public void response() throws Exception {
        Request request = new Logout.Request(token, session);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void userInfo() throws Exception {
        logout(token, session);

        UserInfo.Request request = new UserInfo.Request(token, username);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    // TODO: images not accessible
    // TODO: clients not accessible

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        Request request = new Logout.Request(wrongToken, session);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserToken() throws Exception {
        String user2 = username + "2";

        addUser(user2, "pass");
        Login.Response session = login(user2, "pass");

        Request request = new Logout.Request(token, session.id);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}