package it.polimi.rest.models.oauth2;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAuth2AuthorizationRequest {

    @Expose
    @SerializedName("response_type")
    public final String responseType;

    @Expose
    @SerializedName("client_id")
    @JsonAdapter(ClientIdAdapter.class)
    public final OAuth2Client.Id client;

    @Expose
    @SerializedName("redirect_uri")
    public final String callback;

    @Expose
    @SerializedName("scope")
    @JsonAdapter(ScopesAdapter.class)
    public final Collection<String> scopes;

    @Expose
    @SerializedName("state")
    public final String state;

    public OAuth2AuthorizationRequest(String responseType, OAuth2Client.Id client, String callback, Collection<String> scopes, String state) {
        this.responseType = responseType;
        this.client = client;
        this.callback = callback;
        this.scopes = Collections.unmodifiableCollection(new ArrayList<>(scopes));
        this.state = state;
    }

    static class ClientIdAdapter implements JsonDeserializer<OAuth2Client.Id> {

        @Override
        public OAuth2Client.Id deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new OAuth2Client.Id(json.getAsString());
        }

    }

    static class ScopesAdapter implements JsonDeserializer<Collection<String>> {

        @Override
        public Collection<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String[] scopes = json.getAsString().split(" ");
            return Stream.of(scopes).collect(Collectors.toList());
        }

    }

}
