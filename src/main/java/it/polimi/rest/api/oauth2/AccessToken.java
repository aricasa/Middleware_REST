package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.accesstoken.OAuth2AccessTokenMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException;
import it.polimi.rest.exceptions.oauth2.OAuth2Exception;
import it.polimi.rest.exceptions.oauth2.OAuth2UnauthorizedException;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AccessToken;
import it.polimi.rest.models.oauth2.OAuth2AccessTokenRequest;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.RequestUtils;
import spark.Request;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;
import static it.polimi.rest.exceptions.oauth2.OAuth2Exception.INVALID_CLIENT;
import static it.polimi.rest.exceptions.oauth2.OAuth2Exception.INVALID_GRANT;

class AccessToken extends Responder<TokenId, OAuth2AccessTokenRequest> {

    private final AuthorizationProxy proxy;

    /** OAuth2 access token lifetime (in seconds) */
    private static final int ACCESS_TOKEN_LIFETIME = 3600;

    public AccessToken(AuthorizationProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected OAuth2AccessTokenRequest deserialize(Request request) {
        Map<String, String> bodyParams = RequestUtils.bodyParams(request);

        String grantType = bodyParams.get("grant_type");

        OAuth2Client.Id clientIdHeader = Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Basic"))
                .map(encoded -> encoded.substring("Basic".length()).trim())
                .map(encoded -> new String(Base64.getDecoder().decode(encoded)))
                .map(decoded -> decoded.split(":"))
                .filter(credentials -> credentials.length == 2)
                .map(credentials -> credentials[0])
                .map(OAuth2Client.Id::new)
                .orElse(null);

        OAuth2Client.Id clientIdBody = Optional.ofNullable(bodyParams.get("client_id"))
                .map(OAuth2Client.Id::new)
                .orElseThrow(() -> new OAuth2BadRequestException(OAuth2Exception.INVALID_REQUEST, null, null));

        if (clientIdHeader != null && !clientIdHeader.equals(clientIdBody)) {
            // Client ID used in basic authentication doesn't match with the one specified in the request body
            throw new OAuth2BadRequestException(OAuth2Exception.INVALID_REQUEST, null, null);
        }

        OAuth2Client.Secret clientSecretHeader = Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Basic"))
                .map(encoded -> encoded.substring("Basic".length()).trim())
                .map(encoded -> new String(Base64.getDecoder().decode(encoded)))
                .map(decoded -> decoded.split(":"))
                .filter(credentials -> credentials.length == 2)
                .map(credentials -> credentials[1])
                .map(OAuth2Client.Secret::new)
                .orElse(null);

        OAuth2Client.Secret clientSecretBody = Optional.ofNullable(bodyParams.get("client_secret"))
                        .map(OAuth2Client.Secret::new)
                        .orElse(null);

        if (clientSecretHeader == null && clientSecretBody == null) {
            // Client secret missing
            throw new OAuth2BadRequestException(OAuth2Exception.INVALID_CLIENT, null, null);
        }

        if (clientSecretHeader != null && clientSecretBody != null) {
            // Multiple client secrets specified
            throw new OAuth2BadRequestException(OAuth2Exception.INVALID_REQUEST, null, null);
        }

        boolean basicAuthentication = clientSecretHeader != null;

        OAuth2Client.Secret clientSecret = basicAuthentication ? clientSecretHeader : clientSecretBody;

        String redirectUri = Optional.ofNullable(bodyParams.get("redirect_uri"))
                .orElseThrow(() -> new OAuth2BadRequestException(OAuth2BadRequestException.INVALID_REQUEST, null, null));

        OAuth2AuthorizationCode.Id code = Optional.ofNullable(bodyParams.get("code"))
                .map(OAuth2AuthorizationCode.Id::new)
                .orElseThrow(() -> new OAuth2BadRequestException(OAuth2BadRequestException.INVALID_REQUEST, null, null));

        return new OAuth2AccessTokenRequest(grantType, clientIdBody, clientSecret, basicAuthentication, redirectUri, code);
    }

    @Override
    protected Message process(TokenId token, OAuth2AccessTokenRequest data) {
        Token fakeToken = new Token() {
            @Override
            public TokenId id() {
                return null;
            }

            @Override
            public Agent agent() {
                return data.clientId;
            }

            @Override
            public boolean isValid() {
                return true;
            }
        };

        DataProvider dataProvider = proxy.dataProvider(fakeToken);

        try {
            OAuth2Client client = dataProvider.oAuth2Client(data.clientId);

            if (!client.secret.equals(data.clientSecret)) {
                // Wrong secret
                throw new OAuth2UnauthorizedException(BASIC, INVALID_CLIENT, null, null);
            }

        } catch (NotFoundException | ForbiddenException e) {
            // Client doesn't exist
            throw new OAuth2UnauthorizedException(BASIC, INVALID_CLIENT, null, null);
        }

        try {
            OAuth2AuthorizationCode code = dataProvider.oAuth2AuthCode(data.code);

            if (!code.isValid()) {
                // The authorization code has expired
                dataProvider.remove(code.id);
                throw new OAuth2BadRequestException(INVALID_GRANT, null, null);
            }

            if (!code.redirectUri.equals(data.redirectUri)) {
                // The redirect URI must match the one the authorization code was issued to
                throw new OAuth2BadRequestException(INVALID_GRANT, null, null);
            }

            dataProvider.remove(code.id);

            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    dataProvider.uniqueId(Id::randomizer, OAuth2AccessToken.Id::new),
                    ACCESS_TOKEN_LIFETIME,
                    code.user,
                    code.scope
            );

            proxy.sessionsManager(accessToken).add(accessToken);
            dataProvider.add(accessToken);

            return OAuth2AccessTokenMessage.creation(accessToken);

        } catch (NotFoundException | ForbiddenException e) {
            // Authorization code doesn't exist or was issued to another client
            throw new OAuth2BadRequestException(INVALID_GRANT, null, null);
        }
    }

}
