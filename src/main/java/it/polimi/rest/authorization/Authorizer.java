package it.polimi.rest.authorization;

public interface Authorizer {

    void grant(SecuredObject obj, Agent agent, Permission permission);
    void revoke(SecuredObject obj, Agent agent);
    void revoke(SecuredObject obj);
    Permission get(SecuredObject obj, Agent agent);

}
