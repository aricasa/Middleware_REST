package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class ImageRemoveMessage {

    private ImageRemoveMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final ImageInfoMessage.Response imageInfo;
        private final TokenId token;

        public Request(ImageInfoMessage.Response imageInfo, TokenId token) {
            this.imageInfo = imageInfo;
            this.token = token;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.delete(imageInfo.selfLink().toString());

            if (token != null) {
                builder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.toString());
            }

            HttpUriRequest request = builder.build();
            HttpClient client = HttpClientBuilder.create().build();
            return client.execute(request);
        }

        @Override
        public Response response(String baseUrl) throws IOException {
            HttpResponse response = rawResponse(baseUrl);
            return parseJson(response, Response.class);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        private Response() {

        }

    }

}
