package it.polimi.rest.adapters;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationRequest;
import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2ClientId;
import it.polimi.rest.models.oauth2.Scope;
import spark.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2AuthorizationRequestDeserializer implements Deserializer<OAuth2AuthorizationRequest> {

    @Override
    public OAuth2AuthorizationRequest parse(Request request, TokenId token) {
        String responseType = getResponseType(request);
        OAuth2ClientId client = getClientId(request);
        String callback = getRedirectUri(request);
        Collection<Scope> scope = getScope(request);
        String state = getState(request);

        return new OAuth2AuthorizationRequest(responseType, client, callback, scope, state);
    }

    private String getResponseType(Request request) {
        return Optional.ofNullable(request.queryParams("response_type"))
                .orElseThrow(() -> new BadRequestException("Response type not specified"));
    }

    private OAuth2ClientId getClientId(Request request) {
        return Optional.ofNullable(request.queryParams("client_id"))
                .map(OAuth2ClientId::new)
                .orElseThrow(() -> new BadRequestException("Client ID not specified"));
    }

    private String getRedirectUri(Request request) {
        return Optional.ofNullable(request.queryParams("redirect_uri"))
                .orElseThrow(() -> new BadRequestException("Redirect URI not specified"));
    }

    private Collection<Scope> getScope(Request request) {
        return Optional.ofNullable(request.queryParams("scope"))
                .map(scope -> scope.split(" "))
                .map(Stream::of)
                .map(stream -> stream.map(Scope::new).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private String getState(Request request) {
        return Optional.ofNullable(request.queryParams("state"))
                .orElse("");
    }

}
