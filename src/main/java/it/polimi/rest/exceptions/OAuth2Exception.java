package it.polimi.rest.exceptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuth2Exception extends Exception {

    // TODO: implement remaining errors
    public static final String ACCESS_DENIED = "access_denied";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String SERVER_ERROR = "server_error";

    private static final long serialVersionUID = 2675402053550759440L;

    private final String error;
    private final String errorDescription;
    private final String errorUri;

    public OAuth2Exception(String error, String errorDescription, String errorUri) throws OAuth2Exception {
        super(error);

        this.error = encode(error);
        this.errorDescription = encode(errorDescription);
        this.errorUri = encode(errorUri);
    }

    private String encode(String value) throws OAuth2Exception {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new OAuth2Exception(OAuth2Exception.SERVER_ERROR, null, null);
        }
    }

    public String url(String redirectUri, String state) {
        String result = redirectUri + "?error=" + error;

        if (errorDescription != null && !errorDescription.isEmpty()) {
            result += "&error_description=" + errorDescription;
        }

        if (errorUri != null && !errorUri.isEmpty()) {
            result += "&error_uri=" + errorUri;
        }

        if (state != null && !state.isEmpty()) {
            result += "&state=" + state;
        }

        return result;
    }

}
