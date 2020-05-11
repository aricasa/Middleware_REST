package it.polimi.rest.oauth2;

import static org.junit.Assert.assertEquals;

public class OAuth2DenyTest extends OAuth2AbstractTest {
    // TODO: adapt to tests general structure

    /*
    private TokenId idSession;
    private String clientId;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;

        //Deny request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/deny")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .addParameter("token",idSession.toString())
                .build();

        assertEquals(HttpStatus.FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void incorrectToken() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;

        //Deny request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/deny")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .addParameter("token","fakeToken")
                .build();

        assertEquals(HttpStatus.FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void mismatchingURL() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;

        //Deny request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/deny")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", "differentUrl")
                .addParameter("token",idSession.toString())
                .build();

        assertEquals(HttpStatus.BAD_REQUEST,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void fakeClient() throws IOException, InterruptedException
    {
        //Deny request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/deny")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id","myIdd")
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", "myUrl")
                .addParameter("token","token")
                .build();

        assertEquals(HttpStatus.NOT_FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void missingToken() throws IOException, InterruptedException
    {
        //Add and login a user
        String username = "user";
        String password = "pass";
        addUser(username,password);
        idSession = loginUser(username,password);

        //Add a client
        String clientName = "IamAclient";
        String callback = "myUrl";
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;

        //Deny request
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/deny")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",clientId)
                .addParameter("response_type","code")
                .addParameter("scope","scope1")
                .addParameter("redirect_uri", callback)
                .build();

        assertEquals(HttpStatus.FOUND,client.execute(request).getStatusLine().getStatusCode());
    }

     */


}