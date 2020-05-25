package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAddMessage;
import it.polimi.rest.messages.OAuth2GrantMessage;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OAuth2GrantTest extends OAuth2AbstractTest {

    private OAuth2Client.Id clientId;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("owner", "pass");
        TokenId token = new TokenId(login("owner", "pass").id);
        OAuth2ClientAddMessage.Response response = addClient(token, "owner", "client", callback);
        clientId = new OAuth2Client.Id(response.id);
    }

    @Test
    public void redirectionToRightURI() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2GrantMessage.Response response = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(callback, response.redirectionURI);
    }

    @Test
    public void validAuthorizationCode() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2GrantMessage.Response response = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertNotNull(response.authorizationCode);
        assertFalse(response.authorizationCode.trim().isEmpty());
    }

    @Test
    public void sameState() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2GrantMessage.Response response = authCode(token, clientId, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals("state", response.state);
    }

    @Test
    public void missingToken() throws Exception {
        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(null, clientId, callback, scopes, "state");

        assertEquals(HttpStatus.UNAUTHORIZED,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        addUser("user", "pass");
        TokenId invalidToken = new TokenId(login("user", "pass").id + "invalidToken");

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(invalidToken, clientId, callback, scopes, "state");

        assertEquals(HttpStatus.UNAUTHORIZED,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void missingClientID() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, null, callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void inexistentClientID() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);
        OAuth2Client.Id inexistentId = new OAuth2Client.Id(clientId + "inexistentId");

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, inexistentId, callback, scopes, "state");

        assertEquals(HttpStatus.NOT_FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingCallback() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, clientId, null,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)),
                "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void mismatchingCallback() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, clientId, callback + "mismatch", scopes, "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());

    }

    @Test
    public void missingScopes() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, clientId, callback, null, "state");

        assertEquals(HttpStatus.FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingState() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);

        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, clientId, callback, scopes, null);

        assertEquals(HttpStatus.FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

}