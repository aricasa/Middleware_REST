package it.polimi.rest.user;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserLogoutTest extends AbstractTest {

    private String username = "user";
    private TokenId token;
    private String session;

    @Before
    public void setUp() throws Exception {
        addUser(username, "pass");
        session = login(username, "pass").id;
        token = new TokenId(session);
    }

    @Test
    public void response() throws Exception {
        RootMessage.Response rootLinks = rootLinks();
        LogoutMessage.Request request = new LogoutMessage.Request(rootLinks, token, session);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusLine().getStatusCode());
    }

    @Test
    public void userInfoNotAccessibleAnymore() throws Exception {
        logout(token, session);

        RootMessage.Response rootLinks = rootLinks();
        UserInfoMessage.Request request = new UserInfoMessage.Request(rootLinks, token, username);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void imagesNotAccessibleAnymore() throws Exception {
        logout(token, session);

        UserInfoMessage.Response userInfo = userInfo(token, username);
        ImagesListMessage.Request request = new ImagesListMessage.Request(userInfo, token);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void oAuth2ClientsNotAccessibleAnymore() throws Exception {
        logout(token, session);

        UserInfoMessage.Response userInfo = userInfo(token, username);
        OAuth2ClientsListMessage.Request request = new OAuth2ClientsListMessage.Request(userInfo, token);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void invalidToken() throws Exception {
        TokenId invalidToken = new TokenId(token + "invalidToken");

        RootMessage.Response rootLinks = rootLinks();
        LogoutMessage.Request request = new LogoutMessage.Request(rootLinks, invalidToken, session);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    @Test
    public void otherUserToken() throws Exception {
        String user2 = username + "2";
        addUser(user2, "pass");
        LoginMessage.Response session = login(user2, "pass");

        RootMessage.Response rootLinks = rootLinks();
        LogoutMessage.Request request = new LogoutMessage.Request(rootLinks, token, session.id);
        HttpResponse response = request.rawResponse(BASE_URL);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusLine().getStatusCode());
    }

}