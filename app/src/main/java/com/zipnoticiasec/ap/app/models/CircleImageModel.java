package com.zipnoticiasec.ap.app.models;

/**
 * Created by Andres on 16/6/2019.
 */

public class CircleImageModel {

    public String url_image;
    public Boolean isSelected;

    public CircleImageModel(String url_image, Boolean isSelected) {
        this.url_image = url_image;
        this.isSelected = isSelected;
    }
}
