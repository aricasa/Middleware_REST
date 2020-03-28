package it.polimi.rest.authorization;

public class Authorization {

    public final SecuredObject obj;
    public final Agent agent;
    public final Permission permission;

    public Authorization(SecuredObject obj, Agent agent, Permission permission) {
        this.obj = obj;
        this.agent = agent;
        this.permission = permission;
    }

}
