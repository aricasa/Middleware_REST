package polimi.mw.imageServer.Oauth.Messages;

public class OauthRequestToken {

    private String grant_type;
    private String client_id;
    private String client_secret;

    public OauthRequestToken(String client_id, String client_secret, String grant_type)
    {
        this.client_id=client_id;
        this.client_secret=client_secret;
        this.grant_type=grant_type;
    }

    public String getClient_id() { return client_id; }
    public String getClient_secret() { return client_secret; }
    public String getGrant_type() { return grant_type; }

}
