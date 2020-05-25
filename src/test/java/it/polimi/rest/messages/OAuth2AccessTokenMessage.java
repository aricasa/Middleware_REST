package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2AuthorizationCode;
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

public class OAuth2AccessTokenMessage {

    private OAuth2AccessTokenMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final OAuth2Client.Id clientId;
        private final String callback;
        private final OAuth2Client.Secret secret;
        private final OAuth2AuthorizationCode.Id authCode;
        private final String grantType;

        public Request(OAuth2Client.Id clientId, OAuth2Client.Secret secret, String callback, OAuth2AuthorizationCode.Id authCode, String grantType) {
            this.clientId = clientId;
            this.callback = callback;
            this.secret = secret;
            this.authCode = authCode;
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

            if (authCode != null) {
                builder.addParameter("code", authCode.toString());
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
        public final String token_type;
        public final String expires_in;
        public final String refresh_token;

        public Response(String access_token, String token_type, String expires_in, String refresh_token) {
            this.access_token = access_token;
            this.token_type = token_type;
            this.expires_in = expires_in;
            this.refresh_token = refresh_token;
        }

    }

}
