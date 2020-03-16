package it.polimi.rest.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.Objects;

@JsonAdapter(TokenId.Serializer.class)
public class TokenId implements TokenAcceptor {

    @Expose
    private final String id;

    public TokenId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenId tokenId = (TokenId) o;
        return id.equals(tokenId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean accept(Token token) {
        return token.id.equals(this);
    }

    public static class Serializer implements JsonSerializer<TokenId> {

        @Override
        public JsonElement serialize(TokenId src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.id);
        }

    }

}
