package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Request;
import it.polimi.rest.messages.RootLinks;
import it.polimi.rest.messages.UserAdd;
import org.apache.http.HttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAddTest extends AbstractTest {

    private String username = "user";
    private String password = "pass";

    @Test
    public void response() throws Exception {
        UserAdd.Response response = addUser(username, password);
        assertEquals(username, response.username);
    }

    @Test
    public void missingUsername() throws Exception {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        Request request = new UserAdd.Request(rootLinks,null, password);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingPassword() throws Exception {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        Request request = new UserAdd.Request(rootLinks, username, null);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void alreadyExistingUser() throws Exception {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        Request request = new UserAdd.Request(rootLinks, username, password);
        request.rawResponse(BASE_URL);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}