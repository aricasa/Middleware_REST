package it.polimi.rest.image;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.ImageAdd;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import static org.junit.Assert.assertEquals;

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
    public void valid() throws Exception {
        ImageAdd.Response response = addImage(token, username, title, file);
        assertEquals(title, response.title);
    }

    @Test
    public void wrongUser() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");

        ImageAdd.Request request = new ImageAdd.Request(token, user2, title, file);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN,response.getStatusLine().getStatusCode());
    }

    @Test
    public void wrongToken() throws Exception {
        TokenId wrongToken = new TokenId(token + "wrongToken");
        ImageAdd.Request request = new ImageAdd.Request(wrongToken, username, title, file);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws Exception {
        ImageAdd.Request request = new ImageAdd.Request(null, username, title, file);
        HttpResponse response = request.run(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusLine().getStatusCode());
    }

}