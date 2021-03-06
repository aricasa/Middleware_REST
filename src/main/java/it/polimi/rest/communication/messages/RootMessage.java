package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.Root;

import java.util.Optional;

public class RootMessage implements Message {

    private final Root root;

    public RootMessage(Root root) {
        this.root = root;
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return "application/hal+json";
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(root);
    }

}
