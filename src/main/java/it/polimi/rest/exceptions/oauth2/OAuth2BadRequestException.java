package it.polimi.rest.exceptions.oauth2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import it.polimi.rest.exceptions.BadRequestException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@JsonAdapter(OAuth2Exception.Adapter.class)
public class OAuth2BadRequestException extends BadRequestException implements OAuth2Exception {

    private static final long serialVersionUID = 2675402053550759440L;

    @Expose
    public final String errorDescription;

    @Expose
    public final String errorUri;

    public OAuth2BadRequestException(String error, String errorDescription, String errorUri) {
        super(encode(error));

        this.errorDescription = encode(errorDescription);
        this.errorUri = encode(errorUri);
    }

    private static String encode(String value) {
        if (value == null) {
            return null;
        }

        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new OAuth2BadRequestException(OAuth2BadRequestException.SERVER_ERROR, null, null);
        }
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
