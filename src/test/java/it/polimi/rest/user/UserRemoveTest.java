package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.oauth2.OAuth2AbstractTest;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class UserRemoveTest extends AbstractTest {

    private String username = "user";
    private String password = "pass";
    private TokenId token;
    private RootLinks.Response rootLinks;
    private UserInfo.Response userInfo;

    @Before
    public void setUp() throws Exception {
        addUser(username, password);
        token = new TokenId(login(username, password).id);
        rootLinks = new RootLinks.Request().response(BASE_URL);
        userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
    }

    @Test
    public void response() throws Exception {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        UserRemove.Request request = new UserRemove.Request(userInfo, token);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void loginNotPossible() throws Exception {
        removeUser(token, username);
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        Login.Request request = new Login.Request(rootLinks, username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void signUpAgain() throws Exception {
        removeUser(token, username);
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserAdd.Request request = new UserAdd.Request(rootLinks, username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void infoNotAccessible() throws Exception {
        removeUser(token, username);

        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserInfo.Request request = new UserInfo.Request(rootLinks, token, username);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesNotAccessible() throws Exception {

        //Add image
        File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        Image.Id image = new Image.Id(addImage(token, username, "title", file).id);

        removeUser(token, username);

        //Check image no more accessible
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token, username, image);
        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void clientsNotAccessible() throws Exception {

        //Add client
        OAuth2Client.Id id = new OAuth2Client.Id(OAuth2AbstractTest.addClient(token, username, "clientName", "callback").id);

        removeUser(token, username);

        //Check clients no more visibles
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo,null, id);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        UserRemove.Request request = new UserRemove.Request(userInfo, wrongToken);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }


}