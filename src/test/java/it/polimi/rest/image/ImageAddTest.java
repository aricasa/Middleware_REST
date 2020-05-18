package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageAdd;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import static org.junit.Assert.*;

public class ImageAddTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private String title = "title";
    private File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
    }

    @Test
    public void validIdCreated() throws Exception {
        ImageAdd.Response response = addImage(token, username, title, file);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void correctTitleRegistered() throws Exception {
        ImageAdd.Response response = addImage(token, username, title, file);
        assertEquals(title, response.title);
    }

    @Test
    public void missingToken() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageAdd.Request request = new ImageAdd.Request(userInfo, null, title, file);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId("invalidToken");

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageAdd.Request request = new ImageAdd.Request(userInfo, invalidToken, title, file);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUser() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageAdd.Request request = new ImageAdd.Request(userInfo, token2, title, file);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());
    }

}