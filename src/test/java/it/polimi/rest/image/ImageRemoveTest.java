package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class ImageRemoveTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private Image.Id image;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("image.jpg")).getFile());
        image = new Image.Id(addImage(token, username, "title", file).id);
    }

    @Test
    public void response() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRemoveMessage.Request request = new ImageRemoveMessage.Request(userInfo, token, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void infoNotAccessibleAnymore() throws Exception {
        removeImage(token, username, image);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfoMessage.Request request = new ImageInfoMessage.Request(userInfo, token,  image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void rawDataNotAccessibleAnymore() throws Exception {
        removeImage(token, username, image);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRawMessage.Request request = new ImageRawMessage.Request(userInfo, token, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws Exception {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRemoveMessage.Request request = new ImageRemoveMessage.Request(userInfo, null, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRemoveMessage.Request request = new ImageRemoveMessage.Request(userInfo, invalidToken, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserImage() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        TokenId token2 = new TokenId(login(user2, "pass").id);

        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        ImageRemoveMessage.Request request = new ImageRemoveMessage.Request(userInfo, token2, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}