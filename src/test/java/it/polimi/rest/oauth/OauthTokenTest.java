package it.polimi.rest.oauth;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OauthClientAdd;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class OauthTokenTest extends OauthAbstractTest
{
    private TokenId idSession;
    private String clientId;
    private String clientSecret;
    private String authorizationCode;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        //Grant request
        HttpResponse response = sendGrantRequest(clientId, callback, idSession.toString(), "read_user" );
        String redictedtLoc = response.getFirstHeader("Location").getValue();
        authorizationCode = redictedtLoc.substring(redictedtLoc.lastIndexOf("=")+1);

        //Token request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/token")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("redirect_uri", callback)
                .addParameter("client_secret",clientSecret)
                .addParameter("grant_type","authorization_code")
                .addParameter("code",authorizationCode)
                .build();

        assertEquals(HttpStatus.CREATED,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectAuthorizationCode() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        //Grant request
        HttpResponse response = sendGrantRequest(clientId, callback, idSession.toString(), "read_user" );
        String redictedtLoc = response.getFirstHeader("Location").getValue();
        authorizationCode = redictedtLoc.substring(redictedtLoc.lastIndexOf("=")+1);

        //Token request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/token")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("redirect_uri", callback)
                .addParameter("client_secret",clientSecret)
                .addParameter("grant_type","authorization_code")
                .addParameter("code","fakeCode")
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientId() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        //Grant request
        HttpResponse response = sendGrantRequest(clientId, callback, idSession.toString(), "read_user" );
        String redictedtLoc = response.getFirstHeader("Location").getValue();
        authorizationCode = redictedtLoc.substring(redictedtLoc.lastIndexOf("=")+1);

        //Token request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/token")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id","fakeId")
                .addParameter("response_type","code")
                .addParameter("redirect_uri", callback)
                .addParameter("client_secret",clientSecret)
                .addParameter("grant_type","authorization_code")
                .addParameter("code",authorizationCode)
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingCallbackURL() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        //Grant request
        HttpResponse response = sendGrantRequest(clientId, callback, idSession.toString(), "read_user" );
        String redictedtLoc = response.getFirstHeader("Location").getValue();
        authorizationCode = redictedtLoc.substring(redictedtLoc.lastIndexOf("=")+1);

        //Token request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/token")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("redirect_uri", "differentCallbackUrl")
                .addParameter("client_secret",clientSecret)
                .addParameter("grant_type","authorization_code")
                .addParameter("code",authorizationCode)
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectClientSecret() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        //Grant request
        HttpResponse response = sendGrantRequest(clientId, callback, idSession.toString(), "read_user" );
        String redictedtLoc = response.getFirstHeader("Location").getValue();
        authorizationCode = redictedtLoc.substring(redictedtLoc.lastIndexOf("=")+1);

        //Token request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/token")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("redirect_uri", callback)
                .addParameter("client_secret","fakeSecret")
                .addParameter("grant_type","authorization_code")
                .addParameter("code",authorizationCode)
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void unknownGrantType() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        //Grant request
        HttpResponse response = sendGrantRequest(clientId, callback, idSession.toString(), "read_user" );
        String redictedtLoc = response.getFirstHeader("Location").getValue();
        authorizationCode = redictedtLoc.substring(redictedtLoc.lastIndexOf("=")+1);

        //Token request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/token")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("redirect_uri", callback)
                .addParameter("client_secret",clientSecret)
                .addParameter("grant_type","FakeGrantType")
                .addParameter("code",authorizationCode)
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }
}