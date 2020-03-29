package it.polimi.rest.authorization;

public interface Authorizer {

    void grant(SecuredObject obj, Agent agent, Permission permission);
    void removeObject(SecuredObject obj, Agent agent);
    void removeObject(SecuredObject obj);
    void removeAgent(Agent agent);
    Permission get(SecuredObject obj, Agent agent);

}
