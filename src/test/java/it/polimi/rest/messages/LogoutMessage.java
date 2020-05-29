package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class LogoutMessage {

    private LogoutMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final RootMessage.Response rootLinks;
        private final TokenId token;
        private final String session;

        public Request(RootMessage.Response rootLinks, TokenId token, String session) {
            this.rootLinks = rootLinks;
            this.token = token;
            this.session = session;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.delete(rootLinks.sessionLink().url +"/" + session);

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
