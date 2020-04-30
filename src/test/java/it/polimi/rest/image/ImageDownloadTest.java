package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.TokenId;
import org.apache.commons.io.FileUtils;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImageDownloadTest extends AbstractTest
{
    private TokenId idSession;
    private String idImage;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String imageTitle = "image";

        addUser(username,password);
        idSession = loginUser(username,password);

        idImage = addImage(imageTitle, username, idSession.toString());

        //Download image
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images/"+idImage+"/raw")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        File image = new File(getClass().getClassLoader().getResource(imageTitle+".jpg").getFile());
        ByteArrayOutputStream downloadedImg = new ByteArrayOutputStream();
        client.execute(request).getEntity().writeTo(downloadedImg);
        byte[] bufferDownloadedImage = downloadedImg.toByteArray();
        byte[] bufferImage = FileUtils.readFileToByteArray(image);
        assertTrue(Arrays.equals(bufferDownloadedImage,bufferImage));
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String imageTitle = "image";

        addUser(username,password);
        idSession = loginUser(username,password);

        idImage = addImage(imageTitle, username, idSession.toString());

        //Download image
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username+"/images/"+idImage+"/raw")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED, client.execute(request).getStatusLine().getStatusCode());
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

        idImage = addImage(imageTitle, username, idSession.toString());

        //Download image
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL+"/users/"+username2+"/images/"+idImage+"/raw")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .build();

        assertEquals(HttpStatus.NOT_FOUND, client.execute(request).getStatusLine().getStatusCode());
    }
}