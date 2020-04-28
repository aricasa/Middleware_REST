package it.polimi.rest;

import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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


public class OauthGetTest extends AbstractTest
{
    private static final String URLusers = BASE_URL + "/users";
    private static final String URLoauth = URLusers + "/pinco/oauth2/clients";
    private static final String URLsessions = BASE_URL + "/sessions";

    TokenId idSession;

    public void initializeUsers() throws InterruptedException, IOException
    {
        //Create user1
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
    }


    @Test
    public void correctGetOuath() throws IOException, InterruptedException
    {
        initializeUsers();

        HttpGet httpGet = new HttpGet(URLoauth);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.contains("pinco"));
        assertTrue(respBody.contains("count") && respBody.contains("_links"));
    }

    @Test
    public void incorrectGetOuath() throws IOException, InterruptedException
    {
        initializeUsers();

        HttpGet httpGet = new HttpGet(URLoauth);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken");
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.length()==0);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);
    }
}