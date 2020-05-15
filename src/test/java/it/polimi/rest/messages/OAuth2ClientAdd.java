package it.polimi.rest.messages;

import com.google.gson.annotations.Expose;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class OAuth2ClientAdd {

    private OAuth2ClientAdd() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final UserInfo.Response userInfo;
        private final TokenId token;

        @Expose
        private final String name;

        @Expose
        private final String callback;

        public Request(UserInfo.Response userInfo, TokenId token, String name, String callback) {
            this.token = token;
            this.userInfo = userInfo;
            this.name = name;
            this.callback = callback;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder requestBuilder = RequestBuilder
                    .post(baseUrl + userInfo.oAuth2ClientsLink().url)
                    .setEntity(jsonEntity());

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

        public String name;
        public String callback;
        public String id;
        public String secret;

        private Response() {

        }

    }

}
