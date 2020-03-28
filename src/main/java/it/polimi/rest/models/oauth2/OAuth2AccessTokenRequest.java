package it.polimi.rest.models.oauth2;

public class OAuth2AccessTokenRequest {

    public final String grantType;
    public final OAuth2Client.Id client;
    public final String callback;
    public final OAuth2AuthorizationCode code;

    public OAuth2AccessTokenRequest(String grantType,
                                    OAuth2Client.Id client,
                                    String callback,
                                    OAuth2AuthorizationCode code) {

        this.grantType = grantType;
        this.client = client;
        this.callback = callback;
        this.code = code;
    }

}
