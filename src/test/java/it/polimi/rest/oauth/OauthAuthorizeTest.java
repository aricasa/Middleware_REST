package it.polimi.rest.oauth;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.messages.OauthAuthorize;
import it.polimi.rest.messages.OauthClientAdd;
import it.polimi.rest.messages.Request;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


public class OauthAuthorizeTest extends OauthAbstractTest
{
    private TokenId idSession;
    private String clientId;
    private String clientSecret;

    @Test
    public void valid() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String clientName = "IamAclient";
        String callback = "myUrl";

        addUser(username,password);
        idSession = loginUser(username,password);
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        Collection<String> scope= new ArrayList<String>();
        scope.add("scope1");
        String state = "myState";
        String redirectUri = "myUri";
        String client_id = "myClient";

        HttpUriRequest request = RequestBuilder
                .get(BASE_URL + "/oauth2/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirectUri+"&state="+state+"&scope="+scope)
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        assertEquals(HttpStatus.OK,client.execute(request).getStatusLine().getStatusCode());
    }

    @Test
    public void notRegisteredClient() throws IOException, InterruptedException
    {
        String username = "user";
        String password = "pass";

        String clientName = "IamAclient";
        String callback = "myUrl";

        addUser(username,password);
        idSession = loginUser(username,password);
        OauthClientAdd.Response clientInfo = addClient(username,idSession.toString(),clientName,callback);
        clientId = clientInfo.id;
        clientSecret = clientInfo.secret;

        Collection<String> scope= new ArrayList<String>();
        scope.add("scope1");
        String state = "myState";
        String redirectUri = "myUri";
        String client_id = "myClient";

        HttpUriRequest request = RequestBuilder
                .get(BASE_URL + "/oauth2/authorize?response_type=code&client_id=fakeClient&redirect_uri="+redirectUri+"&state="+state+"&scope="+scope)
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        assertEquals(HttpStatus.OK,client.execute(request).getStatusLine().getStatusCode());
    }
}