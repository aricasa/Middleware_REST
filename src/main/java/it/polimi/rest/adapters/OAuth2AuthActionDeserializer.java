package it.polimi.rest.adapters;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.InternalErrorException;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationRequest;
import it.polimi.rest.models.oauth2.OAuth2Client;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2AuthActionDeserializer implements Deserializer<OAuth2AuthorizationRequest> {

    @Override
    public OAuth2AuthorizationRequest parse(Request request) {
        Map<String, String> params = new HashMap<>();

        for (String param : request.body().split("&")) {
            String[] entry = param.split("=");
            params.put(entry[0], entry[1]);
        }

        String responseType = decode(params.get("response_type"));

        OAuth2Client.Id client = Optional.ofNullable(params.get("client_id"))
                .map(this::decode)
                .map(OAuth2Client.Id::new)
                .orElseThrow(() -> new BadRequestException("Client ID not specified"));

        String callback = Optional.ofNullable(params.get("redirect_uri"))
                .map(this::decode)
                .orElseThrow(() -> new BadRequestException("Redirect URI not specified"));

        Collection<String> scope = Optional.ofNullable(params.get("scope"))
                .map(this::decode)
                .map(scopes -> scopes.split(" "))
                .map(Stream::of)
                .map(stream -> stream
                        .map(this::decode)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        String state = decode(params.get("state"));

        return new OAuth2AuthorizationRequest(responseType, client, callback, scope, state);
    }

    private String decode(String value) {
        if (value == null) {
            return null;
        }

        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());

        } catch (UnsupportedEncodingException e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

}
