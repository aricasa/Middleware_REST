package it.polimi.rest.api.oauth2;

import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.oauth2.OAuth2LoginPage;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationRequest;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.RequestUtils;
import spark.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Show the authentication and authorization page.
 */
class Authorize extends Responder<TokenId, OAuth2AuthorizationRequest> {

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.empty();
    }

    @Override
    protected OAuth2AuthorizationRequest deserialize(Request request) {
        String responseType = Optional.ofNullable(request.queryParams("response_type"))
                .map(RequestUtils::decode)
                .orElse(null);

        OAuth2Client.Id clientId = Optional.ofNullable(request.queryParams("client_id"))
                .map(String::trim)
                .map(RequestUtils::decode)
                .map(OAuth2Client.Id::new)
                .orElse(null);

        String redirectUri = Optional.ofNullable(request.queryParams("redirect_uri"))
                .map(RequestUtils::decode)
                .orElse(null);

        Collection<String> scope = Optional.ofNullable(RequestUtils.decode(request.queryParams("scope")))
                .map(scopes -> scopes.split(" "))
                .map(Stream::of)
                .map(stream -> stream.collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        String state = Optional.ofNullable(request.queryParams("state"))
                .map(RequestUtils::decode)
                .orElse(null);

        return new OAuth2AuthorizationRequest(responseType, clientId, redirectUri, scope, state);
    }

    @Override
    protected Message process(TokenId token, OAuth2AuthorizationRequest data) {
        return new OAuth2LoginPage(data.client.toString(), data.callback, data.scopes, data.state);
    }

}
