package polimi.mw.imageServer.Oauth;

public class OauthRequestToken {

    private String grant_type = "client_credentials";
    private String client_id;
    private String client_secret;

    public OauthRequestToken(String client_id, String client_secret)
    {
        this.client_id=client_id;
        this.client_secret=client_secret;
    }

    public String getClient_id() { return client_id; }
    public String getClient_secret() { return client_secret; }

}
