package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageRaw;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.*;

public class ImageRawTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
    private String title = "title";
    private Image.Id image;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        image = new Image.Id(addImage(token, username, title, file).id);
    }

    @Test
    public void valid() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageRaw.Request request = new ImageRaw.Request(userInfo, token, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        ByteArrayOutputStream downloadedImg = new ByteArrayOutputStream();
        response.getEntity().writeTo(downloadedImg);
        byte[] bufferDownloadedImage = downloadedImg.toByteArray();
        byte[] bufferImage = FileUtils.readFileToByteArray(file);

        assertArrayEquals(bufferDownloadedImage, bufferImage);
    }

    @Test
    public void missingToken() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageRaw.Request request = new ImageRaw.Request(userInfo, null, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageRaw.Request request = new ImageRaw.Request(userInfo, invalidToken, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserImage() throws Exception {
        String user2 = username + "2";
        String pass2 = "pass";

        addUser(user2, pass2);
        TokenId token2 = new TokenId(login(user2, pass2).id);

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageRaw.Request request = new ImageRaw.Request(userInfo, token2, image);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}