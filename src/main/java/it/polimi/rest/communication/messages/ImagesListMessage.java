package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.ImagesList;

import java.util.Optional;

public class ImagesListMessage implements Message {

    private final ImagesList images;

    public ImagesListMessage(ImagesList images) {
        this.images = images;
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
        return Optional.ofNullable(images);
    }

}
