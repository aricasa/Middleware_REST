package it.polimi.rest;

import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertTrue;


public class OauthDeleteClientTest extends AbstractTest
{
    private static final String URLusers = BASE_URL + "/users";
    private static final String URLoauth = URLusers + "/pinco/oauth2/clients";
    private static final String URLsessions = BASE_URL + "/sessions";

    TokenId idSession;
    String oauthClientId;

    public void initializeUsers() throws InterruptedException, IOException
    {
        //Add user
        HttpPost httpPost = new HttpPost(URLusers);
        JSONObject credentials = new JSONObject();
        credentials.put("username","pinco");
        credentials.put("password","pallino");
        HttpEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(httpPost);

        //Login user
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pallino"));
        httpPost = new HttpPost(URLsessions);
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject respField = new JSONObject(respBody);
        idSession = new TokenId(respField.getString("id"));

        //Add client
        httpPost = new HttpPost(URLoauth);
        credentials = new JSONObject();
        credentials.put("name","amazon");
        credentials.put("callback","myUrl");
        entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        respField = new JSONObject(respBody);
        oauthClientId = respField.getString("id");
        assertTrue(respField.getString("name").compareTo("amazon")==0);
        assertTrue(respField.getString("callback").compareTo("myUrl")==0);
    }

    @Test
    public void correctDeleteClient() throws IOException, InterruptedException
    {
        initializeUsers();

        HttpDelete httpDelete = new HttpDelete(URLoauth+"/"+oauthClientId);
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        assertTrue(response.getStatusLine().getStatusCode()>=200 && response.getStatusLine().getStatusCode()<=299);
    }

    @Test
    public void incorrectDeleteClient() throws IOException, InterruptedException
    {
        initializeUsers();

        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete httpDelete = new HttpDelete(URLoauth+"/"+oauthClientId+"1");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpResponse response = client.execute(httpDelete);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);

    }
}