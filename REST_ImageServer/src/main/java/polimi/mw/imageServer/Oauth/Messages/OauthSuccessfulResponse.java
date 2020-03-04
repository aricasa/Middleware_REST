package polimi.mw.imageServer.Oauth.Messages;

//Response returned by Oauth in case of correct request

public class OauthSuccessfulResponse implements OauthResponseToken {

    private String access_token;    //represents the username of the user
    private String refresh_token;   //represents the password of the user
    private int expires_in = 1000;  //represents the number seconds in which the token is valid
    private String token_type= "bearer";

    public void setAccess_token(String access_token) { this.access_token = access_token; }

    public void setRefresh_token(String refresh_token) { this.refresh_token = refresh_token; }

    public String getAccess_token() { return access_token; }

    public int getExpires_in() { return expires_in; }

    public String getRefresh_token() { return refresh_token; }

}
