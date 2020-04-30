package it.polimi.rest.oauth;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OauthClientAdd;
import it.polimi.rest.messages.Request;
import it.polimi.rest.messages.UserAdd;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;


public class OauthAddClientTest extends AbstractTest
{
    private TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        String clientName = "IamAclient";
        String callback = "myUrl";

        Request body = new OauthClientAdd.Request(clientName,callback);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users/" + username + "/oauth2/clients")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(body.jsonEntity())
                .build();

        OauthClientAdd.Response response = parseJson(client.execute(request), OauthClientAdd.Response.class);
        assertEquals(clientName, response.name);
        assertEquals(callback, response.callback);
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        String clientName = "IamAclient";
        String callback = "myUrl";

        Request body = new OauthClientAdd.Request(clientName,callback);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users/" + username + "/oauth2/clients")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .setEntity(body.jsonEntity())
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void repeatedAdd() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        String clientName = "IamAclient";
        String callback = "myUrl";

        Request body = new OauthClientAdd.Request(clientName,callback);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users/" + username + "/oauth2/clients")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(body.jsonEntity())
                .build();

        OauthClientAdd.Response response1 = parseJson(client.execute(request), OauthClientAdd.Response.class);
        OauthClientAdd.Response response2 = parseJson(client.execute(request), OauthClientAdd.Response.class);
        assertNotEquals(response1.id,response2.id);
    }
}