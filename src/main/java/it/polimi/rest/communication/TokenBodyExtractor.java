package it.polimi.rest.communication;

import it.polimi.rest.models.TokenId;
import spark.Request;

import java.util.stream.Stream;

public class TokenBodyExtractor implements TokenExtractor {

    @Override
    public TokenId extract(Request request) {
        String body = request.body();

        if (body == null || body.isEmpty()) {
            return null;
        }

        return Stream.of(body.split("&"))
                .filter(entry -> entry.startsWith("token"))
                .findFirst()
                .map(entry -> entry.split("=")[1])
                .map(TokenId::new)
                .orElse(null);
    }

}
