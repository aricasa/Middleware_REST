package it.polimi.rest;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;


public class LoginTest extends AbstractTest
{
    private static final String URLusers = BASE_URL + "/users";
    private static final String URLsessions = BASE_URL + "/sessions";

    public void initializeUsers() throws InterruptedException, IOException
    {
        //Create user
        HttpPost httpPost = new HttpPost(URLusers);
        JSONObject credentials = new JSONObject();
        credentials.put("username","pinco");
        credentials.put("password","pallino");
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(httpPost);
    }

    @Test
    public void correctLogin() throws IOException, InterruptedException
    {
        initializeUsers();

        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pallino"));
        HttpPost httpPost = new HttpPost(URLsessions);
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        assertTrue(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299);
        assertTrue(respBody.contains("id") && respBody.contains("expiration") && respBody.contains("_links"));
    }

    @Test
    public void wrongPasswordLogin() throws IOException, InterruptedException
    {
        initializeUsers();

        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pincotto"));
        HttpPost httpPost = new HttpPost(URLsessions);
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
        initializeUsers();

        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pallo","pallino"));
        HttpPost httpPost = new HttpPost(URLsessions);
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
        initializeUsers();

        HttpPost httpPost = new HttpPost(URLsessions);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(response.getStatusLine().getStatusCode() >= 400 && response.getStatusLine().getStatusCode() <= 499);
        assertTrue(respBody.length()==0);
    }
}