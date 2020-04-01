package it.polimi.rest.api.oauth2;

import it.polimi.rest.api.Api;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.data.SessionsManager;
import it.polimi.rest.data.Storage;
import spark.Route;

public final class OAuth2Server extends Api {

    public OAuth2Server(Authorizer authorizer,
                        SessionsManager sessionsManager,
                        Storage storage) {

        super(authorizer, sessionsManager, storage);
    }

    public final Route clients = new Clients(proxy);
    public final Route clientAdd = new ClientAdd(proxy);
    public final Route clientRemove = new ClientRemove(proxy);
    public final Route authorize = new Authorize();
    public final Route grant = new Grant(proxy);
    public final Route deny = new Deny(proxy);
    public final Route token = new AccessToken(proxy);

}
