package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class OAuth2ClientRemoveMessage {

    private OAuth2ClientRemoveMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final UserInfoMessage.Response userInfo;
        private final TokenId token;
        private final OAuth2Client.Id client;

        public Request(UserInfoMessage.Response userInfo, TokenId token, OAuth2Client.Id client) {
            this.userInfo = userInfo;
            this.token = token;
            this.client = client;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder requestBuilder = RequestBuilder
                    .delete(baseUrl + userInfo.oAuth2ClientsLink().url + "/" + client)
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

        private Response() {

        }

    }

}
