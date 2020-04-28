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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DeleteImageTest extends AbstractTest
{
    private static final String URLusers = BASE_URL + "/users";
    private static final String URLsessions = BASE_URL + "/sessions";
    private static final String URLimagesUser1 = URLusers + "/pinco/images";
    private static final String URLimagesUser2 = URLusers + "/ferrero/images";

    static TokenId idSession;
    static String imageID;

    public void initializeUsers() throws InterruptedException, IOException
    {
        //Create user1
        HttpPost httpPost = new HttpPost(URLusers);
        JSONObject credentials = new JSONObject();
        credentials.put("username","pinco");
        credentials.put("password","pallino");
        StringEntity entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(httpPost);

        //Create user2
        httpPost = new HttpPost(URLusers);
        credentials = new JSONObject();
        credentials.put("username","ferrero");
        credentials.put("password","rocher");
        entity = new StringEntity(credentials.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        client = HttpClientBuilder.create().build();
        client.execute(httpPost);

        //Login user1
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
        imageID = respField.getString("id");
    }


    @Test
    public void correctTokenImageDeleting() throws IOException, InterruptedException
    {
        initializeUsers();

        //Delete image
        HttpDelete httpDelete = new HttpDelete(URLimagesUser1+"/"+imageID);
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);

        //Check the number of images
        HttpGet httpGet = new HttpGet(URLimagesUser1);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),0);
    }

    @Test
    public void incorrectTokenImageDeleting() throws IOException, InterruptedException
    {
        initializeUsers();

        //Delete image
        HttpDelete httpDelete = new HttpDelete(URLimagesUser1+"/"+imageID);
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken");
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);

        //Check the number of images
        HttpGet httpGet = new HttpGet(URLimagesUser1);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),1);
    }


    @Test
    public void incorrectUserImageDeleting() throws IOException, InterruptedException
    {
        initializeUsers();

        //Delete image
        HttpDelete httpDelete = new HttpDelete(URLimagesUser2+"/"+imageID);
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);

        //Check the number of images
        HttpGet httpGet = new HttpGet(URLimagesUser1);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),1);
    }

    @Test
    public void incorrectImageImageDeleting() throws IOException, InterruptedException
    {
        initializeUsers();

        //Delete image
        HttpDelete httpDelete = new HttpDelete(URLimagesUser1+"/"+imageID+"a");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        assertTrue(response.getStatusLine().getStatusCode()>=400 && response.getStatusLine().getStatusCode()<=499);

        //Check the number of images
        HttpGet httpGet = new HttpGet(URLimagesUser1);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),1);
    }
}