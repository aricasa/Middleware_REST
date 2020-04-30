package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageAdd;
import it.polimi.rest.messages.UserAdd;
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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImageAddTest extends AbstractTest
{
    private TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String imageTitle = "image";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Add image
        File image = new File(getClass().getClassLoader().getResource(imageTitle+".jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file",image)
                .addTextBody("title",imageTitle);

        HttpEntity entity=builder.build();

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(entity)
                .build();

        ImageAdd.Response response = parseJson(client.execute(request), ImageAdd.Response.class);
        assertEquals(imageTitle, response.title);
    }

    @Test
    public void incorrectUser() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String username2 = "user2";
        String password2 = "pass2";

        String imageTitle = "image";

        addUser(username,password);
        addUser(username2,password2);
        idSession = loginUser(username,password);

        //Add image
        File image = new File(getClass().getClassLoader().getResource(imageTitle+".jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file",image)
                .addTextBody("title",imageTitle);

        HttpEntity entity=builder.build();

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/users/"+username2+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(entity)
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String imageTitle = "image";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Add image
        File image = new File(getClass().getClassLoader().getResource(imageTitle+".jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file",image)
                .addTextBody("title",imageTitle);

        HttpEntity entity=builder.build();

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .setEntity(entity)
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String imageTitle = "image";

        addUser(username,password);

        //Add image
        File image = new File(getClass().getClassLoader().getResource(imageTitle+".jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file",image)
                .addTextBody("title",imageTitle);

        HttpEntity entity=builder.build();

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/users/"+username+"/images")
                .setEntity(entity)
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void fakeUser() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String imageTitle = "image";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Add image
        File image = new File(getClass().getClassLoader().getResource(imageTitle+".jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file",image)
                .addTextBody("title",imageTitle);

        HttpEntity entity=builder.build();

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/users/fakeUser/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(entity)
                .build();

        HttpResponse response = client.execute(request);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusLine().getStatusCode());
    }
}