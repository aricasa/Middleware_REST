package it.polimi.rest.oauth;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OauthClientAdd;
import it.polimi.rest.messages.Request;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class OauthGrantTest extends OauthAbstractTest
{
    private TokenId idSession;
    private String clientId;

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

        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","read_user")
                .addParameter("redirect_uri", callback)
                .addParameter("token",idSession.toString())
                .build();

        assertEquals(HttpStatus.FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
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

        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .addParameter("token","fakeToken")
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingURL() throws IOException, InterruptedException
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

        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", "differentUrl")
                .addParameter("token",idSession.toString())
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void fakeClient() throws IOException, InterruptedException
    {
        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id","myIdd")
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", "myUrl")
                .addParameter("token","token")
                .build();

        assertEquals(HttpStatus.NOT_FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
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

        //Grant request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }
}