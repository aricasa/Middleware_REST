package it.polimi.rest;

import com.google.gson.Gson;
import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import it.polimi.rest.data.VolatileStorage;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTest {

    protected static final String BASE_URL = "http://localhost:4567";
    protected HttpClient client = HttpClientBuilder.create().build();

    private App app;

    @Before
    public void setUp() throws InterruptedException {
        Authorizer authorizer = new ACL();
        Storage storage = new VolatileStorage();
        SessionManager sessionManager = new SessionManager(authorizer, storage);
        ResourcesServer resourcesServer = new ResourcesServer(storage, sessionManager);
        OAuth2Server oAuth2Server = new OAuth2Server(storage, sessionManager);
        app = new App(resourcesServer, oAuth2Server);

        app.start();
        Thread.sleep(500);
    }

    @After
    public void tearDown() throws InterruptedException {
        app.stop();
        Thread.sleep(500);
    }

    protected final <T extends Response> T parseJson(HttpResponse response, Class<T> clazz) throws IOException {
        String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        return gson.fromJson(body, clazz);
    }

    public void addUser(String username, String password) throws IOException {
        Request body = new UserAdd.Request(username, password);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users")
                .setEntity(body.jsonEntity())
                .build();

        client.execute(request);
    }

    public TokenId loginUser(String username, String password) throws IOException {
        CredentialsProvider provider=new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username,password));

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/sessions")
                .build();

        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        UserLogin.Response response = parseJson(client.execute(request), UserLogin.Response.class);
        return new TokenId(response.id);
    }

    public String addImage(String image, String username, String idSession) throws IOException {
        File imageFile = new File(getClass().getClassLoader().getResource(image+".jpg").getFile());
        MultipartEntityBuilder builder=MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file",imageFile)
                .addTextBody("title",image);

        HttpEntity entity=builder.build();

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL+"/users/"+username+"/images")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(entity)
                .build();

        ImageAdd.Response response = parseJson(client.execute(request), ImageAdd.Response.class);
        return response.id;
    }

}
