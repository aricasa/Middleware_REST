package it.polimi.rest.messages;

import it.polimi.rest.models.ImageMetadata;

import java.util.Optional;

public class ImageDetailsMessage implements Message {

    private final ImageMetadata metadata;

    public ImageDetailsMessage(ImageMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return APPLICATION_JSON;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(metadata);
    }

}
