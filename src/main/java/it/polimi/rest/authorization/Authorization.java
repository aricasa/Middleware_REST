package it.polimi.rest.authorization;

import it.polimi.rest.models.Id;

public class Authorization {

    public final SecuredObject obj;
    public final Agent user;
    public final Permission permission;

    public Authorization(SecuredObject obj, Agent user, Permission permission) {
        this.obj = obj;
        this.user = user;
        this.permission = permission;
    }

}
