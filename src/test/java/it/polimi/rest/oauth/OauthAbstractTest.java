package it.polimi.rest.oauth;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.messages.OauthClientAdd;
import it.polimi.rest.messages.Request;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class OauthAbstractTest extends AbstractTest {

    public OauthClientAdd.Response addClient(String username, String idSession, String clientName, String callback) throws IOException {
        Request body = new OauthClientAdd.Request(clientName,callback);

        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/users/" + username + "/oauth2/clients")
                .setHeader(HttpHeaders.AUTHORIZATION,"Bearer"+idSession)
                .setEntity(body.jsonEntity())
                .build();

        return parseJson(client.execute(request), OauthClientAdd.Response.class);
    }

    public void sendAuthorizeRequest(String client_id, String response_type, Collection<String> scope, String state, String redirect_uri) throws IOException {
        HttpUriRequest request = RequestBuilder
                .get(BASE_URL + "/oauth2/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri+"&state="+state+"&scope="+scope)
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .build();

        client.execute(request);
    }

    public HttpResponse sendGrantRequest(String client_id, String callback, String idSession, String scope) throws IOException {
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/grant")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",client_id)
                .addParameter("response_type","code")
                .addParameter("scope",scope)
                .addParameter("redirect_uri", callback)
                .addParameter("token",idSession)
                .build();

        return client.execute(request);
    }

    public HttpResponse sendDenyRequest(String client_id, String callback, String idSession, String scope) throws IOException {
        HttpUriRequest request = RequestBuilder
                .post(BASE_URL + "/oauth2/deny")
                .setHeader("Content-Type","application/x-www-form-urlencoded")
                .addParameter("client_id",client_id)
                .addParameter("response_type","code")
                .addParameter("scope",scope)
                .addParameter("redirect_uri", callback)
                .addParameter("token",idSession)
                .build();

        return client.execute(request);
    }



}
