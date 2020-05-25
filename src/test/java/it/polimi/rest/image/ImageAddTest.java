package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageAddMessage;
import it.polimi.rest.messages.RootMessage;
import it.polimi.rest.messages.UserInfoMessage;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Objects;

import static org.junit.Assert.*;

public class ImageAddTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private String title = "title";
    private File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("image.jpg")).getFile());

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
    }

    @Test
    public void validId() throws Exception {
        ImageAddMessage.Response response = addImage(token, username, title, file);

        assertNotNull(response.id);
        assertFalse(response.id.trim().isEmpty());
    }

    @Test
    public void correctTitle() throws Exception {
        ImageAddMessage.Response response = addImage(token, username, title, file);
        assertEquals(title, response.title);
    }

    @Test
    public void validSelfLink() throws Exception {
        ImageAddMessage.Response response = addImage(token, username, title, file);
        assertNotNull(response.selfLink());
    }

    @Test
    public void correctOwnerLink() throws Exception {
        ImageAddMessage.Response response = addImage(token, username, title, file);
        UserInfoMessage.Response userInfo = userInfo(token, username);

        assertEquals(userInfo.selfLink(), response.authorLink());
    }

    @Test
    public void missingToken() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageAddMessage.Request request = new ImageAddMessage.Request(userInfo, null, title, file);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId("invalidToken");

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageAddMessage.Request request = new ImageAddMessage.Request(userInfo, invalidToken, title, file);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageAddMessage.Request request = new ImageAddMessage.Request(userInfo, token2, title, file);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());
    }

}