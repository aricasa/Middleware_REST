package polimi.mw.imageServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polimi.mw.imageServer.Oauth.OauthAPI;
import polimi.mw.imageServer.Oauth.OauthApp;
import java.io.IOException;



public class Init {

    static Logger logger = LoggerFactory.getLogger(Init.class);
    static ImageServerAPI imageServerAPI= new ImageServerAPI("storage");
    static OauthAPI oauthAPI = new OauthAPI(imageServerAPI);
    static ImageServerApp imageServerApp;
    static OauthApp oauthApp;

    public static void main(String[] args) throws InterruptedException, IOException {

        imageServerApp= new ImageServerApp(imageServerAPI);
        oauthApp = new OauthApp(oauthAPI);
    }
}
