package it.polimi.rest.api.oauth2;

import it.polimi.rest.api.Api;
import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.data.Storage;
import spark.Route;

public final class OAuth2Server extends Api {

    public OAuth2Server(Storage storage, SessionManager sessionManager) {
        super(storage, sessionManager);
    }

    public final Route clients = new Clients(sessionManager);
    public final Route clientAdd = new ClientAdd(sessionManager);
    public final Route clientRemove = new ClientRemove(sessionManager);
    public final Route authorize = new Authorize();
    public final Route grant = new Grant(sessionManager);
    public final Route deny = new Deny(sessionManager);
    public final Route token = new AccessToken(sessionManager);

}
