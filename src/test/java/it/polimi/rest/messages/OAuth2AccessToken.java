package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class OAuth2AccessToken {

    private OAuth2AccessToken() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final OAuth2Client.Id clientId;
        private final String callback;
        private final OAuth2Client.Secret secret;
        private final String code;
        private final String grantType;

        public Request(OAuth2Client.Id clientId, OAuth2Client.Secret secret, String callback, String code, String grantType) {
            this.clientId = clientId;
            this.callback = callback;
            this.secret = secret;
            this.code = code;
            this.grantType = grantType;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.post(baseUrl + "/oauth2/token");
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");

            if (clientId != null) {
                builder.addParameter("client_id", clientId.toString());
            }

            if (secret != null) {
                builder.addParameter("client_secret", secret.toString());
            }

            if (callback != null && !callback.isEmpty()) {
                builder.addParameter("redirect_uri", callback);
            }

            if (code != null) {
                builder.addParameter("code", code);
            }

            if (grantType != null) {
                builder.addParameter("grant_type", grantType);
            }

            builder.addParameter("response_type", "code");

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

        public final String access_token;

        public Response(String access_token) {
            this.access_token = access_token;
        }

    }

}
