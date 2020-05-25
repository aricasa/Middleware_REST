package it.polimi.rest.messages;

import it.polimi.rest.models.Image;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageRawMessage {

    private ImageRawMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final UserInfoMessage.Response userInfo;
        private final TokenId token;
        private final Image.Id image;

        public Request(UserInfoMessage.Response userInfo, TokenId token, Image.Id image) {
            this.token = token;
            this.userInfo = userInfo;
            this.image = image;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(baseUrl + userInfo.imagesLink().url + "/" + image + "/raw");

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

            ByteArrayOutputStream downloadedImg = new ByteArrayOutputStream();
            response.getEntity().writeTo(downloadedImg);
            byte[] data = downloadedImg.toByteArray();

            return new Response(data);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public final byte[] data;

        public Response(byte[] data) {
            this.data = data;
        }

    }

}
