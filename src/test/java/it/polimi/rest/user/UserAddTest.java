package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Request;
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
        Request request = new UserAdd.Request(null, password);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingPassword() throws Exception {
        Request request = new UserAdd.Request(username, null);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void alreadyExistingUser() throws Exception {
        Request request = new UserAdd.Request(username, password);
        request.run(BASE_URL);
        HttpResponse response = request.run(BASE_URL);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}