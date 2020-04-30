package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.UserLogin;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UserLogoutTest extends AbstractTest
{
    TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Logout
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/sessions/"+idSession)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        client = HttpClientBuilder.create().build();
        assertEquals(HttpStatus.NO_CONTENT, client.execute(request).getStatusLine().getStatusCode());

        //Try to get data to check if really logged out
        request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED, client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingToken1() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Logout
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/sessions/"+idSession)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .build();

        client = HttpClientBuilder.create().build();
        assertEquals(HttpStatus.UNAUTHORIZED, client.execute(request).getStatusLine().getStatusCode());

        //Try to get data to check if really logged out
        request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        assertEquals(HttpStatus.OK, client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingToken2() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Logout
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/sessions/fakeToken")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        client = HttpClientBuilder.create().build();
        assertEquals(HttpStatus.NOT_FOUND, client.execute(request).getStatusLine().getStatusCode());

        //Try to get data to check if really logged out
        request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        assertEquals(HttpStatus.OK, client.execute(request).getStatusLine().getStatusCode());
    }
}