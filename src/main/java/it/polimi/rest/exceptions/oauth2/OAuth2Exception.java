package it.polimi.rest.exceptions.oauth2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.polimi.rest.exceptions.RedirectionException;

import java.lang.reflect.Type;

public interface OAuth2Exception {

    String ACCESS_DENIED = "access_denied";
    String INVALID_CLIENT = "invalid_client";
    String INVALID_GRANT = "invalid_grant";
    String INVALID_REQUEST = "invalid_request";
    String INVALID_SCOPE = "invalid_scope";
    String SERVER_ERROR = "server_error";

    String error();
    String errorDescription();
    String errorUri();

    default RedirectionException redirect(String redirectUri, String state) {
        String url = redirectUri + "?error=" + error();

        String errorDescription = errorDescription();

        if (errorDescription != null && !errorDescription.isEmpty()) {
            url += "&error_description=" + errorDescription;
        }

        String errorUri = errorUri();

        if (errorUri != null && !errorUri.isEmpty()) {
            url += "&error_uri=" + errorUri;
        }

        if (state != null && !state.isEmpty()) {
            url += "&state=" + state;
        }

        return new RedirectionException(url);
    }

    class Adapter implements JsonSerializer<OAuth2BadRequestException> {

        @Override
        public JsonElement serialize(OAuth2BadRequestException src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();

            json.add("error", context.serialize(src.error));
            json.add("error_description", context.serialize(src.errorDescription));
            json.add("error_uri", context.serialize(src.errorUri));

            return json;
        }

    }

}
