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
    private Root.Response rootLinks;
    private UserInfo.Response userInfo;

    @Before
    public void setUp() throws Exception {
        addUser(username, password);
        token = new TokenId(login(username, password).id);
        rootLinks = new Root.Request().response(BASE_URL);
        userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
    }

    @Test
    public void response() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        UserRemove.Request request = new UserRemove.Request(userInfo, token);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void loginNotPossibleAnymore() throws Exception {
        removeUser(token, username);

        Login.Request request = new Login.Request(rootLinks, username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void signUpAgain() throws Exception {
        removeUser(token, username);

        UserAdd.Request request = new UserAdd.Request(rootLinks, username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void infoNotAccessibleAnymore() throws Exception {
        removeUser(token, username);

        UserInfo.Request request = new UserInfo.Request(rootLinks, token, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesNotAccessibleAnymore() throws Exception {
        // TODO: NO
        //Add image
        File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        Image.Id image = new Image.Id(addImage(token, username, "title", file).id);

        removeUser(token, username);

        //Check image no more accessible
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token, username, image);
        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void oAuth2ClientsNotAccessibleAnymore() throws Exception {
        // TODO: NO
        //Add client
        OAuth2Client.Id id = new OAuth2Client.Id(OAuth2AbstractTest.addClient(token, username, "clientName", "callback").id);

        removeUser(token, username);

        //Check clients no more visibles
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo,null, id);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        UserRemove.Request request = new UserRemove.Request(userInfo, invalidToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }


}