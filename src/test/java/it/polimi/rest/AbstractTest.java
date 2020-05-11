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
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTest {

    protected static final String BASE_URL = "http://localhost:4567";
    private App app;

    @Before
    public void start() throws InterruptedException {
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
    public void stop() throws InterruptedException {
        app.stop();
        Thread.sleep(500);
    }

    protected static <T extends Response> T parseJson(HttpResponse response, Class<T> clazz) throws IOException {
        HttpEntity entity = response.getEntity();
        String body = entity == null ? "{}" : EntityUtils.toString(entity, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        return gson.fromJson(body, clazz);
    }

    public static UsersList.Response usersList(TokenId token) throws IOException {
        UsersList.Request request = new UsersList.Request(token);
        return parseJson(request.run(BASE_URL), UsersList.Response.class);
    }

    public static UserInfo.Response userInfo(TokenId token, String username) throws IOException {
        UserInfo.Request request = new UserInfo.Request(token, username);
        return parseJson(request.run(BASE_URL), UserInfo.Response.class);
    }

    public static UserAdd.Response addUser(String username, String password) throws IOException {
        Request request = new UserAdd.Request(username, password);
        return parseJson(request.run(BASE_URL), UserAdd.Response.class);
    }

    public static UserRemove.Response removeUser(TokenId token, String username) throws IOException {
        Request request = new UserRemove.Request(token, username);
        return parseJson(request.run(BASE_URL), UserRemove.Response.class);
    }

    public static Login.Response login(String username, String password) throws IOException {
        Request request = new Login.Request(username, password);
        return parseJson(request.run(BASE_URL), Login.Response.class);
    }

    public static Logout.Response logout(TokenId token, String session) throws IOException {
        Request request = new Logout.Request(token, session);
        return parseJson(request.run(BASE_URL), Logout.Response.class);
    }

    public static ImagesList.Response imagesList(TokenId token, String username) throws IOException {
        ImagesList.Request request = new ImagesList.Request(token, username);
        return parseJson(request.run(BASE_URL), ImagesList.Response.class);
    }

    public static ImageInfo.Response imageInfo(TokenId token, String username, Image.Id image) throws IOException {
        ImageInfo.Request request = new ImageInfo.Request(token, username, image);
        return parseJson(request.run(BASE_URL), ImageInfo.Response.class);
    }

    public static ImageAdd.Response addImage(TokenId token, String username, String title, File file) throws IOException {
        ImageAdd.Request request = new ImageAdd.Request(token, username, title, file);
        return parseJson(request.run(BASE_URL), ImageAdd.Response.class);
    }

    public static ImageRemove.Response removeImage(TokenId token, String username, Image.Id image) throws IOException {
        ImageRemove.Request request = new ImageRemove.Request(token, username, image);
        return parseJson(request.run(BASE_URL), ImageRemove.Response.class);
    }

}
