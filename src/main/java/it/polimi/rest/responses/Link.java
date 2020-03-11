package it.polimi.rest.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link {

    @Expose
    @SerializedName("rel")
    public final String name;

    @Expose
    @SerializedName("href")
    public final String url;

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }

}
