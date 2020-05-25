package it.polimi.rest.oauth2;

import it.polimi.rest.AbstractTest;
import it.polimi.rest.messages.*;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2RefreshToken;
import it.polimi.rest.models.oauth2.scope.Scope;

import java.io.IOException;
import java.util.Collection;

public abstract class OAuth2AbstractTest extends AbstractTest {

    protected OAuth2ClientsListMessage.Response clientsList(TokenId token, String username) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientsListMessage.Request request = new OAuth2ClientsListMessage.Request(userInfo, token);
        return request.response(BASE_URL);
    }

    protected OAuth2ClientInfoMessage.Response clientInfo(TokenId token, String username, OAuth2Client.Id client) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientInfoMessage.Request request = new OAuth2ClientInfoMessage.Request(userInfo, token, client);
        return request.response(BASE_URL);
    }

    protected OAuth2ClientAddMessage.Response addClient(TokenId token, String username, String name, String callback) throws IOException {
        RootMessage.Response rootLinks = new RootMessage.Request().response(BASE_URL);
        UserInfoMessage.Response userInfo = new UserInfoMessage.Request(rootLinks, token, username).response(BASE_URL);
        OAuth2ClientAddMessage.Request request = new OAuth2ClientAddMessage.Request(userInfo, token, name, callback);
        return request.response(BASE_URL);
    }

    protected OAuth2GrantMessage.Response authCode(TokenId token, OAuth2Client.Id clientId, String callback, Collection<Scope> scopes, String state) throws IOException {
        OAuth2GrantMessage.Request request = new OAuth2GrantMessage.Request(token, clientId, callback, scopes, state);
        return request.response(BASE_URL);
    }

    protected OAuth2DenyMessage.Response deny(OAuth2Client.Id clientId, String callback, Collection<Scope> scopes, String state) throws IOException {
        OAuth2DenyMessage.Request request = new OAuth2DenyMessage.Request(clientId, callback, scopes, state);
        return request.response(BASE_URL);
    }

    protected OAuth2AccessTokenMessage.Response accessToken(OAuth2Client.Id clientId, OAuth2Client.Secret secret, String callback, OAuth2AuthorizationCode.Id authCode) throws IOException {
       OAuth2AccessTokenMessage.Request request = new OAuth2AccessTokenMessage.Request(clientId, secret, callback, authCode, "authorization_code");
       return request.response(BASE_URL);
   }

   protected OAuth2RefreshTokenMessage.Response refreshToken(OAuth2Client.Id id, OAuth2Client.Secret secret, OAuth2RefreshToken.Id refreshToken) throws IOException {
        OAuth2RefreshTokenMessage.Request request = new OAuth2RefreshTokenMessage.Request(id, secret, "refresh_token", refreshToken);
        return request.response(BASE_URL);
   }

}
