package com.rmali.catfacts.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rmali on 11/8/2017.
 */

public class CatFactEntity {
    @SerializedName("fact")
    @Expose
    private String fact;
    @SerializedName("length")
    @Expose
    private int length;

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
