package com.example.garbagesort.text;

import java.util.ArrayList;

public interface TextContract {

    interface textPresenter{
        void setCityId(String cityId);
        void setNowText(String nowText);
        void sendRequest();
        ArrayList<String> getResponseList();
        void requireLinkWord();
        ArrayList<String> getDataList();
    }

    interface textView{
        void show(String s);
        String getNowText();
        void updateAdapter(ArrayList<String> datalist);

    }


}
