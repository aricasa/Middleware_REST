package polimi.mw.imageServer.Oauth;

public class OauthFailedResponse implements OauthResponseToken {

    private String error_description;

    public OauthFailedResponse(String error_description) { this.error_description = error_description; }

}
