package it.polimi.rest;

import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.VolatileAuthorizer;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.credentials.VolatileCredentialsManager;

public class Main {

    // Used for requests from users
    private final ImageServerAPI imageServerAPI;
    //private final OauthAPI oauthAPI;

    // Used for requests from third party
    private final ImageServerApp imageServerApp;
    //private final OauthApp oauthApp;

    public Main() {
        Authorizer authorizer = new VolatileAuthorizer();
        CredentialsManager credentialsManager = new VolatileCredentialsManager(authorizer);
        imageServerAPI= new ImageServerAPI(credentialsManager, authorizer);
        imageServerApp= new ImageServerApp(imageServerAPI);

        //oauthAPI = new OauthAPI(imageServerAPI);
        //oauthApp = new OauthApp(oauthAPI);
    }

    public static void main(String[] args) {
        Main server = new Main();
    }

}
