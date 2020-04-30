package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.UserRemove;
import it.polimi.rest.messages.UsersGet;
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
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UsersGetTest extends AbstractTest
{
    private TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String username2 = "user2";
        String password2 = "pass2";

        addUser(username,password);
        addUser(username2,password2);
        idSession = loginUser(username,password);

        //Get users
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        HttpResponse response = client.execute(request);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject respField = new JSONObject(respBody);
        assertEquals(HttpStatus.OK,response.getStatusLine().getStatusCode());
        assertEquals(2, respField.getInt("count"));
        List<Object> list=respField.getJSONObject("_embedded").getJSONArray("item").toList();
        assertTrue(list.toString().contains(username));
        assertTrue(list.toString().contains(username2));
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Get users
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users")
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Get users
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }
}