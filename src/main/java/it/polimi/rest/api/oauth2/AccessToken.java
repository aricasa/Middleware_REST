package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.SessionManager;
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
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2RefreshToken;
import it.polimi.rest.utils.RequestUtils;
import spark.Request;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;
import static it.polimi.rest.exceptions.oauth2.OAuth2Exception.*;

/**
 * Convert an authorization token into an access token.
 */
class AccessToken extends Responder<TokenId, AccessToken.Data> {

    private final SessionManager sessionManager;

    /** OAuth2 access token lifetime (in seconds) */
    private static final int ACCESS_TOKEN_LIFETIME = 3600;

    public AccessToken(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected Data deserialize(Request request) {
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
                .orElse(null);

        if (clientIdHeader == null && clientIdBody == null) {
            // Client ID missing
            throw new OAuth2BadRequestException(INVALID_REQUEST, null, null);

        } else if (clientIdHeader != null && clientIdBody != null && !clientIdHeader.equals(clientIdBody)) {
            // Multiple and different client IDs specified
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

        } else if (clientSecretHeader != null && clientSecretBody != null && !clientSecretHeader.equals(clientSecretBody)) {
            // Multiple and different client secrets specified
            throw new OAuth2BadRequestException(OAuth2Exception.INVALID_REQUEST, null, null);
        }

        boolean basicAuthentication = clientSecretHeader != null;

        OAuth2Client.Id clientId = basicAuthentication ? clientIdHeader : clientIdBody;
        OAuth2Client.Secret clientSecret = basicAuthentication ? clientSecretHeader : clientSecretBody;

        String redirectUri = Optional.ofNullable(bodyParams.get("redirect_uri"))
                .orElse(null);

        OAuth2AuthorizationCode.Id code = Optional.ofNullable(bodyParams.get("code"))
                .map(OAuth2AuthorizationCode.Id::new)
                .orElse(null);

        OAuth2RefreshToken.Id refreshToken = Optional.ofNullable(bodyParams.get("refresh_token"))
                .map(OAuth2RefreshToken.Id::new)
                .orElse(null);

        return new Data(grantType, clientId, clientSecret, basicAuthentication, redirectUri, code, refreshToken);
    }

    @Override
    protected Message process(TokenId token, Data data) {
        try {
            DataProvider dataProvider = sessionManager.dataProvider(data.clientId);
            OAuth2Client client = dataProvider.oAuth2Client(data.clientId);

            if (!client.secret.equals(data.clientSecret)) {
                // Wrong secret
                throw new OAuth2UnauthorizedException(BASIC, INVALID_CLIENT, null, null);
            }

        } catch (NotFoundException | ForbiddenException e) {
            // Client doesn't exist
            throw new OAuth2UnauthorizedException(BASIC, INVALID_CLIENT, null, null);
        }

        if (data.grantType.equals("authorization_code")) {
            return authorizationCode(data);

        } else if (data.grantType.equals("refresh_token")) {
            return refreshToken(data);

        } else {
            throw new OAuth2BadRequestException(UNSUPPORTED_GRANT_TYPE, null, null);
        }
    }

    private Message authorizationCode(Data data) {
        if (data.redirectUri == null) {
            throw new OAuth2BadRequestException(INVALID_REQUEST, null, null);
        }

        if (data.code == null) {
            throw new OAuth2BadRequestException(INVALID_REQUEST, null, null);
        }

        DataProvider dataProvider = sessionManager.dataProvider(data.clientId);

        try {
            OAuth2AuthorizationCode code = dataProvider.oAuth2AuthCode(data.code);

            if (!code.client.equals(data.clientId)) {
                // The authorization code was issued to another client
                throw new OAuth2BadRequestException(INVALID_GRANT, null, null);
            }

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
                    code.client,
                    code.user,
                    code.scope
            );

            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    dataProvider.uniqueId(Id::randomizer, OAuth2RefreshToken.Id::new),
                    accessToken.id,
                    code.client,
                    code.user,
                    code.scope
            );

            accessToken.attach(refreshToken.id);

            dataProvider.add(accessToken);
            dataProvider.add(refreshToken);

            return OAuth2AccessTokenMessage.creation(accessToken);

        } catch (NotFoundException e) {
            // Authorization code doesn't exist
            throw new OAuth2BadRequestException(INVALID_GRANT, null, null);
        }
    }

    private Message refreshToken(Data data) {
        if (data.refreshToken == null) {
            throw new OAuth2BadRequestException(INVALID_REQUEST, null, null);
        }

        DataProvider dataProvider = sessionManager.dataProvider(data.clientId);
        OAuth2RefreshToken refreshToken;

        try {
            refreshToken = dataProvider.oAuth2RefreshToken(data.refreshToken);

        } catch (NotFoundException e) {
            throw new OAuth2BadRequestException(INVALID_REQUEST, null, null);
        }

        try {
            dataProvider.remove(refreshToken.accessToken);
            logger.d("Previous access token " + refreshToken.accessToken + " removed");

        } catch (NotFoundException e) {
            logger.d("Previous access token " + refreshToken.accessToken + " not found. Probably already expired");
        }

        dataProvider.remove(refreshToken.id);

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                dataProvider.uniqueId(Id::randomizer, OAuth2AccessToken.Id::new),
                ACCESS_TOKEN_LIFETIME,
                refreshToken.client,
                refreshToken.user,
                refreshToken.scope
        );

        OAuth2RefreshToken newRefreshToken = new OAuth2RefreshToken(
                dataProvider.uniqueId(Id::randomizer, OAuth2RefreshToken.Id::new),
                accessToken.id,
                refreshToken.client,
                refreshToken.user,
                refreshToken.scope
        );

        accessToken.attach(newRefreshToken.id);

        dataProvider.add(accessToken);
        dataProvider.add(newRefreshToken);

        return OAuth2AccessTokenMessage.creation(accessToken);
    }

    protected static class Data {

        public final String grantType;
        public final OAuth2Client.Id clientId;
        public final OAuth2Client.Secret clientSecret;
        public final boolean basicAuthentication;
        public final String redirectUri;
        public final OAuth2AuthorizationCode.Id code;
        public final OAuth2RefreshToken.Id refreshToken;

        public Data(String grantType,
                    OAuth2Client.Id clientId,
                    OAuth2Client.Secret clientSecret,
                    boolean basicAuthentication,
                    String redirectUri,
                    OAuth2AuthorizationCode.Id code,
                    OAuth2RefreshToken.Id refreshToken) {

            this.grantType = grantType;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.basicAuthentication = basicAuthentication;
            this.redirectUri = redirectUri;
            this.code = code;
            this.refreshToken = refreshToken;
        }

    }

}
