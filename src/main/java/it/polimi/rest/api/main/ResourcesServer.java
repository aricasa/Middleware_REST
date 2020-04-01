package it.polimi.rest.api.main;

import it.polimi.rest.api.Api;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import spark.Route;

public final class ResourcesServer extends Api {

    public ResourcesServer(Storage storage, SessionManager sessionManager) {
        super(storage, sessionManager);
    }

    public final Route root = new RootPage();
    public final Route users = new Users(sessionManager);
    public final Route userAdd = new UserAdd(sessionManager);
    public final Route userDetails = new UserDetails(sessionManager);
    public final Route userRemove = new UserRemove(sessionManager);
    public final Route login = new Login(sessionManager, authenticator);
    public final Route logout = new Logout(sessionManager);
    public final Route userImages = new UserImages(sessionManager);
    public final Route imageAdd = new ImageAdd(sessionManager);
    public final Route imageDetails = new ImageDetails(sessionManager);
    public final Route imageRaw = new ImageRaw(sessionManager);
    public final Route imageRemove = new ImageRemove(sessionManager);

}
