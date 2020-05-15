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

    @Before
    public void setUp() throws Exception {
        addUser(username, password);
        token = new TokenId(login(username, password).id);
    }

    @Test
    public void response() throws Exception {
        UserRemove.Request request = new UserRemove.Request(token, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void loginNotPossible() throws Exception {
        removeUser(token, username);

        Login.Request request = new Login.Request(username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void signUpAgain() throws Exception {
        removeUser(token, username);

        UserAdd.Request request = new UserAdd.Request(username, password);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.CREATED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void infoNotAccessible() throws Exception {
        removeUser(token, username);

        UserInfo.Request request = new UserInfo.Request(token, username);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesNotAccessible() throws Exception {

        //Add image
        File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        Image.Id image = new Image.Id(addImage(token, token, username, "title", file).id);

        removeUser(token, username);

        //Check image no more accessible
        ImageInfo.Request request = new ImageInfo.Request(token, username, image);
        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void clientsNotAccessible() throws Exception {

        //Add client
        OAuth2ClientAdd.Response response = OAuth2AbstractTest.addClient(token, username, "clientName", "clientCallback");

        removeUser(token, username);

        //Check list of clients no more accessible
        OAuth2ClientsList.Request request1 = new OAuth2ClientsList.Request(token, username);
        assertEquals(HttpStatus.UNAUTHORIZED, request1.rawResponse(BASE_URL).getStatusLine().getStatusCode());

        //Check info of client no more accessible
        OAuth2ClientInfo.Request request2 = new OAuth2ClientInfo.Request(token, username, new OAuth2Client.Id(response.id));
        assertEquals(HttpStatus.UNAUTHORIZED, request2.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        UserRemove.Request request = new UserRemove.Request(wrongToken, username);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void deleteOtherUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");

        UserRemove.Request request = new UserRemove.Request(token, user2);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}