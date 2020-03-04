package polimi.mw.imageServer;

import polimi.mw.imageServer.Oauth.OauthAPI;
import polimi.mw.imageServer.Oauth.OauthApp;
import java.io.IOException;


public class Init {

    static ImageServerAPI imageServerAPI= new ImageServerAPI("storage");
    static OauthAPI oauthAPI = new OauthAPI(imageServerAPI);
    static ImageServerApp imageServerApp;
    static OauthApp oauthApp;

    public static void main(String[] args) throws InterruptedException, IOException {

        imageServerApp= new ImageServerApp(imageServerAPI);
        oauthApp = new OauthApp(oauthAPI);
    }
}
