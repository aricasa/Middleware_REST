package it.polimi.rest.adapters;

import it.polimi.rest.credentials.CredentialsManager;
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

import static java.util.UUID.randomUUID;

public class ImageDeserializer implements Deserializer<Image> {

    private final String usernameParam;
    private final CredentialsManager credentialsManager;
    private final DataProvider dataProvider;

    public ImageDeserializer(String usernameParam, CredentialsManager credentialsManager, DataProvider dataProvider) {
        this.usernameParam = usernameParam;
        this.credentialsManager = credentialsManager;
        this.dataProvider = dataProvider;
    }

    @Override
    public Image parse(Request request, TokenId token) {
        String username = request.params(usernameParam);
        User user = credentialsManager.userByUsername(username);

        ImageId id;

        do {
            id = new ImageId(randomUUID().toString());
        } while (dataProvider.contains(id));

        try {
            Part titlePart = request.raw().getPart("title");
            String title = IOUtils.toString(titlePart.getInputStream(), Charset.defaultCharset()).trim();

            if (title.isEmpty()) {
                throw new BadRequestException();
            }

            Part filePart = request.raw().getPart("file");
            InputStream stream = filePart.getInputStream();

            ImageMetadata metadata = new ImageMetadata(id, title, user.id);
            return new Image(metadata, IOUtils.toByteArray(stream));

        } catch (IOException | ServletException e) {
            throw new BadRequestException();
        }
    }

}