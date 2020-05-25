package it.polimi.rest.oauth2;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OAuth2AuthorizeMessage;
import it.polimi.rest.messages.OAuth2ClientAddMessage;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class OAuth2AuthorizeTest extends OAuth2AbstractTest {

    private OAuth2Client.Id clientId;
    private String callback = "http://localhost/callback";

    @Before
    public void setUp() throws Exception {
        addUser("user", "pass");
        TokenId token = new TokenId(login("user", "pass").id);
        OAuth2ClientAddMessage.Response response = addClient(token, "user", "client", "http://localhost/callback");
        clientId = new OAuth2Client.Id(response.id);
    }

    @Test
    public void response() throws Exception {
        OAuth2AuthorizeMessage.Request request = new OAuth2AuthorizeMessage.Request(
                clientId,
                callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_USER)),
                "state"
        );

        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingClientId() throws Exception {
        OAuth2AuthorizeMessage.Request request = new OAuth2AuthorizeMessage.Request(
                null,
                callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_USER)),
                "state"
        );

        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingCallback() throws Exception {
        OAuth2AuthorizeMessage.Request request = new OAuth2AuthorizeMessage.Request(
                clientId,
                null,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_USER)),
                "state"
        );

        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingScopes() throws Exception {
        OAuth2AuthorizeMessage.Request request = new OAuth2AuthorizeMessage.Request(
                clientId,
                callback,
                null,
                "state"
        );

        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingState() throws Exception {
        OAuth2AuthorizeMessage.Request request = new OAuth2AuthorizeMessage.Request(
                clientId,
                callback,
                Arrays.asList(Scope.get(Scope.READ_USER), Scope.get(Scope.READ_USER)),
                null
        );

        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.OK, response.getStatusLine().getStatusCode());
    }

}