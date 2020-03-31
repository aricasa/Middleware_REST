package it.polimi.rest.api.main;

import it.polimi.rest.api.Api;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.credentials.CredentialsManager;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.sessions.SessionsManager;
import spark.Route;

public final class ResourcesServer extends Api {

    public ResourcesServer(Authorizer authorizer,
                           SessionsManager sessionsManager,
                           DataProvider dataProvider,
                           CredentialsManager credentialsManager) {

        super(authorizer, sessionsManager, dataProvider, credentialsManager);
    }

    public final Route root = new RootPage();
    public final Route users = new Users(proxy);
    public final Route userAdd = new UserAdd(proxy, credentialsManager);
    public final Route userDetails = new UserDetails(proxy);
    public final Route userRemove = new UserRemove(proxy, credentialsManager);
    public final Route login = new Login(proxy, credentialsManager);
    public final Route logout = new Logout(proxy);
    public final Route userImages = new UserImages(proxy);
    public final Route imageAdd = new ImageAdd(proxy);
    public final Route imageDetails = new ImageDetails(proxy);
    public final Route imageRaw = new ImageRaw(proxy);
    public final Route imageRemove = new ImageRemove(proxy);

}
