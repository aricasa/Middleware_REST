package it.polimi.rest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class UsersTest extends AbstractTest {

    private static final String URL = BASE_URL + "/users";

    @Test
    public void signUp_valid() throws Exception {
        String username = "user";
        String password = "pass";

        JSONObject credentials = new JSONObject();
        credentials.put("username", username);
        credentials.put("password", password);
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        JSONObject usernameField = new JSONObject(respBody);
        assertEquals(username, usernameField.getString("username"));
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