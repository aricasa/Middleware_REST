package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Request;
import it.polimi.rest.messages.UserAdd;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAddTest extends AbstractTest {

    @Test
    public void valid() throws Exception {
        String username = "user";
        String password = "pass";

        Request body = new UserAdd.Request(username, password);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        UserAdd.Response response = parseJson(client.execute(request), UserAdd.Response.class);
        assertEquals(username, response.username);
    }

    @Test
    public void usernameMissing() throws Exception {
        Request body = new UserAdd.Request(null, "pass");

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void passwordMissing() throws Exception {
        Request body = new UserAdd.Request("user", null);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void alreadyExistingUser() throws Exception {
        Request body = new UserAdd.Request("user", "pass");

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        client.execute(request);
        HttpResponse response = client.execute(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}