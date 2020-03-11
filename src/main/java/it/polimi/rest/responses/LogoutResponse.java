package it.polimi.rest.responses;

import java.util.Map;
import java.util.Optional;

public final class LogoutResponse extends Response {

    public LogoutResponse() {
        super(200, APPLICATION_JSON, null);
    }

    @Override
    public Optional<String> self() {
        return Optional.empty();
    }

}
