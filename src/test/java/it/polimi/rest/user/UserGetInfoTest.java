package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.UserAdd;
import it.polimi.rest.messages.UserRemove;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UserGetInfoTest extends AbstractTest
{
    TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Get info user
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        HttpResponse response = client.execute(request);
        UserRemove.Response responseRemove = parseJson(response, UserRemove.Response.class);
        assertEquals(HttpStatus.OK,response.getStatusLine().getStatusCode());
        assertEquals(username, responseRemove.username);
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Get info user
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/users/"+username)
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Get info user
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/users/"+username)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }
}