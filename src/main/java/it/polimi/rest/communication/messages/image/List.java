package it.polimi.rest.communication.messages.image;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.ImagesList;

import java.util.Optional;

class List implements Message {

    private final ImagesList images;

    public List(ImagesList images) {
        this.images = images;
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
        return Optional.ofNullable(images);
    }

}
