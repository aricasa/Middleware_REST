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

public class ImageInfo {

    private ImageInfo() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final UserInfo.Response userInfo;
        private final TokenId token;
        private final String username;
        private final Image.Id image;

        public Request (UserInfo.Response userInfo, TokenId token, String username, Image.Id image) {
            this.userInfo = userInfo;
            this.token = token;
            this.username = username;
            this.image = image;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(baseUrl + userInfo.imagesLink().url + "/" + image);

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

        public String id;
        public String title;

        private Response() {

        }

    }

}
