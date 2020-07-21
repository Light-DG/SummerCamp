package com.example.garbagesort.home;

import java.util.ArrayList;

public interface HomeContract {

    interface homePresenter{
        void requireHotWord();
        ArrayList<String> getHotWordList();
        void setCityId(String s);
        int getNowChecked();
        String getCityId();
        String getCityName();
        void clearFiles();
        int SetLastCity();
    }

    interface homeView{
        void turnToTextSearchPage(ArrayList<String> hotWordList);
        void setCityName(String s);
        void uncheckOthers();
    }


}
