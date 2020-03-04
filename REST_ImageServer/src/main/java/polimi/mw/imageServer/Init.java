package polimi.mw.imageServer;

import org.ini4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import polimi.mw.imageServer.Oauth.OauthServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Init {

    static Logger logger = LoggerFactory.getLogger(Init.class);
    static ImageServerAPI imageServerAPI= new ImageServerAPI();
    static ImageServerApp imageServerApp;
    static OauthServer oauthServer;

    public static void main(String[] args) throws InterruptedException, IOException {
        
        imageServerApp= new ImageServerApp(imageServerAPI);
        oauthServer= new OauthServer(imageServerAPI);
    }
}
