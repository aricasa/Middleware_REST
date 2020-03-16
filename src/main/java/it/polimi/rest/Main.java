package it.polimi.rest;

import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.credentials.VolatileCredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.data.VolatileDataProvider;
import it.polimi.rest.sessions.SessionsManager;
import it.polimi.rest.sessions.VolatileSessionManager;

public class Main {

    public static void main(String[] args) {
        CredentialsManager credentialsManager = new VolatileCredentialsManager();
        SessionsManager sessionsManager = new VolatileSessionManager();
        DataProvider dataProvider = new VolatileDataProvider();

        ImageServerAPI imageServerAPI = new ImageServerAPI(credentialsManager, sessionsManager, dataProvider);
        ImageServerApp imageServerApp = new ImageServerApp(imageServerAPI);
    }

}
