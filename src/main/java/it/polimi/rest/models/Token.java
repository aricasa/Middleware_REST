package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import it.polimi.rest.authorization.Permission;

import java.util.*;

public class Token implements Model {

    @Expose
    public final TokenId id;

    @Expose(deserialize = false)
    private final Calendar expiration;

    public final UserId user;
    public final Permission accountPermission;
    public final Permission sessionPermission;
    public final Permission imagesPermission;

    public Token(TokenId id, int lifeTime,
                 UserId user,
                 Permission accountPermission,
                 Permission sessionPermission,
                 Permission imagesPermission) {

        this.id = id;

        this.expiration = Calendar.getInstance();
        this.expiration.add(Calendar.SECOND, lifeTime);

        this.user = user;
        this.accountPermission = accountPermission;
        this.sessionPermission = sessionPermission;
        this.imagesPermission = imagesPermission;
    }

    /**
     * Check if the token is still valid.
     *
     * @return whether the token is valid (true) or has expired (false)
     */
    public boolean isValid() {
        Calendar now = Calendar.getInstance();
        return now.before(expiration);
    }

    @Override
    public Optional<String> self() {
        return Optional.of("/sessions/" + id);
    }

    @Override
    public Map<String, Link> links() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> embedded() {
        return Collections.emptyMap();
    }

}
