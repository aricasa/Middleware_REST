package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageRawMessage;
import it.polimi.rest.messages.RootMessage;
import it.polimi.rest.messages.UserInfoMessage;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static org.junit.Assert.*;

public class ImageRawTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private byte[] data;
    private Image.Id image;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("image.jpg")).getFile());
        image = new Image.Id(addImage(token, username, "title", file).id);
        data = FileUtils.readFileToByteArray(file);
    }

    @Test
    public void sameData() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRawMessage.Request request = new ImageRawMessage.Request(userInfo, token, image);
        ImageRawMessage.Response response = request.response(BASE_URL);

        assertArrayEquals(data, response.data);
    }

    @Test
    public void missingToken() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRawMessage.Request request = new ImageRawMessage.Request(userInfo, null, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRawMessage.Request request = new ImageRawMessage.Request(userInfo, invalidToken, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void inexistentImage() throws Exception {
        Image.Id inexistentId = new Image.Id(image + "inexistentId");

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRawMessage.Request request = new ImageRawMessage.Request(userInfo, token, inexistentId);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserImage() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRawMessage.Request request = new ImageRawMessage.Request(userInfo, token2, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}