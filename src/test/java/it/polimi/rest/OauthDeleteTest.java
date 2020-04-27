package it.polimi.rest;

import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import it.polimi.rest.data.VolatileStorage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OauthDeleteTest
{
    Authorizer authorizer = new ACL();
    Storage storage = new VolatileStorage();
    SessionManager sessionManager = new SessionManager(authorizer, storage);

    ResourcesServer resourcesServer = new ResourcesServer(storage, sessionManager);
    OAuth2Server oAuth2Server = new OAuth2Server(storage, sessionManager);

    App app = new App(resourcesServer, oAuth2Server);

    @Before
    public void startServer() throws InterruptedException
    {
        authorizer = new ACL();
        storage = new VolatileStorage();
        sessionManager = new SessionManager(authorizer, storage);
        resourcesServer = new ResourcesServer(storage, sessionManager);
        oAuth2Server = new OAuth2Server(storage, sessionManager);
        app = new App(resourcesServer, oAuth2Server);
        app.start();
        Thread.sleep(500);
    }

    @After
    public void stopServer() throws InterruptedException
    {
        app.stop();
        Thread.sleep(500);
    }

    @Test
    public void correctSignUp() throws IOException, InterruptedException
    {
        HttpPost httpPost = new HttpPost("http://localhost:4567/users");
        JSONObject credentials = new JSONObject();
        credentials.put("username","pinco");
        credentials.put("password","pallino");
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        JSONObject usernameField = new JSONObject(respBody);
        String username =usernameField.getString("username");
        assertTrue(username.contains("pinco"));
        assertTrue(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299);
        assertFalse(respBody.contains("error"));
    }

    @Test
    public void missingUsernameSignUp() throws IOException, InterruptedException
    {
        HttpPost httpPost = new HttpPost("http://localhost:4567/users");
        JSONObject credentials = new JSONObject();
        credentials.put("password","pallino");
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        JSONObject errorDescription = new JSONObject(respBody);
        assertTrue(respBody.contains("error"));
        String errorMsg =errorDescription.getString("error");
        assertTrue(errorMsg.contains("Username not specified"));
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);
    }

    @Test
    public void missingPasswordSignUp() throws IOException, InterruptedException
    {
        HttpPost httpPost = new HttpPost("http://localhost:4567/users");
        JSONObject credentials = new JSONObject();
        credentials.put("username","ambaraba");
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        JSONObject errorDescription = new JSONObject(respBody);
        assertTrue(respBody.contains("error"));
        String errorMsg =errorDescription.getString("error");
        assertTrue(errorMsg.contains("Password not specified"));
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);
    }

    @Test
    public void repeatedSignUp() throws IOException
    {
        HttpPost httpPost = new HttpPost("http://localhost:4567/users");
        JSONObject credentials = new JSONObject();
        credentials.put("username","ferrero");
        credentials.put("password","rocher");
        HttpEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        response = client.execute(httpPost);
        String respBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        JSONObject errorDescription = new JSONObject(respBody);
        assertTrue(respBody.contains("error"));
        String errorMsg =errorDescription.getString("error");
        assertTrue(errorMsg.contains("already in use"));
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);
    }
}