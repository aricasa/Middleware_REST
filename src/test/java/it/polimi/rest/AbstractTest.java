package it.polimi.rest;

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
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;

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

    protected Root.Response rootLinks() throws IOException {
        return new Root.Request().response(BASE_URL);
    }

    protected UsersList.Response usersList(TokenId token) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UsersList.Request request = new UsersList.Request(rootLinks, token);
        return request.response(BASE_URL);
    }

    protected UserInfo.Response userInfo(TokenId token, String username) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Request request = new UserInfo.Request(rootLinks, token, username);
        return request.response(BASE_URL);
    }

    protected UserAdd.Response addUser(String username, String password) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserAdd.Request request = new UserAdd.Request(rootLinks, username, password);
        return request.response(BASE_URL);
    }

    protected UserRemove.Response removeUser(TokenId token, String username) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        UserRemove.Request request = new UserRemove.Request(userInfo, token);
        return request.response(BASE_URL);
    }

    protected Login.Response login(String username, String password) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Login.Request request = new Login.Request(rootLinks, username, password);
        return request.response(BASE_URL);
    }

    protected Logout.Response logout(TokenId token, String session) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Logout.Request request = new Logout.Request(rootLinks, token, session);
        return request.response(BASE_URL);
    }

    protected ImagesList.Response imagesList(TokenId token, String username) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImagesList.Request request = new ImagesList.Request(userInfo, token);
        return request.response(BASE_URL);
    }

    protected ImageInfo.Response imageInfo(TokenId token, String username, Image.Id image) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token, image);
        return request.response(BASE_URL);
    }

    protected ImageAdd.Response addImage(TokenId token, String username, String title, File file) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageAdd.Request request = new ImageAdd.Request(userInfo, token, title, file);
        return request.response(BASE_URL);
    }

    protected ImageRemove.Response removeImage(TokenId token, String username, Image.Id image) throws IOException {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageRemove.Request request = new ImageRemove.Request(userInfo, token, image);
        return request.response(BASE_URL);
    }

}
