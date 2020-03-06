package polimi.mw.imageServer.Oauth;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polimi.mw.imageServer.Oauth.Messages.OauthRequestToken;

import static spark.Spark.*;

/** Manages the requests for obtaining a token from third parties */

public class OauthApp {

    static Logger logger = LoggerFactory.getLogger(OauthApp.class);
    static OauthAPI oauthAPI;

    public OauthApp(OauthAPI oauthAPI)
    {
        this.oauthAPI=oauthAPI;
        Start();
    }

    public static void Start() {

        Gson gson = new Gson();

        /** This allows a third party to authenticate
        * Example of curl command: curl -X GET http://localhost:4567/imageServer/authorization -H 'Cache-Control: no-cache' -d '{ "grant_type" : "client_credentials" , "client_id" : "piPPone" , "client_secret" : "myPassword" }' */
        path("/imageServer", () -> {
            get("/authorization", (request, response) -> {

                OauthRequestToken auth = gson.fromJson(request.body(), OauthRequestToken.class);
                return gson.toJson(oauthAPI.authenticate(auth));
            });
        });

    }
}
