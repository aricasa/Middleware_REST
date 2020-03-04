package polimi.mw.imageServer.Oauth.Messages;

public class OauthFailedResponse implements OauthResponseToken {

    private String error;
    private String error_description;

    public OauthFailedResponse(String error, String error_description) {

        this.error = error;
        this.error_description = error_description;
    }

}
