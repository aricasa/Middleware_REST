package it.polimi.rest.messages;

import it.polimi.rest.models.Link;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ImageAddMessage {

    private ImageAddMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final UserInfoMessage.Response userInfo;
        private final TokenId token;
        private final String title;
        private final File file;

        public Request(UserInfoMessage.Response userInfo, TokenId token, String title, File file) {
            this.userInfo = userInfo;
            this.token = token;
            this.title = title;
            this.file = file;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file", file)
                    .addTextBody("title", title)
                    .build();

            RequestBuilder requestBuilder = RequestBuilder
                    .post(baseUrl + userInfo.imagesLink())
                    .setEntity(entity);

            if (token != null) {
                requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer" + token);
            }

            HttpUriRequest request = requestBuilder.build();
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
        private Map<String, Link> _links;

        private Response() {

        }

        public Link selfLink() {
            return _links.get("self");
        }

        public Link authorLink() {
            return _links.get("author");
        }

        public Link rawLink() {
            return _links.get("describes");
        }

    }

}
