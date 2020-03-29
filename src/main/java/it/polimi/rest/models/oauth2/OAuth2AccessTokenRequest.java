package it.polimi.rest.models.oauth2;

public class OAuth2AccessTokenRequest {

    public final String grantType;
    public final OAuth2Client.Id clientId;
    public final OAuth2Client.Secret clientSecret;
    public final boolean basicAuthentication;
    public final String redirectUri;
    public final OAuth2AuthorizationCode.Id code;

    public OAuth2AccessTokenRequest(String grantType,
                                    OAuth2Client.Id clientId,
                                    OAuth2Client.Secret clientSecret,
                                    boolean basicAuthentication,
                                    String redirectUri,
                                    OAuth2AuthorizationCode.Id code) {

        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.basicAuthentication = basicAuthentication;
        this.redirectUri = redirectUri;
        this.code = code;
    }

}
