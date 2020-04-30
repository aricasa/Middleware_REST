package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Request;
import it.polimi.rest.messages.UserAdd;
import it.polimi.rest.messages.UserLogin;
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
import org.junit.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;


public class UserLoginTest extends AbstractTest
{
    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);

        //Login
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username,password));

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/sessions")
                .build();

        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        assertEquals(HttpStatus.CREATED, client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongPassword() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);

        //Login
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username,"fakePass"));

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/sessions")
                .build();

        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(request);
        UserLogin.Response respLogin = parseJson(client.execute(request), UserLogin.Response.class);
        assertEquals(HttpStatus.UNAUTHORIZED, client.execute(request).getStatusLine().getStatusCode());
        assertTrue(respLogin.error.compareTo("Wrong credentials")==0);
    }

    @Test
    public void wrongUsername() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);

        //Login
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("fakeUsername",password));

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/sessions")
                .build();

        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(request);
        UserLogin.Response respLogin = parseJson(client.execute(request), UserLogin.Response.class);
        assertEquals(HttpStatus.UNAUTHORIZED, client.execute(request).getStatusLine().getStatusCode());
        assertTrue(respLogin.error.compareTo("Wrong credentials")==0);
    }

    @Test
    public void missingCredentials() throws IOException, InterruptedException
    {
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/sessions")
                .build();

        client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
        UserLogin.Response respLogin = parseJson(client.execute(request), UserLogin.Response.class);
        assertEquals(HttpStatus.UNAUTHORIZED, client.execute(request).getStatusLine().getStatusCode());
    }
}