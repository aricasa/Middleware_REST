package it.polimi.rest;

import com.google.gson.Gson;
import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import it.polimi.rest.data.VolatileStorage;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runners.MethodSorters;
import spark.utils.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest
{

    static Authorizer authorizer = new ACL();
    static Storage storage = new VolatileStorage();
    static SessionManager sessionManager = new SessionManager(authorizer, storage);

    static ResourcesServer resourcesServer = new ResourcesServer(storage, sessionManager);
    static OAuth2Server oAuth2Server = new OAuth2Server(storage, sessionManager);

    static App app = new App(resourcesServer, oAuth2Server);

    static TokenId idSession;
    static TokenId idSession2;

    static String idImage;

    static String oauthClientId;
    static String oauthClientSecret;


    @Test
    public void testAinitialization() throws InterruptedException
    {
        app.start();

        Thread.sleep(2000);
        System.out.println("Setup the server");
    }

    @Test
    public void testBsignUp() throws IOException, InterruptedException
    {
        System.out.println("SIGN UP\n");
        Gson gson=new Gson();
        //assertEquals(storage.users().count,0);

        //Add first user
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/users");
        JSONObject jo = new JSONObject();
        jo.put("username","pinco");
        jo.put("password","pallino");
        StringEntity entity = new StringEntity(jo.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try (CloseableHttpResponse response = client.execute(httpPost))
        {
            String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            //System.out.println("Sign up: "+respBody);
            assertTrue(respBody.contains("pinco"));
            assertEquals(response.getStatusLine().getStatusCode(),201);
            assertFalse(respBody.contains("error"));
            //assertEquals(storage.users().count,1);
            //assertTrue(storage.userByUsername("pinco")!=null);
            //assertTrue(storage.userByUsername("pinco").password.compareTo("pallino")==0);
        }

        //Add a second user
        jo = new JSONObject();
        jo.put("username","ferrero");
        jo.put("password","rocher");
        entity = new StringEntity(jo.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        try (CloseableHttpResponse response = client.execute(httpPost))
        {
            String respBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            //System.out.println("Sign up: " + respBody);
            assertFalse(respBody.contains("error"));
            //assertEquals(storage.users().count, 2);
            //assertEquals(response.getStatusLine().getStatusCode(),201);
            //assertTrue(storage.userByUsername("ferrero") != null);
            //assertTrue(storage.userByUsername("ferrero").password.compareTo("rocher") == 0);
        }

        //Try to add again the same user (error)
        try (CloseableHttpResponse response = client.execute(httpPost))
        {
            String respBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            //System.out.println("Sign up: " + respBody);
            assertTrue(respBody.contains("error"));
            assertTrue(respBody.contains("already in use"));
            //assertEquals(storage.users().count,2);
            assertTrue(response.getStatusLine().getStatusCode()>=400);
        }

        client.close();
    }

    @Test
    public void testClogin() throws IOException, InterruptedException
    {
        System.out.println("LOGIN \n");
        Gson gson=new Gson();

        //Login first user
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("pinco","pallino"));
        HttpPost httpPost = new HttpPost("http://localhost:4567/sessions");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        idSession = new TokenId(jsonObj.getString("id"));

        //check whether the session token has as agent the right user id
        //assertTrue(sessionManager.token(idSession).agent().toString().compareTo(storage.userByUsername("pinco").id.toString())==0);
        assertEquals(response.getStatusLine().getStatusCode(),201);

        //Login second user
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("ferrero","rocher"));
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        jsonObj = new JSONObject(respBody);
        idSession2 = new TokenId(jsonObj.getString("id"));

        //check whether the session token has as agent the right user id
        //assertTrue(sessionManager.token(idSession2).agent().toString().compareTo(storage.userByUsername("ferrero").id.toString())==0);
        assertEquals(response.getStatusLine().getStatusCode(),201);

        //Login with wrong credentials (not existing Basic token)
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("ferrero","rochr"));
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(response.getStatusLine().getStatusCode()>=400);
        assertTrue(respBody.contains("error")&&respBody.contains("Wrong credentials"));
    }

    @Test
    public void testDgetUsers() throws IOException, InterruptedException
    {
        //Show users with real token
        HttpGet httpGet = new HttpGet("http://localhost:4567/users");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(response.getStatusLine().getStatusCode()<=299);
        JSONObject jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),2);
        List<Object> list=jsonObj.getJSONObject("_embedded").getJSONArray("item").toList();
        assertTrue(list.toString().contains("ferrero"));
        assertTrue(list.toString().contains("pinco"));

        //Show users with fake token (error)
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString()+"a");
        client = HttpClientBuilder.create().build();
        response = client.execute(httpGet);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.length()==0);
        assertTrue(response.getStatusLine().getStatusCode()>=400);
    }

    @Test
    public void testEgetUserInfo() throws IOException, InterruptedException
    {
        //Try obtaining info with the right token
        HttpGet httpGet = new HttpGet("http://localhost:4567/users/pinco");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.contains("pinco"));

        //Try obtaining info with the wrong token
        httpGet = new HttpGet("http://localhost:4567/users/pinco");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        client = HttpClientBuilder.create().build();
        response = client.execute(httpGet);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.length()==0);
        assertTrue(response.getStatusLine().getStatusCode()>=400);
    }

    @Test
    public void testFaddImage() throws IOException, InterruptedException
    {
        System.out.println("ADD IMAGE \n");

        //Add image of existing user with right bearer and existing path
        HttpPost httpPost = new HttpPost("http://localhost:4567/users/pinco/images");
        File image = new File("C:/Users/Arianna.DESKTOP-ABIVNVH/Desktop/image.jpg");
        MultipartEntityBuilder builder=MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file",image);
        builder.addTextBody("title","image");
        HttpEntity entity=builder.build();
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        idImage=jsonObj.getString("id");
        assertTrue(respBody.contains("image"));
        //assertTrue(sessionManager.dataProvider(idSession).images("pinco").count==1);
        //assertTrue(sessionManager.dataProvider(idSession).images("pinco").iterator().next().title.compareTo("image")==0);

        //Add image of existing user with wrong bearer
        httpPost = new HttpPost("http://localhost:4567/users/pinco/images");
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.length()==0);
        //assertTrue(sessionManager.dataProvider(idSession).images("pinco").count==1);

        //Add image of non existing user
        httpPost = new HttpPost("http://localhost:4567/users/pillo/images");
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.length()==0);
        //assertTrue(sessionManager.dataProvider(idSession).images("pinco").count==1);
    }

    @Test
    public void testGgetInfoAboutImages() throws InterruptedException, IOException
    {
        System.out.println("INFO ABOUT SPECIFIC IMAGE \n");

        HttpGet httpGet = new HttpGet("http://localhost:4567/users/pinco/images");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),1);

        httpGet = new HttpGet("http://localhost:4567/users/ferrero/images");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        response = client.execute(httpGet);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),0);

        HttpPost httpPost = new HttpPost("http://localhost:4567/users/ferrero/images");
        File image = new File("C:/Users/Arianna.DESKTOP-ABIVNVH/Desktop/image.jpg");
        MultipartEntityBuilder builder=MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file",image);
        builder.addTextBody("title","image");
        HttpEntity entity=builder.build();
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        response=client.execute(httpPost);
        response = client.execute(httpGet);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        jsonObj = new JSONObject(respBody);
        assertEquals(jsonObj.getInt("count"),1);
    }

    @Test
    public void testHDownloadImage() throws IOException
    {
        HttpGet httpGet = new HttpGet("http://localhost:4567/users/pinco/images/"+idImage+"/raw");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity=response.getEntity();
        if(entity!=null)
        {
            try(FileOutputStream outputStream = new FileOutputStream("C:/Users/Arianna.DESKTOP-ABIVNVH/Desktop/belooo.jpg"))
            {
                entity.writeTo(outputStream);
            }
        }
    }

    //DA SISTEMARE
    @Test
    public void testIoauthGet() throws IOException, InterruptedException
    {
        System.out.println("GET OAUTH2 \n");

        //Oauth2 with right token
        HttpGet httpGet = new HttpGet("http://localhost:4567/users/pinco/oauth2/clients");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.contains("pinco"));

        //Oauth2 with wrong token
        httpGet = new HttpGet("http://localhost:4567/users/pinco/oauth2/clients");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        response = client.execute(httpGet);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        //System.out.println(respBody);
        assertTrue(respBody.length()==0);
    }

    //DA SISTEMARE
    @Test
    public void testJoauthPost() throws IOException, InterruptedException
    {
        System.out.println("POST OAUTH2 \n");

        //Oauth2 with right token
        HttpPost httpPost = new HttpPost("http://localhost:4567/users/pinco/oauth2/clients");
        JSONObject jo = new JSONObject();
        jo.put("name","amazon");
        jo.put("callback","myUrl");
        StringEntity entity = new StringEntity(jo.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);
        String respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject jsonObj = new JSONObject(respBody);
        oauthClientId = jsonObj.getString("id");
        oauthClientSecret = jsonObj.getString("secret");
        assertTrue(jsonObj.getString("name").compareTo("amazon")==0);
        assertTrue(jsonObj.getString("callback").compareTo("myUrl")==0);

        //Oauth2 with wrong token
        httpPost = new HttpPost("http://localhost:4567/users/pinco/oauth2/clients");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        response = client.execute(httpPost);
        respBody=EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertTrue(respBody.length()==0);
    }

    //DA SISTEMARE
    @Test
    public void testKoauthDelete() throws IOException, InterruptedException
    {
        System.out.println("DELETE OAUTH2 \n");

        //Oauth2 with right token and existing client id
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/users/pinco/oauth2/clients/"+oauthClientId);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);

        //Oauth2 with right token and not existing client id
        httpDelete = new HttpDelete("http://localhost:4567/users/pinco/oauth2/clients/"+oauthClientId+"1");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        response = client.execute(httpDelete);
    }

    @Test
    public void testPdeleteImage() throws IOException, InterruptedException
    {
        System.out.println("DELETE IMAGE \n");

        //Delete image of existing user with right session token
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/users/pinco/images/"+idImage);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        //assertTrue(sessionManager.dataProvider(idSession).images("pinco").count==0);
        //assertTrue(sessionManager.dataProvider(idSession2).images("ferrero").count==1);
    }

    @Test
    public void testQcorrectLogout() throws IOException, InterruptedException
    {
        System.out.println("LOGOUT \n");

        //Logout user with existing session token
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/sessions/"+idSession2);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        //sessionManager.token(idSession2);
    }

    @Test
    public void testRincorrectLogout() throws IOException, InterruptedException
    {
        //Logout user with non existing session token
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/sessions/"+idSession);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString()+"a");
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        //assertTrue(sessionManager.token(idSession).agent().toString().compareTo(storage.userByUsername("pinco").id.toString())==0);
    }

    @Test
    public void testSdeleteUser() throws IOException, InterruptedException
    {
        //Delete existing user with right token
        System.out.println("DELETE USER \n");
        //assertEquals(storage.users().count,2);
        HttpDelete httpDelete = new HttpDelete("http://localhost:4567/users/pinco");
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpDelete);
        //assertEquals(storage.users().count,1);

        //Delete existing user with WRONG token
        httpDelete = new HttpDelete("http://localhost:4567/users/ferrero");
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString());
        response = client.execute(httpDelete);
        //assertEquals(storage.users().count,1);

        //Delete non existing user with existing token
        httpDelete = new HttpDelete("http://localhost:4567/users/ferrer");
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession2.toString());
        response = client.execute(httpDelete);
        //assertEquals(storage.users().count,1);
    }



}