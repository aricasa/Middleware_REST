package it.polimi.rest;

import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.AuthorizationTable;
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
        Authorizer authorizer = new AuthorizationTable();
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

    protected RootMessage.Response rootLinks() throws IOException {
        return new RootMessage.Request().response(BASE_URL);
    }

    protected UsersListMessage.Response usersList(TokenId token) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UsersListMessage.Request request = new UsersListMessage.Request(rootLinks, token);
        return request.response(BASE_URL);
    }

    protected UserInfoMessage.Response userInfo(TokenId token, String username) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, token, username);
        return request.response(BASE_URL);
    }

    protected UserAddMessage.Response addUser(String username, String password) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserAddMessage.Request request = new UserAddMessage.Request(rootLinks, username, password);
        return request.response(BASE_URL);
    }

    protected UserRemoveMessage.Response removeUser(TokenId token, String username) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        UserRemoveMessage.Request request = new UserRemoveMessage.Request(userInfo, token);
        return request.response(BASE_URL);
    }

    protected LoginMessage.Response login(String username, String password) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        LoginMessage.Request request = new LoginMessage.Request(rootLinks, username, password);
        return request.response(BASE_URL);
    }

    protected LogoutMessage.Response logout(TokenId token, String session) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        LogoutMessage.Request request = new LogoutMessage.Request(rootLinks, token, session);
        return request.response(BASE_URL);
    }

    protected ImagesListMessage.Response imagesList(TokenId token, String username) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImagesListMessage.Request request = new ImagesListMessage.Request(userInfo, token);
        return request.response(BASE_URL);
    }

    protected ImageInfoMessage.Response imageInfo(TokenId token, String username, Image.Id image) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, token, image);
        return request.response(BASE_URL);
    }

    protected ImageAddMessage.Response addImage(TokenId token, String username, String title, File file) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageAddMessage.Request request = new ImageAddMessage.Request(userInfo, token, title, file);
        return request.response(BASE_URL);
    }

    protected ImageRemoveMessage.Response removeImage(TokenId token, String username, Image.Id image) throws IOException {
        ImageInfoMessage.Response imageInfo = imageInfo(token, username, image);
        ImageRemoveMessage.Request request = new ImageRemoveMessage.Request(imageInfo, token);
        return request.response(BASE_URL);
    }

}
