package it.polimi.rest.authorization;

import it.polimi.rest.models.Token;
import it.polimi.rest.models.User;

import java.util.Optional;

import static java.util.UUID.randomUUID;

public abstract class Authorizer {

    private final int tokenLifetime;

    /**
     * Constructor.
     *
     * @param tokenLifetime     token lifetime in seconds
     */
    public Authorizer(int tokenLifetime) {
        this.tokenLifetime = tokenLifetime;
    }

    /**
     * Create a new token for a user and discard the old ones if there is any.
     *
     * @param user  owner of the new token
     * @return token
     */
    public final Token authorize(User user) {
        Optional<Token> previousToken = getByUser(user);
        previousToken.ifPresent(this::remove);

        Token token;

        do {
            token = new Token(randomUUID().toString(), tokenLifetime, user);
        } while (searchToken(token.id).isPresent());

        add(token);
        return token;
    }

    /**
     * Delete all the tokens of a user.
     *
     * @param user  user
     */
    public final void revoke(User user) {
        getByUser(user).ifPresent(this::remove);
    }

    /**
     * Search for a token.
     *
     * @param id    ID of the token to be searched
     * @return token
     */
    public abstract Optional<Token> searchToken(String id);

    protected abstract Optional<Token> getByUser(User user);
    protected abstract void add(Token token);
    protected abstract void remove(Token token);

}
