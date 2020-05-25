package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.RootMessage;
import it.polimi.rest.messages.UserAddMessage;
import org.apache.http.HttpResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAddTest extends AbstractTest {

    private String username = "user";
    private String password = "pass";

    @Test
    public void validIdCreated() throws Exception {
        UserAddMessage.Response response = addUser(username, password);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void correctUsername() throws Exception {
        UserAddMessage.Response response = addUser(username, password);
        assertEquals(username, response.username);
    }

    @Test
    public void validSelfLink() throws Exception {
        UserAddMessage.Response response = addUser(username, password);
        assertNotNull(response.selfLink());
    }

    @Test
    public void missingUsername() throws Exception {
        RootMessage.Response rootLinks = rootLinks();
        UserAddMessage.Request request = new UserAddMessage.Request(rootLinks,null, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingPassword() throws Exception {
        RootMessage.Response rootLinks = rootLinks();
        UserAddMessage.Request request = new UserAddMessage.Request(rootLinks, username, null);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void alreadyExistingUser() throws Exception {
        RootMessage.Response rootLinks = rootLinks();
        UserAddMessage.Request request = new UserAddMessage.Request(rootLinks, username, password);
        request.rawResponse(BASE_URL);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}