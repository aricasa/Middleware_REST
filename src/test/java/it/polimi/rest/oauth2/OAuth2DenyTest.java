package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAddMessage;
import it.polimi.rest.messages.OAuth2DenyMessage;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OAuth2DenyTest extends OAuth2AbstractTest {

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
    public void validResponseError() throws Exception {
        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2DenyMessage.Response response = deny(clientId, callback, scopes, "state");

        assertEquals("access_denied", response.error);
    }

    @Test
    public void validRedirectionURI() throws Exception {
        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2DenyMessage.Response response = deny(clientId, callback, scopes, "state");

        assertEquals(callback, response.redirectionURI);
    }

    @Test
    public void inexistentClient() throws Exception {
        OAuth2Client.Id inexistentId = new OAuth2Client.Id(clientId + "inexistentId");
        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2DenyMessage.Request request = new OAuth2DenyMessage.Request(inexistentId, callback, scopes, "state");

        assertEquals(HttpStatus.NOT_FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingURL() throws Exception {
        List<Scope> scopes = Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES));
        OAuth2DenyMessage.Request request = new OAuth2DenyMessage.Request(clientId, "differentURL",  scopes, "state");

        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

}