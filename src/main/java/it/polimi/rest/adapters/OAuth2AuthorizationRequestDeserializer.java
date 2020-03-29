package it.polimi.rest.adapters;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationRequest;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.RequestUtils;
import spark.Request;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2AuthorizationRequestDeserializer implements Deserializer<OAuth2AuthorizationRequest> {

    @Override
    public OAuth2AuthorizationRequest parse(Request request) {
        Map<String, String> bodyParams = RequestUtils.bodyParams(request);

        String responseType = responseType(request, bodyParams);
        OAuth2Client.Id client = clientId(request, bodyParams);
        String redirectUri = redirectUri(request, bodyParams);
        Collection<String> scope = scope(request, bodyParams);
        String state = state(request, bodyParams);

        return new OAuth2AuthorizationRequest(responseType, client, redirectUri, scope, state);
    }

    private String responseType(Request request, Map<String, String> bodyParams) {
        return Optional.ofNullable(request.queryParams("response_type"))
                .map(RequestUtils::decode)
                .orElse(bodyParams.get("response_type"));
    }

    private OAuth2Client.Id clientId(Request request, Map<String, String> bodyParams) {
        Optional<OAuth2Client.Id> header = Optional.ofNullable(request.queryParams("client_id"))
                .map(String::trim)
                .map(RequestUtils::decode)
                .map(OAuth2Client.Id::new);

        OAuth2Client.Id id = header.orElseGet(() ->
                Optional.ofNullable(bodyParams.get("client_id"))
                        .map(OAuth2Client.Id::new)
                        .orElse(null));

        if (id == null) {
            throw new BadRequestException("Client ID not specified");
        }

        return id;
    }

    private String redirectUri(Request request, Map<String, String> bodyParams) {
        String redirectUri = Optional.ofNullable(request.queryParams("redirect_uri"))
                .map(RequestUtils::decode)
                .orElse(bodyParams.get("redirect_uri"));

        if (redirectUri == null) {
            throw new BadRequestException("Redirect URI not specified");
        }

        return redirectUri;
    }

    private Collection<String> scope(Request request, Map<String, String> bodyParams) {
        String scopes = RequestUtils.decode(request.queryParams("scope"));

        if (scopes == null) {
            scopes = bodyParams.get("scope");
        }

        return Optional.ofNullable(scopes)
                .map(scope -> scope.split(" "))
                .map(Stream::of)
                .map(stream -> stream.collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private String state(Request request, Map<String, String> bodyParams) {
        return Optional.ofNullable(request.queryParams("state"))
                .map(RequestUtils::decode)
                .orElse(bodyParams.get("state"));
    }

}
