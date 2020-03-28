package it.polimi.rest.api;

import it.polimi.rest.adapters.*;
import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.AuthorizationProxy;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.*;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.OAuth2LoginPage;
import it.polimi.rest.communication.messages.oauth2.client.OAuth2ClientMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.OAuth2Exception;
import it.polimi.rest.exceptions.RedirectionException;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.*;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.utils.Logger;
import it.polimi.rest.utils.Pair;
import spark.Request;
import spark.Route;

public class OAuth2Api {

    private final Logger logger = new Logger(this.getClass());

    private final AuthorizationProxy proxy;

    private final TokenExtractor tokenHeaderExtractor = new TokenHeaderExtractor();
    private final TokenExtractor tokenBodyExtractor = new TokenBodyExtractor();

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

    public Route authorize() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new OAuth2AuthRequestDeserializer();
        Responder.Action<OAuth2AuthorizationRequest> action = (data, token) -> new OAuth2LoginPage(data);

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

    public Route grant() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new OAuth2AuthActionDeserializer();

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
                throw new BadRequestException("Redirect URI mismatch");
            }

            DataProvider dataProvider = proxy.dataProvider(token);

            try {
                OAuth2AuthorizationCode code = new OAuth2AuthorizationCode(
                        dataProvider.uniqueId(Id::randomizer, OAuth2AuthorizationCode.Id::new),
                        client.id, Scope.convert(data.scopes));

                dataProvider.add(code);
                proxy.sessionsManager(token).remove(token);

                String url = client.callback + "?code=" + code.id;

                if (data.state != null) {
                    url += "&state=" + data.state;
                }

                throw new RedirectionException(HttpStatus.CREATED, url);

            } catch (OAuth2Exception e) {
                String url = e.url(client.callback, data.state);
                throw new RedirectionException(HttpStatus.BAD_REQUEST, url);
            }
        };

        return new Responder<>(tokenBodyExtractor, deserializer, action);
    }

    public Route deny() {
        Deserializer<OAuth2AuthorizationRequest> deserializer = new OAuth2AuthActionDeserializer();

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
                throw new BadRequestException("Redirect URI mismatch");
            }

            try {
                throw new  OAuth2Exception(OAuth2Exception.ACCESS_DENIED, null, null);

            } catch (OAuth2Exception e) {
                String url = e.url(client.callback, data.state);
                throw new RedirectionException(HttpStatus.FOUND, url);
            }
        };

        return new Responder<>(tokenBodyExtractor, deserializer, action);
    }

    public Route token() {
        Deserializer<String> deserializer = new Deserializer<String>() {
            @Override
            public String parse(Request request) {
                String body = request.body();
                return null;
            }
        };

        Responder.Action<String> action = new Responder.Action<String>() {
            @Override
            public Message run(String data, TokenId token) {
                return null;
            }
        };

        return new Responder<>(tokenHeaderExtractor, deserializer, action);
    }

}
