package it.polimi.rest.oauth2;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2Grant;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.eclipse.jetty.io.ssl.ALPNProcessor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class OAuth2GrantTest extends OAuth2AbstractTest {

    private OAuth2Client.Id clientId;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("owner", "pass");
        TokenId token = new TokenId(login("owner", "pass").id);
        OAuth2ClientAdd.Response response = addClient(token, "owner", "client", callback);
        clientId = new OAuth2Client.Id(response.id);
    }

    @Test
    public void response() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Response response = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertNotNull(response.authorizationCode);
        assertFalse(response.authorizationCode.trim().isEmpty());
        assertEquals("state", response.state);
    }

    @Test
    public void missingToken() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(null, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.UNAUTHORIZED,request.run(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void wrongToken() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(new TokenId("fakeToken"), clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.UNAUTHORIZED,request.run(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void missingClientID() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(token, null, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void wrongClientID() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(token, new OAuth2Client.Id("fakeClientID"), callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.NOT_FOUND,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingCallback() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, null,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void wrongCallback() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, "fakeCallback",
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.run(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void missingScopes() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, callback,
                null, "state");

        assertEquals(HttpStatus.FOUND,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongScopes() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);


       // OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, callback,
         //       Arrays.asList(new Scope("hola")), "state");

        //assertEquals(HttpStatus.FOUND,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingState() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                null);

        assertEquals(HttpStatus.FOUND,request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    // TODO: wrong scopes

}