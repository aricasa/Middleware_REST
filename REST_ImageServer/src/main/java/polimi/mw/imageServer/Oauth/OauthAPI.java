package polimi.mw.imageServer.Oauth;

import polimi.mw.imageServer.ImageServerAPI;
import polimi.mw.imageServer.Oauth.Messages.OauthFailedResponse;
import polimi.mw.imageServer.Oauth.Messages.OauthRequestToken;
import polimi.mw.imageServer.Oauth.Messages.OauthResponseToken;
import polimi.mw.imageServer.Oauth.Messages.OauthSuccessfulResponse;
import polimi.mw.imageServer.User;

public class OauthAPI {

    private static ImageServerAPI imageServerAPI;

    public OauthAPI(ImageServerAPI imageServerAPI)
    {
        this.imageServerAPI=imageServerAPI;
    }


    // @param requestToken          represents the Oauth request of a token
    // Returns a OauthResponseToken object which can be a OauthSuccesfulResponse or a OauthFailedResponse
    public static OauthResponseToken authenticate(OauthRequestToken requestToken)
    {
        OauthResponseToken responseToken;

        if(checkIfMissingField(requestToken))
            return new OauthFailedResponse("invalid_request","Missing parameter in the request.");

        if(checkGrantType(requestToken))
            return new OauthFailedResponse("unsupported_grant_type","The only supported grant type is client_credentials.");

        //Check if the credentials provided actyally correspond to a user
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

    //Check if all the fields of a Request have been filled up
    private static boolean checkIfMissingField(OauthRequestToken requestToken)
    {
        return requestToken.getClient_secret()==null || requestToken.getClient_id()== null || requestToken.getGrant_type()== null;
    }

    //Check if the grant type of request corresponds to the ones accepted
    private static boolean checkGrantType(OauthRequestToken requestToken)
    {
        return requestToken.getGrant_type().compareTo("client_credentials")!=0;
    }
}
