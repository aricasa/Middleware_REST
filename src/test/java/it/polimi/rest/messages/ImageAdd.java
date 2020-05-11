package it.polimi.rest.messages;

import com.google.gson.annotations.Expose;
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

public class ImageAdd {

    private ImageAdd() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        private TokenId token;
        private final String username;
        private final String title;
        private final File file;

        public Request(TokenId token, String username, String title, File file) {
            this.token = token;
            this.username = username;
            this.title = title;
            this.file = file;
        }

        @Override
        public HttpResponse run(String baseUrl) throws IOException {
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file", file)
                    .addTextBody("title", title)
                    .build();

            RequestBuilder requestBuilder = RequestBuilder
                    .post(baseUrl + "/users/" + username + "/images")
                    .setEntity(entity);

            if (token != null) {
                requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer" + token);
            }

            HttpUriRequest request = requestBuilder.build();
            HttpClient client = HttpClientBuilder.create().build();

            return client.execute(request);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String title;

        private Response() {

        }

    }

}
