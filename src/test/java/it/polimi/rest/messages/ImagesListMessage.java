package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class ImagesListMessage {

    private ImagesListMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final UserInfoMessage.Response userInfo;
        private final TokenId token;

        public Request(UserInfoMessage.Response userInfo, TokenId token) {
            this.userInfo = userInfo;
            this.token = token;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(baseUrl + userInfo.imagesLink());

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

        public int count;

        private Response() {

        }

    }

}
