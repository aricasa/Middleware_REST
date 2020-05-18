package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.Root;
import it.polimi.rest.messages.UsersList;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UsersListTest extends AbstractTest {

    private int count = 10;
    private TokenId token;

    @Before
    public void setUp() throws Exception {
        String username = "user";

        addUser(username, "pass");
        token = new TokenId(login(username, "pass").id);

        for (int i = 1; i < count; i++) {
            String user = username + i;
            addUser(user, "pass");
        }
    }

    @Test
    public void usersCount() throws Exception {
        UsersList.Response response = usersList(token);
        assertEquals(count, Integer.valueOf(response.count).intValue());
    }

    @Test
    public void missingToken() throws Exception {
        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UsersList.Request request = new UsersList.Request(rootLinks,null);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        Root.Response rootLinks = new Root.Request().response(BASE_URL);
        UsersList.Request request = new UsersList.Request(rootLinks, invalidToken);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

}