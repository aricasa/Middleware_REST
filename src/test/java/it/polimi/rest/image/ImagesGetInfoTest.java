package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImagesGetInfo;
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


public class ImagesGetInfoTest extends AbstractTest
{
    private TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        addImage("image",username,idSession.toString());

        //Check the number of images
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        ImagesGetInfo.Response response = parseJson(client.execute(request), ImagesGetInfo.Response.class);
        assertEquals("1",response.count);
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        //Check the number of images
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }


    @Test
    public void getInfoImageFakeUser() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String username2 = "user2";
        String password2 = "pass2";

        addUser(username,password);
        addUser(username2,password2);
        idSession = loginUser(username,password);

        //Check the number of images
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username2+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        assertEquals(HttpStatus.FORBIDDEN,client.execute(request).getStatusLine().getStatusCode());
    }
}