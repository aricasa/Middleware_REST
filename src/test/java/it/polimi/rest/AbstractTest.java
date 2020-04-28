package it.polimi.rest;

import it.polimi.rest.api.main.ResourcesServer;
import it.polimi.rest.api.oauth2.OAuth2Server;
import it.polimi.rest.authorization.ACL;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import it.polimi.rest.data.VolatileStorage;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractTest {

    private App app;
    protected static final String BASE_URL = "http://localhost:4567/";

    @Before
    public void setUp() throws InterruptedException {
        Authorizer authorizer = new ACL();
        Storage storage = new VolatileStorage();
        SessionManager sessionManager = new SessionManager(authorizer, storage);
        ResourcesServer resourcesServer = new ResourcesServer(storage, sessionManager);
        OAuth2Server oAuth2Server = new OAuth2Server(storage, sessionManager);
        app = new App(resourcesServer, oAuth2Server);

        app.start();
        Thread.sleep(500);
    }

    @After
    public void tearDown() throws InterruptedException {
        app.stop();
        Thread.sleep(500);
    }

}
