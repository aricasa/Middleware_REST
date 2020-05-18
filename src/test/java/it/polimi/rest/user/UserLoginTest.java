package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Login;
import it.polimi.rest.messages.Root;
import org.apache.http.HttpResponse;
import org.junit.*;

import static org.junit.Assert.*;

public class UserLoginTest extends AbstractTest {

    private String username = "user";
    private String password = "pass";

    @Before
    public void setUp() throws Exception {
        addUser(username, password);
    }

    @Test
    public void validIdCreated() throws Exception {
        Login.Response response = login(username, password);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void missingCredentials() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Login.Request request = new Login.Request(rootLinks,null, null);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        String invalidToken = password + "invalidToken";

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Login.Request request = new Login.Request(rootLinks, username, invalidToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

}