package com.example.garbagesort.image;

import android.content.Intent;

import java.util.ArrayList;

public interface ImageContract {

    interface imageView{
        String getPhotoPath();
        void resultShow();
    }

    interface imagePresent{
        ArrayList<String> getResponseList();
        void handlePhoto();
        void setCity(String s);
    }

}
