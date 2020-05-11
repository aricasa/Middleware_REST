package it.polimi.rest.oauth2;

import it.polimi.rest.AbstractTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO: not clear what this is
public class OauthGetTest extends AbstractTest
{
    /*
    private TokenId idSession;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        String clientName = "IamAclient";
        String callback = "myUrl";

        Request body = new OauthClientAdd.Request(clientName,callback);

        HttpUriRequest request = RequestBuilder
                .get(BASE_URL + "/users/" + username + "/oauth2/clients")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession.toString())
                .setEntity(body.jsonEntity())
                .build();

        assertEquals(HttpStatus.OK,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectTokeh() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        addUser(username,password);
        idSession = loginUser(username,password);

        String clientName = "IamAclient";
        String callback = "myUrl";

        Request body = new OauthClientAdd.Request(clientName,callback);

        HttpUriRequest request = RequestBuilder
                .get(BASE_URL + "/users/" + username + "/oauth2/clients")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+"fakeToken")
                .setEntity(body.jsonEntity())
                .build();

        assertEquals(HttpStatus.UNAUTHORIZED,client.execute(request).getStatusLine().getStatusCode());
    }

     */
}