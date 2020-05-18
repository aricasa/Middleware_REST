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

public class UserLogoutTest extends AbstractTest {

    private String username = "user";
    UserInfo.Response userInfo;
    private TokenId token;
    private String session;
    private Root.Response rootLinks;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        session = login(username, "pass").id;
        token = new TokenId(session);
        rootLinks = new Root.Request().response(BASE_URL);
        userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
    }

    @Test
    public void response() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Logout.Request request = new Logout.Request(rootLinks, token, session);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void userInfoNotAccessibleAnymore() throws Exception {
        logout(token, session);

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

        logout(token, session);

        //Check image no more accessible
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token, username, image);
        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void oAuth2ClientsNotAccessibleAnymore() throws Exception {
        // TODO: NO

        //Add client
        OAuth2Client.Id id = new OAuth2Client.Id(OAuth2AbstractTest.addClient(token, username, "clientName", "callback").id);

        logout(token, session);

        //Check clients no more visibles
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo,null, id);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Logout.Request request = new Logout.Request(rootLinks, invalidToken, session);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserToken() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        Login.Response session = login(user2, pass2);
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        Logout.Request request = new Logout.Request(rootLinks, token, session.id);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}