package com.example.garbagesort.voice;

import java.util.ArrayList;

public interface VoiceContract {

    interface voicePresenter{
        ArrayList<String> getResponseList();
        void requireMessage();
        void SetFileNameAndPath();
        String getPcmFilePath();
        String getWavFielPath();
        void setCity(String s);
    }

    interface voiceView{
        void showResult();
    }

}
