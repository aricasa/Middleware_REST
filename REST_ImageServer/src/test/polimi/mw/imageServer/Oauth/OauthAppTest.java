package polimi.mw.imageServer.Oauth;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import polimi.mw.imageServer.ImageServerAPI;
import polimi.mw.imageServer.Oauth.Messages.OauthFailedResponse;
import polimi.mw.imageServer.Oauth.Messages.OauthRequestToken;
import polimi.mw.imageServer.Oauth.Messages.OauthResponseToken;
import polimi.mw.imageServer.Oauth.Messages.OauthSuccessfulResponse;
import polimi.mw.imageServer.Token;
import polimi.mw.imageServer.User;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OauthAppTest {

    ImageServerAPI imageServerAPI= new ImageServerAPI("storage",1000,1000);
    OauthAPI oauthAPI = new OauthAPI(imageServerAPI);
    OauthApp oauthApp = new OauthApp(oauthAPI);

    @Test
    public void authentication()
    {
        imageServerAPI.clearUsers();

        Gson gson = new Gson();
        User user1= new User("abcd123", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        imageServerAPI.addID("abcd123",user1);

        OauthRequestToken oauthRequestToken = new OauthRequestToken("annina", "rossi", "client_credentials");
        OauthResponseToken response = (OauthSuccessfulResponse) oauthAPI.authenticate(oauthRequestToken);
        String token= ((OauthSuccessfulResponse) response).getAccess_token();
        assertTrue(gson.toJson(response).contains("access_token"));

        oauthRequestToken= new OauthRequestToken("annina", "rossi", "Authotization_code");
        response = (OauthFailedResponse) oauthAPI.authenticate(oauthRequestToken);
        assertTrue(gson.toJson(response).contains("unsupported_grant_type"));

        oauthRequestToken= new OauthRequestToken(null, "rossi", "client_credentials");
        response = (OauthFailedResponse) oauthAPI.authenticate(oauthRequestToken);
        assertTrue(gson.toJson(response).contains("invalid_request"));

        oauthRequestToken= new OauthRequestToken("annina", "verdi", "client_credentials");
        response = (OauthFailedResponse) oauthAPI.authenticate(oauthRequestToken);
        assertTrue(gson.toJson(response).contains("invalid_client"));


        imageServerAPI.clearUsers();
    }

}