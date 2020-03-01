package polimi.mw.imageServer.Oauth;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polimi.mw.imageServer.ImageServerAPI;
import polimi.mw.imageServer.ImageServerApp;
import polimi.mw.imageServer.User;

import static spark.Spark.*;

public class OauthServer {

    static Logger logger = LoggerFactory.getLogger(OauthServer.class);
    static ImageServerAPI imageServerAPI;

    public void setImageServerAPI(ImageServerAPI imageServerAPI) { this.imageServerAPI=imageServerAPI; }

    public static void main(String[] args) {
        Gson gson = new Gson();

        port(1234);

        //This allows a third party to authenticate
        //Example of curl command: curl -X GET http://localhost:1234/imageServer/authorization -H 'Cache-Control: no-cache' -d '{ "grant_type" : "client_credentials" , "client_id" : "piPPone" , "client_secret" : "myPassword" }'
        path("/imageServer", () -> {
            get("/authorization", (request, response) -> {
                OauthRequestToken auth = gson.fromJson(request.body(), OauthRequestToken.class);
                return gson.toJson(imageServerAPI.authenticate(auth));
            });
        });

    }
}
