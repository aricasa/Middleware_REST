package it.polimi.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link {

    @Expose
    @SerializedName("href")
    public final String url;

    public Link(String url) {
        this.url = url;
    }

}
