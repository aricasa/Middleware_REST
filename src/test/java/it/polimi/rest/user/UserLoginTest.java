package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.LoginMessage;
import it.polimi.rest.messages.RootMessage;
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
    public void validId() throws Exception {
        LoginMessage.Response response = login(username, password);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void missingCredentials() throws Exception {
        RootMessage.Response rootLinks = rootLinks();
        LoginMessage.Request request = new LoginMessage.Request(rootLinks,null, null);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        String invalidToken = password + "invalidToken";

        RootMessage.Response rootLinks = rootLinks();
        LoginMessage.Request request = new LoginMessage.Request(rootLinks, username, invalidToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

}