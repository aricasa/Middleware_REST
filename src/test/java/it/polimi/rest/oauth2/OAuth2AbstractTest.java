package it.polimi.rest.oauth2;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import it.polimi.rest.utils.RequestUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public abstract class OAuth2AbstractTest extends AbstractTest {

    public static OAuth2ClientsList.Response clientsList(TokenId token, String username) throws IOException {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientsList.Request request = new OAuth2ClientsList.Request(userInfo, token);
        return parseJson(request.rawResponse(BASE_URL), OAuth2ClientsList.Response.class);
    }

    public static OAuth2ClientInfo.Response clientInfo(TokenId token, String username, OAuth2Client.Id client) throws IOException {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientInfo.Request request = new OAuth2ClientInfo.Request(userInfo, token, client);
        return parseJson(request.rawResponse(BASE_URL), OAuth2ClientInfo.Response.class);
    }

    public static OAuth2ClientAdd.Response addClient(TokenId token, String username, String name, String callback) throws IOException {
        RootLinks.Response rootLinks = new RootLinks.Request().response(BASE_URL);
        UserInfo.Response userInfo = new UserInfo.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientAdd.Request request = new OAuth2ClientAdd.Request(userInfo, token, name, callback);
        return parseJson(request.rawResponse(BASE_URL), OAuth2ClientAdd.Response.class);
    }

    public static OAuth2Grant.Response authCode(TokenId token, OAuth2Client.Id clientId, String callback, Collection<Scope> scopes, String state) throws IOException {
        OAuth2Grant.Request request = new OAuth2Grant.Request(token, clientId, callback, scopes, state);
        HttpResponse response = request.rawResponse(BASE_URL);

        String url = response.getFirstHeader("Location").getValue();
        Map<String, String> params = RequestUtils.bodyParams(url.substring(callback.length() + 1));

        return new OAuth2Grant.Response(params.get("code"), params.get("state"));
    }

   public static OAuth2AccessToken.Response getAccessToken(OAuth2Client.Id clientId, OAuth2Client.Secret secret, String callback, String code, String grantType) throws IOException {
       OAuth2AccessToken.Request request = new OAuth2AccessToken.Request(clientId, secret, callback, code, grantType);
       return  parseJson(request.rawResponse(BASE_URL), OAuth2AccessToken.Response.class);
   }

}
