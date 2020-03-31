package it.polimi.rest.communication;

import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.Optional;
import java.util.function.Function;

/**
 * Extract the token ID from the Authorization header.
 *
 * Example header: Authorization Bearer tokenId
 */
public class TokenHeaderExtractor<T extends TokenId> implements TokenExtractor<T> {

    private final Function<String, T> supplier;

    public TokenHeaderExtractor(Function<String, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T extract(Request request) {
        return Optional.ofNullable(request.headers("Authorization"))
                .filter(header -> header.startsWith("Bearer"))
                .map(header -> header.substring("Bearer".length()).trim())
                .map(supplier)
                .orElse(null);
    }

}
