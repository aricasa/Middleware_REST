package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Request;
import it.polimi.rest.messages.UserAdd;
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
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UserRemoveTest extends AbstractTest
{
    TokenId idSession;

    @Test
    public void valid() throws Exception {

        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Delete user
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/users/"+username)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        client.execute(request);

        //Try obtain information about user
        request = RequestBuilder
                .get(BASE_URL+"/users/"+username)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());

        //Try obtain information about images
        request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());

        //Try login
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username,password));

         request = RequestBuilder
                .post(BASE_URL+"/sessions")
                .build();

        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());

        //Try signup
        Request body = new UserAdd.Request(username, password);

        request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        response = client.execute(request);
        assertEquals(HttpStatus.CREATED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws Exception {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Delete user
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/users/"+username)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .build();

        client.execute(request);

        //Try signup
        Request body = new UserAdd.Request(username, password);

        request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());
    }

    @Test
    public void notExistingUser() throws Exception {
        String username1 = "user1";
        String password1 = "pass1";

        String username2 = "user2";
        String password2 = "pass2";

        addUser(username1,password1);
        addUser(username2,password2);
        idSession = loginUser(username1,password1);

        //Delete user
        HttpUriRequest request = RequestBuilder
                .delete(BASE_URL+"/users/"+username2)
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());

        //Try signup user1
        Request body = new UserAdd.Request(username1, password1);

        request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        response = client.execute(request);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());
    }
}