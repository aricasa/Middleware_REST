package it.polimi.rest.communication.messages.image;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.ImageMetadata;

import java.util.Optional;

class Details implements Message {

    private final ImageMetadata metadata;

    public Details(ImageMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return APPLICATION_HAL_JSON;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(metadata);
    }

}
