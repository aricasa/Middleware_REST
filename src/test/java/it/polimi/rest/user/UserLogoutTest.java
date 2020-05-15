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
    private TokenId token;
    private String session;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        session = login(username, "pass").id;
        token = new TokenId(session);
    }

    @Test
    public void response() throws Exception {
        Request request = new Logout.Request(token, session);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void userInfo() throws Exception {
        logout(token, session);

        UserInfo.Request request = new UserInfo.Request(token, username);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesNotAccessible() throws Exception {

        //Add image
        File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
        Image.Id image = new Image.Id(addImage(token, token, username, "title", file).id);

        logout(token, session);

        //Check image no more accessible
        ImageInfo.Request request = new ImageInfo.Request(token, username, image);
        assertEquals(HttpStatus.UNAUTHORIZED, request.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void clientsNotAccessible() throws Exception {

        //Add client
        OAuth2ClientAdd.Response response = OAuth2AbstractTest.addClient(token, username, "clientName", "clientCallback");

        logout(token, session);

        //Check list of clients no more accessible
        OAuth2ClientsList.Request request1 = new OAuth2ClientsList.Request(token, username);
        assertEquals(HttpStatus.UNAUTHORIZED, request1.run(BASE_URL).getStatusLine().getStatusCode());

        //Check info of client no more accessible
        OAuth2ClientInfo.Request request2 = new OAuth2ClientInfo.Request(token, username, new OAuth2Client.Id(response.id));
        assertEquals(HttpStatus.UNAUTHORIZED, request2.run(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        Request request = new Logout.Request(wrongToken, session);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserToken() throws Exception {
        String user2 = username + "2";

        addUser(user2, "pass");
        Login.Response session = login(user2, "pass");

        Request request = new Logout.Request(token, session.id);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}