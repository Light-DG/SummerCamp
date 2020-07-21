package com.example.garbagesort.text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.garbagesort.Parse;
import com.example.garbagesort.R;
import com.example.garbagesort.show.Show;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TextSearch extends AppCompatActivity implements SearchView.OnQueryTextListener , View.OnClickListener, AdapterView.OnItemClickListener,TextContract.textView {

    private static final String TAG = "TextSearch";

    TextPresenter presenter;

    SearchView search =null;
    ListView listView = null;
    int count = 0;
    List<String> datalist = null;
    ArrayAdapter adapter = null;
    Parse mParse = null;
    String rear = null;
    Timer mytimer = null;
    MyTimerTask myTimerTask = null;
    private static final int UPDATE_ADAPTER = 1;
    private static final int SET_INVISIBLE = 2;
    private static final int DIFFERENT_OR_NOT = 3;
    private static final int SHOW = 4;
    private static final int BUTTON_CLICKABLE = 5;
    String[] Texts = null;
    boolean differentJudge = false;
    TextView text01 = null;
    TextView text02 = null;
    TextView text03 = null;
    TextView text04 = null;
    TextView text05 = null;
    TextView text06 = null;
    TextView text07 = null;
    TextView text08 = null;
    TextView text09 = null;
    TextView text010 = null;
    String nowText = null;
    String cityId;
    String cityName;
    boolean alreadyClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_search);

        presenter = new TextPresenter(this);



        search = (SearchView)findViewById(R.id.search_view);
        listView = (ListView)findViewById(R.id.listview);



        listView.setOnItemClickListener(this);
        datalist = new ArrayList<String>();
        search.setFocusable(false);
        search.setOnQueryTextListener(this);
        mParse = new Parse();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        myTimerTask = new MyTimerTask();
        Texts = new String[2];
        ArrayList<String> hotWordList = getIntent().getStringArrayListExtra("hot_word");
        cityId = getIntent().getStringExtra("cityId");
        cityName = getIntent().getStringExtra("cityName");

        presenter.setCityId(cityId);

        setHotWord(hotWordList);
        setHotWordListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTimerTask.cancel();
    }

    public String getNowText() {
        return search.getQuery().toString();
    }

    public void updateAdapter(ArrayList<String> datalist){
        Message message =new Message();
        message.what = UPDATE_ADAPTER;
        handler.sendMessage(message);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_ADAPTER:
                    adapter.clear();
                    adapter.addAll(presenter.getDataList());
                    listView.setVisibility(View.VISIBLE);
                    break;

                case SET_INVISIBLE:
                    adapter.clear();
                    listView.setVisibility(View.INVISIBLE);
                    break;

                 //每隔200ms判断搜索框类的内容是否改变，
                //若改变，则网络申请获得“搜索热词”
                case DIFFERENT_OR_NOT:
                    if (Texts[0]==null)
                    {
                        Texts[0]=rear;
                    }else if (Texts[1]==null)
                    {
                        Texts[1]=rear;
                    }
                    else{
                        Texts[0]=Texts[1];
                        Texts[1]=rear;
                        if (!Texts[0].equals(Texts[1])){
                            differentJudge = true;
                        }else{
                            differentJudge = false;
                        }
                    }
                    break;

                case BUTTON_CLICKABLE:
                    alreadyClick = false;
                    break;

                default:
                    break;

            }
        }
    };


    public void sendButtonRenewMessage(){
        Message message = new Message();
        message.what = BUTTON_CLICKABLE;
        handler.sendMessageDelayed(message,1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.share).setVisible(false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        nowText = search.getQuery().toString();
        presenter.setNowText(nowText);
        presenter.sendRequest();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (count==0)
        {
            mytimer = new Timer();
            mytimer.schedule(myTimerTask,10,200);
            presenter.requireLinkWord();
            count++;
        }
        return false;
    }

    public void onHotWordClick(TextView text){
        if (!alreadyClick)
        {
            alreadyClick = true;
            sendButtonRenewMessage();
            nowText =text.getText().toString().substring(3);
            presenter.setNowText(nowText);
            Log.d(TAG, "调试： onHotWordClick: 发送请求前 ");
            presenter.sendRequest();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text01:
                onHotWordClick(text01);
                break;

            case R.id.text02:
                onHotWordClick(text02);
                break;

            case R.id.text03:
                onHotWordClick(text03);
                break;

            case R.id.text04:
                  onHotWordClick(text04);
                break;

            case R.id.text05:
                onHotWordClick(text05);
                break;

            case R.id.text06:
                onHotWordClick(text06);
                break;

            case R.id.text07:
                onHotWordClick(text07);
                break;

            case R.id.text08:
                onHotWordClick(text08);
                break;

            case R.id.text09:
                onHotWordClick(text09);
                break;

            case R.id.text010:
                onHotWordClick(text010);
                break;

            default:
                break;




        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        nowText = listView.getAdapter().getItem(position).toString();
        presenter.setNowText(nowText);
        presenter.sendRequest();
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            rear = search.getQuery().toString();
            Message message2 = new Message();
            message2.what=DIFFERENT_OR_NOT;
            handler.sendMessage(message2);
            if (rear.equals("")){
                Message message = new Message();
                message.what=SET_INVISIBLE;
                handler.sendMessage(message);
            }
            else if (differentJudge){
                presenter.requireLinkWord();
            }
        }
    }


    public void setHotWord(ArrayList<String> hotWordList){
        text01 = (TextView)findViewById(R.id.text01);
        text02 = (TextView)findViewById(R.id.text02);
        text03 = (TextView)findViewById(R.id.text03);
        text04 = (TextView)findViewById(R.id.text04);
        text05 = (TextView)findViewById(R.id.text05);
        text06 = (TextView)findViewById(R.id.text06);
        text07 = (TextView)findViewById(R.id.text07);
        text08 = (TextView)findViewById(R.id.text08);
        text09 = (TextView)findViewById(R.id.text09);
        text010 = (TextView)findViewById(R.id.text010);

        int size = hotWordList.size();
        if (size>0)
        {
            text01.setText(hotWordList.get(0));
        }
        if (size>1)
        {
            text02.setText(hotWordList.get(1));
        }
        if (size>2)
        {
            text03.setText(hotWordList.get(2));
        }
        if (size>3)
        {
            text04.setText(hotWordList.get(3));
        }
        if (size>4)
        {
            text05.setText(hotWordList.get(4));
        }
        if (size>5)
        {
            text06.setText(hotWordList.get(5));
        }
        if (size>6)
        {
            text07.setText(hotWordList.get(6));
        }
        if (size>7)
        {
            text08.setText(hotWordList.get(7));
        }
        if (size>8)
        {
            text09.setText(hotWordList.get(8));
        }
        if (size>9)
        {
            text010.setText(hotWordList.get(9));
        }


    }

    public void setHotWordListener(){
        text01.setOnClickListener(this);
        text02.setOnClickListener(this);
        text03.setOnClickListener(this);
        text04.setOnClickListener(this);
        text05.setOnClickListener(this);
        text06.setOnClickListener(this);
        text07.setOnClickListener(this);
        text08.setOnClickListener(this);
        text09.setOnClickListener(this);
        text010.setOnClickListener(this);
    }


    public void show(String s){
        switch (s) {
            case "success":
                Log.d(TAG, "调试 ：show: ");
                Intent intent = new Intent(TextSearch.this, Show.class);
                intent.putStringArrayListExtra("garbage_info",presenter.getResponseList());
                intent.putExtra("model","text");
                intent.putExtra("model02","NO");
                intent.putExtra("cityName",cityName);
                startActivity(intent);
                Log.d(TAG, "调试： show: 跳转后");
                presenter.getResponseList().clear();
                break;

            case "failure":
                showDialog();
                break;


            default:
                break;
        }
    }

    public void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(TextSearch.this);
        dialog.setTitle("查询失败");
        dialog.setMessage("未能找到匹配物品");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }



}
