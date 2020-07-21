package com.example.garbagesort.voice;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyAudioRecord {

    private AudioRecord recorder = null;
    private String pcmFilePath = "";
    private String wavFilePath = "";
    private static final int Channel = 1;
    private static final int sampleRate = 16000;
    private int format = AudioFormat.ENCODING_PCM_16BIT;
    private boolean isRecording = false;
    private int bufferSize = 0;

    private static final String TAG = "MyAudioRecord";

    public MyAudioRecord(String pcmFilePath, String wavFilePath){
        this.pcmFilePath = pcmFilePath;
        this.wavFilePath = wavFilePath;
    }

    public void recordAndSaveFile()
    {
        if (recorder==null){
            createRecorder();
            Log.d(TAG, "调试：recordAndSaveFile1 ");
        }
        recorder.startRecording();
        isRecording = true;
        new Thread(new AudioRecordThread()).start();

    }

    private void createRecorder() {
        bufferSize = AudioRecord.getMinBufferSize(sampleRate,Channel,format);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,Channel, AudioFormat.ENCODING_PCM_16BIT,bufferSize);

    }

    public void close(){
        if (recorder!=null)
        {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    class AudioRecordThread implements Runnable{

        @Override
        public void run() {
            writeDataToFile();
            copyWavFile(pcmFilePath,wavFilePath);
        }
    }

    private void copyWavFile(String pcmFilePath, String wavFilePath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen+36;
        long byteRate = 16*sampleRate*Channel/8;
        byte[] data = new byte[bufferSize];
        int len = 0;
        try {
            fis = new FileInputStream(pcmFilePath);
            fos = new FileOutputStream(wavFilePath);
            totalAudioLen = fis.getChannel().size();
            totalDataLen = totalAudioLen+36;
            WriteWaveFileHeader(fos, totalAudioLen, totalDataLen,
                    sampleRate, Channel, byteRate);
            while((len=fis.read(data))!=-1){
                fos.write(data,0,len);
            }
            if (fis!=null)
            {
                fis.close();
            }
            if (fos!=null)
            {
                fos.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void WriteWaveFileHeader(FileOutputStream fos, long totalAudioLen, long totalDataLen, int sampleRate,
                                     int channel, long byteRate) throws IOException {

        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) Channel;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        fos.write(header, 0, 44);

    }

    private void writeDataToFile() {
        File pcmFile = new File(pcmFilePath);
        byte[] bufferArray = new byte[bufferSize];
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(pcmFile));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (isRecording==true){
            int readSize = recorder.read(bufferArray,0,bufferSize);
            if (readSize>0){
                try {
                    bos.write(bufferArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (bos!=null)
            {
                bos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
