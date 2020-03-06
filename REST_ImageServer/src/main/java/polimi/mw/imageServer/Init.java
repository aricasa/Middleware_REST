package polimi.mw.imageServer;

import polimi.mw.imageServer.Oauth.OauthAPI;
import polimi.mw.imageServer.Oauth.OauthApp;
import java.io.IOException;


public class Init {

    //Used for requests from users
    static ImageServerAPI imageServerAPI;
    static OauthAPI oauthAPI;

    //Used for requests from third party
    static ImageServerApp imageServerApp;
    static OauthApp oauthApp;

    public static void main(String[] args) throws InterruptedException, IOException {

        //Initialize components
        imageServerAPI= new ImageServerAPI("storage",1000,1000);
        oauthAPI = new OauthAPI(imageServerAPI);
        imageServerApp= new ImageServerApp(imageServerAPI);
        oauthApp = new OauthApp(oauthAPI);
    }
}
