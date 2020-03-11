package it.polimi.rest.oauth.messages;

//Response returned by Oauth in case of wrong request

public class OauthFailedResponse implements OauthResponseToken {

    private String error;               //represents the type of error
    private String error_description;   //represents a brief description of the type of error

    public OauthFailedResponse(String error, String error_description) {

        this.error = error;
        this.error_description = error_description;
    }

}
