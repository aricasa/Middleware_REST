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
        UserRemove.Request request = new UserRemove.Request(token, username);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void loginNotPossible() throws Exception {
        removeUser(token, username);

        Login.Request request = new Login.Request(username, password);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void signUpAgain() throws Exception {
        removeUser(token, username);

        UserAdd.Request request = new UserAdd.Request(username, password);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void infoNotAccessible() throws Exception {
        removeUser(token, username);

        UserInfo.Request request = new UserInfo.Request(token, username);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    // TODO: images not accessible
    // TODO: clients not accessible

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        UserRemove.Request request = new UserRemove.Request(wrongToken, username);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void deleteOtherUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");

        UserRemove.Request request = new UserRemove.Request(token, user2);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}