package it.polimi.rest.models;

import it.polimi.rest.exceptions.UnsupportedMediaTypeException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class Image implements TokenAcceptor {

    public final ImageMetadata info;
    public final byte[] data;

    public Image(ImageMetadata info, byte[] data) {
        this.info = info;
        this.data = data;
        checkMediaType();
    }

    @Override
    public boolean accept(Token token) {
        return info.accept(token);
    }

    private void checkMediaType() {
        String mediaType = getMediaType();

        if (!mediaType.equals("image/gif") &&
                !mediaType.equals("image/png") &&
                !mediaType.equals("image/jpeg") &&
                !mediaType.equals("image/svg+xml")) {

            throw new UnsupportedMediaTypeException();
        }
    }

    public String getMediaType() {
        try (InputStream stream = new BufferedInputStream(new ByteArrayInputStream(data))) {
            return URLConnection.guessContentTypeFromStream(stream);

        } catch (IOException e) {
            throw new UnsupportedMediaTypeException("Can't determine media type");
        }
    }

}
