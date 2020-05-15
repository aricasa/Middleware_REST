package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageRaw;
import it.polimi.rest.messages.ImageRemove;
import it.polimi.rest.messages.ImageInfo;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImageRemoveTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private String title = "title";
    private File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
    private Image.Id image;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        image = new Image.Id(addImage(token, username, title, file).id);
    }

    @Test
    public void response() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(token, username).response(BASE_URL);
        ImageRemove.Request request = new ImageRemove.Request(userInfo, token, image);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void info() throws Exception {
        removeImage(token, username, image);

        ImageInfo.Request request = new ImageInfo.Request(token, username, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void raw() throws Exception {
        removeImage(token, username, image);
        UserInfo.Response userInfo = new UserInfo.Request(token, username).response(BASE_URL);
        ImageRaw.Request request = new ImageRaw.Request(userInfo, token, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        UserInfo.Response userInfo = new UserInfo.Request(token, username).response(BASE_URL);
        ImageRemove.Request request = new ImageRemove.Request(userInfo, wrongToken, image);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserImage() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);
        UserInfo.Response userInfo = new UserInfo.Request(token, username).response(BASE_URL);
        ImageRemove.Request request = new ImageRemove.Request(userInfo, token2, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}