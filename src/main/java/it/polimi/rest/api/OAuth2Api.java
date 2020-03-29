package it.polimi.rest.api;

import it.polimi.rest.adapters.*;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.*;
import it.polimi.rest.communication.messages.oauth2.OAuth2LoginPage;
import it.polimi.rest.communication.messages.oauth2.accesstoken.OAuth2AccessTokenMessage;
import it.polimi.rest.communication.messages.oauth2.client.OAuth2ClientMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.*;
import it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException;
import it.polimi.rest.exceptions.oauth2.OAuth2UnauthorizedException;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.*;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.utils.Logger;
import it.polimi.rest.utils.Pair;
import spark.Route;

import static it.polimi.rest.exceptions.UnauthorizedException.AuthType.BASIC;
import static it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException.*;

public class OAuth2Api {

    private final Logger logger = new Logger(this.getClass());

    private final AuthorizationProxy proxy;

    private final TokenExtractor tokenHeaderExtractor = new TokenHeaderExtractor();
    private final TokenExtractor tokenBodyExtractor = new TokenBodyExtractor();

    private static final int ACCESS_TOKEN_LIFETIME = 3600;

    public OAuth2Api(Authorizer authorizer,
                          SessionsManager sessionsManager,
                          DataProvider dataProvider) {

        this.proxy = new AuthorizationProxy(authorizer, sessionsManager, dataProvider);
    }

    public Route clients(String usernameParam) {
        Deserializer<String> deserializer = request -> request.params(usernameParam);

        Responder.Action<String> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data);
            OAuth2ClientsList clients = dataProvider.oAuth2Clients(user.id);
            return OAuth2ClientMessage.list(clients);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route addClient(String usernameParam) {
        Deserializer<Pair<String, OAuth2Client>> deserializer = request -> {
            OAuth2Client client = new GsonDeserializer<>(OAuth2Client.class).parse(request);
            String username = request.params(usernameParam);
            return new Pair<>(username, client);
        };

        Responder.Action<Pair<String, OAuth2Client>> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            User user = dataProvider.userByUsername(data.first);

            OAuth2Client.Id id = dataProvider.uniqueId(Id::randomizer, OAuth2Client.Id::new);
            OAuth2Client.Secret secret = dataProvider.uniqueId(Id::randomizer, OAuth2Client.Secret::new);

            OAuth2Client oAuthClient = new OAuth2Client(user, id, secret, data.second.name, data.second.callback);
            dataProvider.add(oAuthClient);

            logger.d("OAuth2 client " + oAuthClient + " added");
            return OAuth2ClientMessage.creation(oAuthClient);
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route removeClient(String clientIdParam) {
        Deserializer<OAuth2Client.Id> deserializer = request -> {
            String id = request.params(clientIdParam);
            return new OAuth2Client.Id(id);
        };

        Responder.Action<OAuth2Client.Id> action = (data, token) -> {
            DataProvider dataProvider = proxy.dataProvider(token);
            dataProvider.remove(data);
            return OAuth2ClientMessage.deletion();
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    /**
     * Show the authentication & authorization page.
     */
    public Route authorize() {
        return new Responder<>(null,
                new OAuth2AuthorizationRequestDeserializer(),
                (data, token) -> new OAuth2LoginPage(data));
    }

    public Route grant() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new OAuth2AuthorizationRequestDeserializer();

        Responder.Action<OAuth2AuthorizationRequest> action = (data, token) -> {
            OAuth2Client client = proxy.dataProvider(new Token() {
                @Override
                public TokenId id() {
                    return null;
                }

                @Override
                public Agent agent() {
                    return data.client;
                }

                @Override
                public boolean isValid() {
                    return true;
                }
            }).oAuth2Client(data.client);

            if (!client.callback.equals(data.callback)) {
                // Invalid redirect URI.
                // As stated in the RFC, the user agent must not be redirected to it.
                throw new OAuth2BadRequestException(INVALID_REQUEST, "Redirect URI mismatch", null);
            }

            DataProvider dataProvider = proxy.dataProvider(token);

            try {
                OAuth2AuthorizationCode code = new OAuth2AuthorizationCode(
                        dataProvider.uniqueId(Id::randomizer, OAuth2AuthorizationCode.Id::new),
                        client.id, data.callback, Scope.convert(data.scopes));

                // Store the new authorization token and logout the user
                dataProvider.add(code);
                proxy.sessionsManager(token).remove(token);

                // Redirect to the client callback URL
                String url = client.callback + "?code=" + code.id;

                if (data.state != null) {
                    url += "&state=" + data.state;
                }

                throw new RedirectionException(url);

            } catch (OAuth2BadRequestException e) {
                throw e.redirect(client.callback, data.state);
            }
        };

        return new Responder<>(tokenBodyExtractor, deserializer, action);
    }

    public Route deny() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new OAuth2AuthorizationRequestDeserializer();

        Responder.Action<OAuth2AuthorizationRequest> action = (data, token) -> {
            OAuth2Client client = proxy.dataProvider(new Token() {
                @Override
                public TokenId id() {
                    return null;
                }

                @Override
                public Agent agent() {
                    return data.client;
                }

                @Override
                public boolean isValid() {
                    return true;
                }
            }).oAuth2Client(data.client);

            if (!client.callback.equals(data.callback)) {
                // Invalid redirect URI.
                // As stated in the RFC, the user agent must not be redirected to it.
                throw new OAuth2BadRequestException(INVALID_REQUEST, "Redirect URI mismatch", null);
            }

            throw new OAuth2BadRequestException(ACCESS_DENIED, null, null).redirect(client.callback, data.state);
        };

        return new Responder<>(tokenBodyExtractor, deserializer, action);
    }

    public Route token() {
        Deserializer<OAuth2AccessTokenRequest> deserializer = new OAuth2AccessTokenRequestDeserializer();

        Responder.Action<OAuth2AccessTokenRequest> action = (data, token) -> {
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
            OAuth2Client client;

            try {
                client = dataProvider.oAuth2Client(data.clientId);

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
                        client.owner.id
                );

                proxy.sessionsManager(accessToken).add(accessToken);
                dataProvider.add(accessToken);

                return OAuth2AccessTokenMessage.creation(accessToken);

            } catch (NotFoundException | ForbiddenException e) {
                // Authorization code doesn't exist or was issued to another client
                throw new OAuth2BadRequestException(INVALID_GRANT, null, null);
            }
        };

        return new Responder<>(null, deserializer, action);
    }

}
