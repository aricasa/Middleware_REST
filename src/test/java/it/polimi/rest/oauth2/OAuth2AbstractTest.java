package it.polimi.rest.oauth2;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.messages.OAuth2ClientAdd;
import it.polimi.rest.messages.OAuth2ClientInfo;
import it.polimi.rest.messages.OAuth2ClientsList;
import it.polimi.rest.messages.OAuth2Grant;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import it.polimi.rest.utils.RequestUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

abstract class OAuth2AbstractTest extends AbstractTest {

    public static OAuth2ClientsList.Response clientsList(TokenId token, String username) throws IOException {
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(token, username);
        return parseJson(request.run(BASE_URL), OAuth2ClientsList.Response.class);
    }

    public static OAuth2ClientInfo.Response clientInfo(TokenId token, String username, OAuth2Client.Id client) throws IOException {
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(token, username, client);
        return parseJson(request.run(BASE_URL), OAuth2ClientInfo.Response.class);
    }

    public static OAuth2ClientAdd.Response addClient(TokenId token, String username, String name, String callback) throws IOException {
        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(token, username, name, callback);
        return parseJson(request.run(BASE_URL), OAuth2ClientAdd.Response.class);
    }

    public static OAuth2Grant.Response authCode(TokenId token, OAuth2Client.Id clientId, String callback, Collection<Scope> scopes, String state) throws IOException {
        OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, callback, scopes, state);
        HttpResponse response = request.run(BASE_URL);

        String url = response.getFirstHeader("Location").getValue();
        Map<String, String> params = RequestUtils.bodyParams(url.substring(callback.length() + 1));

        return new OAuth2Grant.Response(params.get("code"), params.get("state"));
    }

    /*

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

     */

}
