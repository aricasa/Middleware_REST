package it.polimi.rest.api.oauth2;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.authorization.Token;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenBodyExtractor;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.exceptions.RedirectionException;
import it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException;
import it.polimi.rest.models.BasicToken;
import it.polimi.rest.models.Id;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.User;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationRequest;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import it.polimi.rest.utils.RequestUtils;
import spark.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.rest.exceptions.oauth2.OAuth2Exception.INVALID_REQUEST;

/**
 * Grant the access to the resources.
 */
public class Grant extends Responder<BasicToken.Id, OAuth2AuthorizationRequest> {

    protected final SessionManager sessionManager;

    public Grant(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<BasicToken.Id>> tokenExtractor() {
        return Optional.of(new TokenBodyExtractor<>(BasicToken.Id::new));
    }

    @Override
    protected OAuth2AuthorizationRequest deserialize(Request request) {
        Map<String, String> params = RequestUtils.bodyParams(request);

        String responseType = params.get("response_type");

        OAuth2Client.Id clientId = Optional.ofNullable(params.get("client_id"))
                .map(OAuth2Client.Id::new)
                .orElseThrow(() -> new BadRequestException("Client ID not specified"));

        String redirectUri = Optional.ofNullable(params.get("redirect_uri"))
                .orElseThrow(() -> new BadRequestException("Redirect URI not specified"));

        Collection<String> scope = Optional.ofNullable(params.get("scope"))
                .map(scopes -> scopes.split(" "))
                .map(Stream::of)
                .map(stream -> stream.collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        String state = params.get("state");

        return new OAuth2AuthorizationRequest(responseType, clientId, redirectUri, scope, state);
    }

    @Override
    protected Message process(BasicToken.Id token, OAuth2AuthorizationRequest data) {
        OAuth2Client client = sessionManager.dataProvider(new Token() {
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

        DataProvider dataProvider = sessionManager.dataProvider(token);
        User.Id user;

        try {
            BasicToken basicToken = dataProvider.basicToken(token);
            user = basicToken.user;

        } catch (NotFoundException | ForbiddenException e) {
            throw new OAuth2BadRequestException(INVALID_REQUEST, "Invalid session token", null).redirect(client.callback, data.state);
        }

        try {
            OAuth2AuthorizationCode code = new OAuth2AuthorizationCode(
                    dataProvider.uniqueId(Id::randomizer, OAuth2AuthorizationCode.Id::new),
                    client.id,
                    data.callback,
                    Scope.convert(data.scopes),
                    user
            );

            // Store the new authorization token and logout the user
            dataProvider.add(code);
            sessionManager.remove(token);

            // Redirect to the client callback URL
            String url = client.callback + "?code=" + code.id;

            if (data.state != null) {
                url += "&state=" + data.state;
            }

            throw new RedirectionException(url);

        } catch (OAuth2BadRequestException e) {
            throw e.redirect(client.callback, data.state);
        }
    }

}
