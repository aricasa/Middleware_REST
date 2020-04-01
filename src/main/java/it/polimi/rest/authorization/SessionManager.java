package it.polimi.rest.authorization;

import it.polimi.rest.data.BaseDataProvider;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.data.Storage;
import it.polimi.rest.exceptions.ForbiddenException;
import it.polimi.rest.exceptions.NotFoundException;
import it.polimi.rest.exceptions.RestException;
import it.polimi.rest.models.*;

import java.util.Collection;
import java.util.HashSet;

public class SessionManager {

    private final Authorizer authorizer;
    private final DataProvider dataProvider;

    private final Collection<Token> tokens = new HashSet<>();

    public SessionManager(Authorizer authorizer, Storage storage) {
        this.authorizer = authorizer;
        this.dataProvider = new BaseDataProvider(storage, this);
    }

    public DataProvider dataProvider(TokenId tokenId) {
        Token token = null;

        if (tokenId != null) {
            try {
                token = token(tokenId);
            } catch (RestException ignored) {

            }
        }

        return dataProvider(token);
    }

    public DataProvider dataProvider(Token token) {
        Agent agent = token == null || !token.isValid() ? null : token.agent();
        return new SecureDataProvider(dataProvider, authorizer, agent);
    }

    public Token token(TokenId id) {
        Token result = tokens.stream()
                .filter(token -> token.id().equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);

        if (!result.isValid()) {
            result.onExpiration(dataProvider);
            throw new NotFoundException();
        }

        return result;
    }

    public void add(Token token) {
        if (tokens.stream().anyMatch(t -> t.id().equals(token.id()))) {
            throw new ForbiddenException();
        }

        tokens.add(token);
    }

    public void remove(TokenId id) {
        tokens.removeIf(t -> t.id().equals(id));
    }

}
