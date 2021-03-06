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

    private final DataProvider dataProvider;
    private final Authorizer authorizer;
    private final Storage storage;

    private final Collection<Token> tokens = new HashSet<>();

    public SessionManager(Authorizer authorizer, Storage storage) {
        this.dataProvider = new BaseDataProvider(storage, this);
        this.authorizer = authorizer;
        this.storage = storage;
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
        return dataProvider(agent);
    }

    public DataProvider dataProvider(Agent agent) {
        return new SecureDataProvider(dataProvider, this, authorizer, agent);
    }

    public Token token(TokenId id) {
        Token result = tokens.stream()
                .filter(token -> token.id().equals(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);

        if (!result.isValid()) {
            BaseDataProvider dataProvider = new BaseDataProvider(storage, this);
            result.onExpiration(dataProvider, this);
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
