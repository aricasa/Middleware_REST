package it.polimi.rest.exceptions.oauth2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.exceptions.UnauthorizedException;

import java.lang.reflect.Type;

@JsonAdapter(OAuth2Exception.Adapter.class)
public class OAuth2UnauthorizedException extends UnauthorizedException implements OAuth2Exception {

    private static final long serialVersionUID = 1576816220436009345L;

    @Expose
    public final String errorDescription;

    @Expose
    public final String errorUri;

    public OAuth2UnauthorizedException(AuthType authentication, String error, String errorDescription, String errorUri) {
        super(authentication, error);

        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    @Override
    public String error() {
        return error;
    }

    @Override
    public String errorDescription() {
        return errorDescription;
    }

    @Override
    public String errorUri() {
        return errorUri;
    }

}
