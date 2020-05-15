package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2Deny;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class OAuth2DenyTest extends OAuth2AbstractTest {

    private TokenId token;
    private OAuth2Client.Id clientId;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("owner", "pass");
        token = new TokenId(login("owner", "pass").id);
        OAuth2ClientAdd.Response response = addClient(token, "owner", "client", callback);
        clientId = new OAuth2Client.Id(response.id);
    }
    @Test
    public void valid() throws IOException, InterruptedException
    {
        OAuth2Deny.Request request = new OAuth2Deny.Request(token, clientId, callback,  Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)), "state");
        assertEquals(HttpStatus.FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        OAuth2Deny.Request request = new OAuth2Deny.Request(new TokenId("fakeToken"), clientId, callback,  Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)), "state");
        assertEquals(HttpStatus.FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingURL() throws IOException, InterruptedException
    {
        OAuth2Deny.Request request = new OAuth2Deny.Request(token, clientId, "differentURL",  Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)), "state");
        assertEquals(HttpStatus.BAD_REQUEST,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void fakeClient() throws IOException, InterruptedException
    {
        OAuth2Deny.Request request = new OAuth2Deny.Request(token, new OAuth2Client.Id("fakeClientID"), callback,  Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)), "state");
        assertEquals(HttpStatus.NOT_FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
    {
        OAuth2Deny.Request request = new OAuth2Deny.Request(null, clientId, callback,  Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_IMAGES)), "state");
        assertEquals(HttpStatus.FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }
}