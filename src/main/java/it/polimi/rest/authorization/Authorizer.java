package it.polimi.rest.authorization;

public interface Authorizer {

    void grant(SecuredObject obj, Agent agent, Permission permission);
    void remove(SecuredObject obj, Agent agent);
    void remove(Object object);
    Permission get(SecuredObject obj, Agent agent);

}
