package it.polimi.rest.communication.messages.oauth2;

import com.google.common.io.Resources;
import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.exceptions.BadRequestException;
import it.polimi.rest.exceptions.InternalErrorException;
import it.polimi.rest.models.oauth2.OAuth2Client;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class OAuth2LoginPage implements Message {

    public final String html;

    @SuppressWarnings("UnstableApiUsage")
    public OAuth2LoginPage(String clientId, String redirectUri, Collection<String> scope, String state) {
        try {
            URL url = Resources.getResource("authorize.html");

            this.html = Resources.toString(url, StandardCharsets.UTF_8)
                    .replace("{:clientId}", clientId)
                    .replace("{:callback}", redirectUri)
                    .replace("{:scopes}", scope.stream().map(Object::toString).collect(Collectors.joining(" ")))
                    .replace("{:state}", state);

        } catch (IOException e) {
            throw new InternalErrorException();
        }
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return TEXT_HTML;
    }

    @Override
    public Optional<Object> payload() {
        return Optional.of(html);
    }

}
