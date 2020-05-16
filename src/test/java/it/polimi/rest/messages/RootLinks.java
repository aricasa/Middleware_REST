package it.polimi.rest.messages;

import it.polimi.rest.models.Link;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class RootLinks {

    private RootLinks() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        public Request() {
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder requestBuilder = RequestBuilder
                    .get(baseUrl + "/");

            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
            HttpUriRequest request = requestBuilder.build();
            return clientBuilder.build().execute(request);
        }

        @Override
        public Response response(String baseUrl) throws IOException {
            return parseJson(rawResponse(baseUrl), Response.class);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public Map<String, Link> _links;

        private Response() {
        }

        public Link sessionLink() { return _links.get("sessions"); }

        public Link usersLink() { return _links.get("users"); }

        public Link selfLink() { return  _links.get("self"); }
    }

}
