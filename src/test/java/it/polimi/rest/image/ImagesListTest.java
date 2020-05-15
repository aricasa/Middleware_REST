package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImagesList;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ImagesListTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private int count = 10;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());

        for (int i = 0; i < count; i++) {
            addImage(token, username, "title" + i, file);
        }
    }

    @Test
    public void response() throws Exception {
        ImagesList.Response response = imagesList(token, username);
        assertEquals(count, Integer.valueOf(response.count).intValue());
    }

    @Test
    public void missingToken() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(token, username).response(BASE_URL);
        ImagesList.Request request = new ImagesList.Request(userInfo,null);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        UserInfo.Response userInfo = new UserInfo.Request(token, username).response(BASE_URL);
        ImagesList.Request request = new ImagesList.Request(userInfo, wrongToken);
        HttpResponse response = request.rawResponse(BASE_URL);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

}
