package it.polimi.rest;

import com.google.gson.Gson;
import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import it.polimi.rest.data.VolatileStorage;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;


public class LoginTest
{
    Authorizer authorizer = new ACL();
    Storage storage = new VolatileStorage();
    SessionManager sessionManager = new SessionManager(authorizer, storage);

    ResourcesServer resourcesServer = new ResourcesServer(storage, sessionManager);
    OAuth2Server oAuth2Server = new OAuth2Server(storage, sessionManager);

    App app = new App(resourcesServer, oAuth2Server);

    @Before
    public void startServer() throws InterruptedException, IOException
    {
        authorizer = new ACL();
        storage = new VolatileStorage();
        sessionManager = new SessionManager(authorizer, storage);
        resourcesServer = new ResourcesServer(storage, sessionManager);
        oAuth2Server = new OAuth2Server(storage, sessionManager);
        app = new App(resourcesServer, oAuth2Server);
        app.start();
        Thread.sleep(500);

        //Create user
        HttpPost httpPost = new HttpPost("http://localhost:4567/users");
        JSONObject credentials = new JSONObject();
        credentials.put("username","pinco");
        credentials.put("password","pallino");
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(httpPost);
    }

    @After
    public void stopServer() throws InterruptedException
    {
        app.stop();
        Thread.sleep(500);
    }

    @Test
    public void correctLogin() throws IOException, InterruptedException
    {
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pallino"));
        HttpPost httpPost = new HttpPost("http://localhost:4567/sessions");
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        assertTrue(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299);
        assertTrue(respBody.contains("id") && respBody.contains("expiration") && respBody.contains("_links"));
    }

    @Test
    public void wrongPasswordLogin() throws IOException, InterruptedException
    {
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pincotto"));
        HttpPost httpPost = new HttpPost("http://localhost:4567/sessions");
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        assertTrue(response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() <= 499);
        JSONObject errorDescription = new JSONObject(respBody);
        assertTrue(respBody.contains("error"));
        String errorMsg =errorDescription.getString("error");
        assertTrue(errorMsg.contains("Wrong credentials"));
    }

    @Test
    public void wrongUsernameLogin() throws IOException, InterruptedException
    {
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pallo","pallino"));
        HttpPost httpPost = new HttpPost("http://localhost:4567/sessions");
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        assertTrue(response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() <= 499);
        JSONObject errorDescription = new JSONObject(respBody);
        assertTrue(respBody.contains("error"));
        String errorMsg =errorDescription.getString("error");
        assertTrue(errorMsg.contains("Wrong credentials"));
    }

    @Test
    public void missingCredentialsLogin() throws IOException, InterruptedException
    {
        HttpPost httpPost = new HttpPost("http://localhost:4567/sessions");
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() <= 499);
        assertTrue(respBody.length()==0);
    }
}