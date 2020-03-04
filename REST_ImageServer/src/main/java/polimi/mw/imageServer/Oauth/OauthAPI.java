package polimi.mw.imageServer.Oauth;

import polimi.mw.imageServer.ImageServerAPI;
import polimi.mw.imageServer.Oauth.Messages.OauthFailedResponse;
import polimi.mw.imageServer.Oauth.Messages.OauthRequestToken;
import polimi.mw.imageServer.Oauth.Messages.OauthResponseToken;
import polimi.mw.imageServer.Oauth.Messages.OauthSuccessfulResponse;
import polimi.mw.imageServer.User;

import java.util.Iterator;

public class OauthAPI {

    private static ImageServerAPI imageServerAPI;

    public OauthAPI(ImageServerAPI imageServerAPI)
    {
        this.imageServerAPI=imageServerAPI;
    }

    public static OauthResponseToken authenticate(OauthRequestToken requestToken)
    {
        OauthResponseToken responseToken;

        if(checkIfMissingField(requestToken))
            return new OauthFailedResponse("invalid_request","Missing parameter in the request.");

        if(checkGrantType(requestToken))
            return new OauthFailedResponse("unsupported_grant_type","The only supported grant type is client_credentials.");

        User user = imageServerAPI.userWithUsernamePassword(requestToken.getClient_id(), requestToken.getClient_secret());

        if(user!=null)
        {
            responseToken=new OauthSuccessfulResponse();
            ((OauthSuccessfulResponse) responseToken).setAccess_token(user.addThirdPartyToken(imageServerAPI.getTokenExpirationTimeThirdParty()));
            return responseToken;
        }

        responseToken= new OauthFailedResponse("invalid_client","Username and password don't correspond to any user.");
        return responseToken;
    }

    private static boolean checkIfMissingField(OauthRequestToken requestToken)
    {
        return requestToken.getClient_secret()==null || requestToken.getClient_id()== null || requestToken.getGrant_type()== null;
    }

    private static boolean checkGrantType(OauthRequestToken requestToken)
    {
        return requestToken.getGrant_type().compareTo("client_credentials")!=0;
    }
}
