package it.polimi.rest.messages;

import it.polimi.rest.models.ImageMetadata;

public class ImageDeletionMessage extends ImageDetailsMessage {

    public ImageDeletionMessage(ImageMetadata metadata) {
        super(metadata);
    }

    @Override
    public int code() {
        return HttpStatus.NO_CONTENT;
    }

}
