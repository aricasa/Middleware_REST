package it.polimi.rest.messages;

import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class ImageRaw {

    private ImageRaw() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        private final TokenId token;
        private final String username;
        private final Image.Id image;

        public Request(TokenId token, String username, Image.Id image) {
            this.token = token;
            this.username = username;
            this.image = image;
        }

        @Override
        public HttpResponse run(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(baseUrl + "/users/" + username + "/images/" + image + "/raw");

            if (token != null) {
                builder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.toString());
            }

            HttpUriRequest request = builder.build();
            HttpClient client = HttpClientBuilder.create().build();
            return client.execute(request);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        private Response() {

        }
    }

}
