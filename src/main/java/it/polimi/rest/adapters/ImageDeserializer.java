package it.polimi.rest.adapters;

import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.models.*;
import org.apache.commons.io.IOUtils;
import spark.Request;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ImageDeserializer implements Deserializer<Image> {

    private final String usernameParam;

    public ImageDeserializer(String usernameParam) {
        this.usernameParam = usernameParam;
    }

    @Override
    public Image parse(Request request) {
        String username = request.params(usernameParam);
        User user = new User(null, username, null);

        try {
            Part titlePart = request.raw().getPart("title");
            String title = IOUtils.toString(titlePart.getInputStream(), Charset.defaultCharset()).trim();

            Part filePart = request.raw().getPart("file");
            InputStream stream = filePart.getInputStream();

            ImageMetadata metadata = new ImageMetadata(null, title, user);
            return new Image(metadata, IOUtils.toByteArray(stream));

        } catch (IOException | ServletException e) {
            throw new BadRequestException();
        }
    }

}
