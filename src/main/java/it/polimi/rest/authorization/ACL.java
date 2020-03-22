package it.polimi.rest.authorization;

import java.util.Collection;
import java.util.HashSet;

/**
 * Access control list.
 */
public class ACL implements Authorizer {

    private final Collection<Authorization> authorizations = new HashSet<>();

    @Override
    public void grant(SecuredObject obj, Agent agent, Permission permission) {
        revoke(obj, agent);
        authorizations.add(new Authorization(obj, agent, permission));
    }

    @Override
    public void revoke(SecuredObject obj, Agent agent) {
        authorizations.removeIf(auth -> auth.obj.equals(obj) && auth.user.equals(agent));
    }

    @Override
    public void revokeAll(SecuredObject obj) {
        authorizations.removeIf(auth -> auth.obj.equals(obj));
    }

    @Override
    public Permission get(SecuredObject obj, Agent agent) {
        return authorizations.stream()
                .filter(auth -> auth.obj.equals(obj) && auth.user.equals(agent))
                .findFirst()
                .orElse(new Authorization(obj, agent, Permission.NONE))
                .permission;
    }

}
