package it.polimi.rest.adapters;

import it.polimi.rest.exceptions.oauth2.OAuth2BadRequestException;
import it.polimi.rest.models.oauth2.OAuth2AccessTokenRequest;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.utils.RequestUtils;
import spark.Request;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public class OAuth2AccessTokenRequestDeserializer implements Deserializer<OAuth2AccessTokenRequest> {

    @Override
    public OAuth2AccessTokenRequest parse(Request request) {
        Map<String, String> bodyParams = RequestUtils.bodyParams(request);

        String grantType = grantType(bodyParams);
        OAuth2Client.Id clientId = clientId(request, bodyParams);
        OAuth2Client.Secret clientSecret = clientSecret(request, bodyParams);
        boolean basicAuthentication = isBasicAuthentication(request);
        String redirectUri = redirectUri(bodyParams);
        OAuth2AuthorizationCode.Id code = authorizationCode(bodyParams);

        return new OAuth2AccessTokenRequest(grantType, clientId, clientSecret, basicAuthentication, redirectUri, code);
    }


    private String grantType(Map<String, String> bodyParams) {
        return bodyParams.get("grant_type");
    }

    private OAuth2Client.Id clientId(Request request, Map<String, String> bodyParams) {
        return Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Basic"))
                .map(encoded -> encoded.substring("Basic".length()).trim())
                .map(encoded -> new String(Base64.getDecoder().decode(encoded)))
                .map(decoded -> decoded.split(":")[0])
                .map(OAuth2Client.Id::new)
                .orElseGet(() -> Optional.ofNullable(bodyParams.get("client_id"))
                        .map(OAuth2Client.Id::new)
                        .orElseThrow(() -> new OAuth2BadRequestException(OAuth2BadRequestException.INVALID_REQUEST, null, null)));
    }

    private OAuth2Client.Secret clientSecret(Request request, Map<String, String> bodyParams) {
        return Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Basic"))
                .map(encoded -> encoded.substring("Basic".length()).trim())
                .map(encoded -> new String(Base64.getDecoder().decode(encoded)))
                .map(decoded -> decoded.split(":")[1])
                .map(OAuth2Client.Secret::new)
                .orElseGet(() -> Optional.ofNullable(bodyParams.get("client_secret"))
                        .map(OAuth2Client.Secret::new)
                        .orElseThrow(() -> new OAuth2BadRequestException(OAuth2BadRequestException.INVALID_CLIENT, null, null)));
    }

    private boolean isBasicAuthentication(Request request) {
        return Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Basic")).isPresent();
    }

    private String redirectUri(Map<String, String> bodyParams) {
        return Optional.ofNullable(bodyParams.get("redirect_uri"))
                .orElseThrow(() -> new OAuth2BadRequestException(OAuth2BadRequestException.INVALID_REQUEST, null, null));
    }

    private OAuth2AuthorizationCode.Id authorizationCode(Map<String, String> bodyParams) {
        return Optional.ofNullable(bodyParams.get("code"))
                .map(OAuth2AuthorizationCode.Id::new)
                .orElseThrow(() -> new OAuth2BadRequestException(OAuth2BadRequestException.INVALID_REQUEST, null, null));
    }

}
