package it.polimi.rest;

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

import static org.junit.Assert.assertTrue;


public class DownloadImageTest extends AbstractTest
{
    private static final String URLusers = BASE_URL + "/users";
    private static final String URLuser1 = URLusers + "/pinco";
    private static final String URLuser2 = URLusers + "/ferrero";
    private static final String URLimagesUser1 = URLuser1 + "/images";
    private static final String URLimagesUser2 = URLuser2 + "/images";
    private static final String URLsessions = BASE_URL + "/sessions";

    TokenId idSession;
    String idImage;

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

        //Login
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pallino"));
        httpPost = new HttpPost(URLsessions);
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject respField = new JSONObject(respBody);
        idSession = new TokenId(respField.getString("id"));

        //Add image
        httpPost = new HttpPost(URLimagesUser1);
        File image = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file",image);
        builder.addTextBody("title","image");
        HttpEntity ent=builder.build();
        httpPost.setEntity(ent);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        respField = new JSONObject(respBody);
        idImage=respField.getString("id");
    }

    @Test
    public void correctTokenImageDownload() throws IOException, InterruptedException
    {
        initializeUsers();

        File image = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        HttpGet httpGet = new HttpGet(URLimagesUser1+"/"+idImage+"/raw");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        ByteArrayOutputStream downloadedImg = new ByteArrayOutputStream();
        response.getEntity().writeTo(downloadedImg);
        byte[] bufferDownloadedImage = downloadedImg.toByteArray();
        byte[] bufferImage;
        bufferImage=FileUtils.readFileToByteArray(image);
        assertTrue(Arrays.equals(bufferDownloadedImage,bufferImage));
    }

    @Test
    public void incorrectTokenImageDownload() throws IOException, InterruptedException
    {
        initializeUsers();

        File image = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        HttpGet httpGet = new HttpGet(URLimagesUser1+"/"+idImage+"/raw");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken");
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);
        assertTrue(respBody.length()==0);
    }

    @Test
    public void incorrectUserImageDownload() throws IOException, InterruptedException
    {
        initializeUsers();

        HttpPost httpPost = new HttpPost(URLimagesUser1);
        HttpGet httpGet = new HttpGet(URLimagesUser2+"/"+idImage+"/raw");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);
        assertTrue(respBody.length()==0);
    }
}