package it.polimi.rest.models.oauth2.scope;

import it.polimi.rest.authorization.Agent;
import it.polimi.rest.authorization.Authorizer;
import it.polimi.rest.authorization.Permission;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.models.User;

class ReadUser extends Scope {

    public ReadUser() {
        super(Scope.READ_USER);
    }

    @Override
    protected void addPermissions(Authorizer authorizer, DataProvider dataProvider, User.Id user, Agent agent) {
        authorizer.grant(user, agent, Permission.READ);
    }

}
