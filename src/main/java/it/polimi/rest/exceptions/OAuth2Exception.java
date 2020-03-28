package it.polimi.rest.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(OAuth2Exception.Adapter.class)
public class OAuth2Exception extends BadRequestException {

    // TODO: implement remaining errors
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String ACCESS_DENIED = "access_denied";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String SERVER_ERROR = "server_error";

    private static final long serialVersionUID = 2675402053550759440L;

    @Expose
    public final boolean redirect;

    @Expose
    public final String errorDescription;

    @Expose
    public final String errorUri;

    public OAuth2Exception(boolean redirect, String error, String errorDescription, String errorUri) {
        super(error);

        this.redirect = redirect;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    public static class Adapter implements JsonSerializer<OAuth2Exception> {

        @Override
        public JsonElement serialize(OAuth2Exception src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();

            json.add("redirect", context.serialize(src.redirect));
            json.add("error", context.serialize(src.error));
            json.add("error_description", context.serialize(src.errorDescription));
            json.add("error_uri", context.serialize(src.errorUri));

            return json;
        }

    }

}
