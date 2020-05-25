package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageInfoMessage;
import it.polimi.rest.messages.RootMessage;
import it.polimi.rest.messages.UserInfoMessage;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static org.junit.Assert.*;

public class ImageInfoTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private Image.Id id;
    private String title = "title";

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("image.jpg")).getFile());
        id = new Image.Id(addImage(token, username, title, file).id);
    }

    @Test
    public void correctId() throws Exception {
        ImageInfoMessage.Response response = imageInfo(token, username, id);
        assertEquals(response.id, id.toString());
    }

    @Test
    public void correctTitle() throws Exception {
        ImageInfoMessage.Response response = imageInfo(token, username, id);
        assertEquals(response.title, title);
    }

    @Test
    public void validSelfLink() throws Exception {
        ImageInfoMessage.Response response = imageInfo(token, username, id);
        assertNotNull(response.selfLink());
    }

    @Test
    public void correctOwner() throws Exception {
        ImageInfoMessage.Response response = imageInfo(token, username, id);
        UserInfoMessage.Response userInfo = userInfo(token, username);

        assertEquals(userInfo.selfLink(), response.authorLink());
    }

    @Test
    public void missingToken() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, null, id);

        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, invalidToken, id);

        assertEquals(HttpStatus.UNAUTHORIZED, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserToken() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, token2, id);

        assertEquals(HttpStatus.FORBIDDEN, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void wrongUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token2, user2).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, token, id);

        assertEquals(HttpStatus.NOT_FOUND, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserImage() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token2, user2).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, token2, id);

        assertEquals(HttpStatus.FORBIDDEN, request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

}
