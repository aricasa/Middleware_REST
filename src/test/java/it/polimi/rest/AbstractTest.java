package it.polimi.rest;

import com.google.gson.Gson;
import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import it.polimi.rest.data.VolatileStorage;
import it.polimi.rest.messages.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTest {

    protected static final String BASE_URL = "http://localhost:4567";
    protected final HttpClient client = HttpClientBuilder.create().build();

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

}
