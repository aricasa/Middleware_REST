package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserAdd;
import org.apache.http.HttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAddTest extends AbstractTest {

    private String username = "user";
    private String password = "pass";

    @Test
    public void validIdCreated() throws Exception {
        UserAdd.Response response = addUser(username, password);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void correctUsernameRegistered() throws Exception {
        UserAdd.Response response = addUser(username, password);
        assertEquals(username, response.username);
    }

    @Test
    public void missingUsername() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserAdd.Request request = new UserAdd.Request(rootLinks,null, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingPassword() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserAdd.Request request = new UserAdd.Request(rootLinks, username, null);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void alreadyExistingUser() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserAdd.Request request = new UserAdd.Request(rootLinks, username, password);
        request.rawResponse(BASE_URL);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}