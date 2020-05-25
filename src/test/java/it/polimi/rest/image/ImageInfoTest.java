package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageInfo;
import it.polimi.rest.messages.ImagesList;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UserInfo;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImageInfoTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private int count = 10;
    private File file = new File(getClass().getClassLoader().getResource("image.jpg").getFile());
    private Root.Response rootLinks;
    private ArrayList<Image.Id> image = new ArrayList<Image.Id>();

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);
        rootLinks = new Root.Request().response(BASE_URL);
        for (int i = 0; i < count; i++) {
            image.add(new Image.Id(addImage(token, username, "title" + i, file).id));
        }
    }

    @Test
    public void correctIdRetrieved() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        for (int i = 0; i < count; i++) {
            ImageInfo.Response response = new ImageInfo.Request(userInfo, token, image.get(i)).response(BASE_URL);

            assertEquals(response.id,image.get(i).toString());
        }
    }

    @Test
    public void correctTitleRetrieved() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        for (int i = 0; i < count; i++) {
            ImageInfo.Response response = new ImageInfo.Request(userInfo, token, image.get(i)).response(BASE_URL);

            assertEquals(response.title, "title"+i);
        }
    }

    @Test
    public void missingToken() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfo.Request request = new ImageInfo.Request(userInfo, null, image.get(0));

        assertEquals(HttpStatus.UNAUTHORIZED,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfo.Request request = new ImageInfo.Request(userInfo, new TokenId("fakeToken"), image.get(0));

        assertEquals(HttpStatus.UNAUTHORIZED,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserToken() throws Exception {
        addUser("user2", "pass2");
        TokenId token2 = new TokenId(login("user2", "pass2").id);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token2, image.get(0));

        assertEquals(HttpStatus.FORBIDDEN,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void invalidUserInfo() throws Exception {
        addUser("user2", "pass2");
        TokenId token2 = new TokenId(login("user2", "pass2").id);
        Root.Response rootLinks2 = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks2, token2, "user2").response(BASE_URL);
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token, image.get(0));

        assertEquals(HttpStatus.NOT_FOUND,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserImage() throws Exception {
        addUser("user2", "pass2");
        TokenId token2 = new TokenId(login("user2", "pass2").id);
        Root.Response rootLinks2 = new Root.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks2, token2, "user2").response(BASE_URL);
        ImageInfo.Request request = new ImageInfo.Request(userInfo, token2, image.get(0));

        assertEquals(HttpStatus.FORBIDDEN,request.rawResponse(BASE_URL).getStatusLine().getStatusCode());
    }

}
