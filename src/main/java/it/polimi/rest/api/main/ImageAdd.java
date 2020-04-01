package it.polimi.rest.api.main;

import it.polimi.rest.authorization.SessionManager;
import it.polimi.rest.communication.Responder;
import it.polimi.rest.communication.TokenExtractor;
import it.polimi.rest.communication.TokenHeaderExtractor;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.communication.messages.image.ImageMessage;
import it.polimi.rest.data.DataProvider;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.models.*;
import org.apache.commons.io.IOUtils;
import spark.Request;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Add a new image.
 */
class ImageAdd extends Responder<TokenId, ImageAdd.Data> {

    private final SessionManager sessionManager;

    public ImageAdd(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected Optional<TokenExtractor<TokenId>> tokenExtractor() {
        return Optional.of(new TokenHeaderExtractor<>(TokenId::new));
    }

    @Override
    protected Data deserialize(Request request) {
        String username = request.params("username");

        try {
            Part titlePart = request.raw().getPart("title");
            String title = IOUtils.toString(titlePart.getInputStream(), Charset.defaultCharset()).trim();

            Part filePart = request.raw().getPart("file");
            InputStream stream = filePart.getInputStream();

            byte[] data = IOUtils.toByteArray(stream);

            return new Data(username, title, data);

        } catch (IOException | ServletException e) {
            throw new BadRequestException();
        }
    }

    @Override
    protected Message process(TokenId token, Data data) {
        DataProvider dataProvider = sessionManager.dataProvider(token);

        User user = dataProvider.userByUsername(data.username);

        ImageMetadata metadata = new ImageMetadata(
                dataProvider.uniqueId(Id::randomizer, Image.Id::new),
                data.title,
                user
        );

        Image image = new Image(metadata, data.data);
        dataProvider.add(image);

        logger.d("Image " + image.info.id + " added");
        return ImageMessage.creation(image.info);
    }

    protected static class Data {

        public final String username;
        public final String title;
        public final byte[] data;

        public Data(String username, String title, byte[] data) {
            this.username = username;
            this.title = title;
            this.data = data;
        }

    }

}
