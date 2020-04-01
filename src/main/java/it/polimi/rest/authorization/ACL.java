package it.polimi.rest.authorization;

import java.util.Collection;
import java.util.HashSet;

/**
 * Access control list.
 */
public class ACL implements Authorizer {

    private final Collection<Authorization> authorizations = new HashSet<>();

    public void grant(SecuredObject obj, Agent agent, Permission permission) {
        removeObject(obj, agent);
        authorizations.add(new Authorization(obj, agent, permission));
    }

    public void removeObject(SecuredObject obj, Agent agent) {
        authorizations.removeIf(auth ->
                auth.obj.equals(obj) &&
                auth.agent.equals(agent));
    }

    public void removeObject(SecuredObject obj) {
        authorizations.removeIf(auth ->
                auth.obj.equals(obj));
    }

    public void removeAgent(Agent agent) {
        authorizations.removeIf(auth ->
                auth.agent.equals(agent));
    }

    public Permission get(SecuredObject obj, Agent agent) {
        return authorizations.stream()
                .filter(auth -> auth.obj.equals(obj) &&
                                auth.agent.equals(agent))
                .findFirst()
                .orElse(new Authorization(obj, agent, Permission.NONE))
                .permission;
    }

}
