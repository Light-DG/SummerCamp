package com.example.garbagesort.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.garbagesort.DataBase.LastCity;
import com.example.garbagesort.DataBase.LastCityDao;
import com.example.garbagesort.DataBase.NewGarbageDatabase;
import com.example.garbagesort.HigherAccuracy;
import com.example.garbagesort.history.History;
import com.example.garbagesort.image.ImageSearch;
import com.example.garbagesort.KnowledgeShow;
import com.example.garbagesort.Meaning;
import com.example.garbagesort.R;
import com.example.garbagesort.voice.VoiceSearch;
import com.example.garbagesort.DataBase.dbHelper;
import com.example.garbagesort.text.TextSearch;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener , DrawerLayout.DrawerListener ,HomeContract.homeView{

    private static final String TAG = "MainActivity";

    HomeContract.homePresenter presenter;

//    Parse myParse = null;
//    ArrayList<String> hotWordList = null;
    ImageView microphonePicture = null;

    ImageView cameraPicture = null;
    public static final int NEXT_PICTURE = 5;     //让ViewPager更换的字段
    public static final int BUTTON_CLICKABLE = 6;
    private DrawerLayout mDrawerLayout;
    boolean drawerIsOut = false;
    TextView nowCity = null;
    ImageView arrow;
    TextView clearData = null;
    TextView textHistory = null;
    ViewPager BannerViewPager = null;
    TextView BannerTitle = null;
    LinearLayout BannerPointLayout = null;
    ArrayList<ImageView> mImageViews = null;
    int prePosition = 0;
    boolean isDragging = false;
    int imageCount = 0;
    boolean alreadyClick = false;     //用于防止快速多次点击同一个按钮





    String[] TitleTexts = {
            "垃圾分类知识",
            "提高识别率小技巧",
            "垃圾分类的意义",
    };
    int[] imageIds={
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3
    };

    LinearLayout ciyt1;
    LinearLayout ciyt2;
    LinearLayout ciyt3;
    LinearLayout ciyt4;
    LinearLayout ciyt5;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    List<CheckBox> checkBoxesList;
//    int nowChecked = 3;
//    public String cityId = null;
//    String cityName = null;
    int clickArrowCount = 0;

//    private Handler mhandler = new Handler();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        presenter = new HomePresenter(this);

        presenter.requireHotWord();
        //一打开程序，就启动 获得当天热词的线程


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.daohang);
        }

        //与轮播图有关
        BannerViewPager = (ViewPager) findViewById(R.id.viewPager);
        BannerTitle = (TextView)findViewById(R.id.BannerTitle);
        BannerPointLayout = (LinearLayout)findViewById(R.id.BannerPointLayout);
        mImageViews = new ArrayList<ImageView>();
        for (int i=0;i<imageIds.length;i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageIds[i]);
            mImageViews.add(imageView);
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20,20);

            if (i==0){
                point.setEnabled(true);
            }else{
                point.setEnabled(false);
                params.leftMargin = 20;
            }
            point.setLayoutParams(params);
            BannerPointLayout.addView(point);

        }


        BannerViewPager.setAdapter(new MyPageAdapter());
        BannerViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        BannerViewPager.setCurrentItem(Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2%mImageViews.size()));
        viewPagerhandler.sendEmptyMessageDelayed(0,4000);


        checkBoxesList = new ArrayList<CheckBox>();
        ciyt1 = (LinearLayout)findViewById(R.id.city_1);
        ciyt2 = (LinearLayout)findViewById(R.id.city_2);
        ciyt3 = (LinearLayout)findViewById(R.id.city_3);
        ciyt4 = (LinearLayout)findViewById(R.id.city_4);
        ciyt5 = (LinearLayout)findViewById(R.id.city_5);
        nowCity = (TextView) findViewById(R.id.now_city);
        ciyt5.setVisibility(View.INVISIBLE);
        ciyt4.setVisibility(View.INVISIBLE);
        ciyt3.setVisibility(View.INVISIBLE);
        ciyt2.setVisibility(View.INVISIBLE);
        ciyt1.setVisibility(View.INVISIBLE);
        nowCity.setVisibility(View.INVISIBLE);
        checkBox1 = (CheckBox)findViewById(R.id.checkbox_1);
        checkBox2 = (CheckBox)findViewById(R.id.checkbox_2);
        checkBox3 = (CheckBox)findViewById(R.id.checkbox_3);
        checkBox4 = (CheckBox)findViewById(R.id.checkbox_4);
        checkBox5 = (CheckBox)findViewById(R.id.checkbox_5);
        arrow = (ImageView) findViewById(R.id.image_arrow);

        checkBoxesList.add(checkBox1);
        checkBoxesList.add(checkBox2);
        checkBoxesList.add(checkBox3);
        checkBoxesList.add(checkBox4);
        checkBoxesList.add(checkBox5);

        checkBox3.setChecked(true);

        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBox4.setOnClickListener(this);
        checkBox5.setOnClickListener(this);
        arrow.setOnClickListener(this);

        //将上次退出程序前的城市的复选框打上勾
        checkBoxesList.get(presenter.SetLastCity()-1).setChecked(true);



        LinearLayout searchG = (LinearLayout)findViewById(R.id.search_g);
        searchG.setOnClickListener(this);
        ImageView searchIcon = (ImageView)findViewById(R.id.search_icon);
        textHistory = (TextView)findViewById(R.id.text_history);
        clearData = (TextView)findViewById(R.id.text_clear_data);

        clearData.setOnClickListener(this);
        textHistory.setOnClickListener(this);
        searchIcon.setOnClickListener(this);
        microphonePicture = (ImageView) findViewById(R.id.image_microphone);
        microphonePicture.setOnClickListener(this);
        cameraPicture = (ImageView)findViewById(R.id.image_camera);
        cameraPicture.setOnClickListener(this);
        nowCity = (TextView)findViewById(R.id.now_city);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }




    }


    //专门用来处理轮播图的Handler
    private Handler viewPagerhandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

                    //利用Handler自动轮循
                    int item = BannerViewPager.getCurrentItem()+1;
                    BannerViewPager.setCurrentItem(item);
                    viewPagerhandler.sendEmptyMessageDelayed(NEXT_PICTURE,4000);  //类似循环，定时滚动

//                case BUTTON_CLICKABLE:
//                    alreadyClick = false;
//                    Log.d(TAG, "调试： alreadyClick3: "+alreadyClick);
//                    break;


        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
        handler.sendMessageDelayed(message,400);
    }


    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
//        Log.d(TAG, "调试：onDrawerOpened: ");
        drawerIsOut = true;
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
//        Log.d(TAG, "调试： onDrawerClosed");
        drawerIsOut = false;
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }


    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //根据图片变化更换标题
            BannerTitle.setText(TitleTexts[position % imageIds.length]);
            //上一个点变成灰色
            BannerPointLayout.getChildAt(prePosition % imageIds.length).setEnabled(false);
            //现在这个点变成红色
            BannerPointLayout.getChildAt(position % imageIds.length).setEnabled(true);
            prePosition = position ;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            /*
               SCROLL_STATE_DRAGGING:   用户拖动
               SCROLL_STATE_IDLE:       闲置状态（静）
               SCROLL_STATE_SETTLING：  缓冲（从动->静）
             */
            if (ViewPager.SCROLL_STATE_DRAGGING == state) {
                isDragging = true;
                viewPagerhandler.removeCallbacksAndMessages(null);      //只要是手指拖动，就暂停轮循，直到松开手指
            } else if (ViewPager.SCROLL_STATE_IDLE == state) {
            } else if (ViewPager.SCROLL_STATE_SETTLING == state &&isDragging) {
                //用 &&isDraging 表示用手指拖动造成的，将 自动滚动的情况 排除在外
                viewPagerhandler.sendEmptyMessageDelayed(NEXT_PICTURE,4000);    //手指拖动完成后就继续轮循
                isDragging = false;
            }
        }

    }


        //自己定义的PageAdapter
        class MyPageAdapter extends PagerAdapter {

            @Override
            //该方法为空，变为在实例时删除
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                imageCount++;
                ImageView imageView = mImageViews.get(position % imageIds.length);
                imageView.setTag(position % imageIds.length);
                container.removeView(imageView);    //用手反方向滑动时，（position % imageIds.length）不变（不会轮循环地变为下一个值，如0->1,1->2,2->0）
                                                    //此时如果不先remove,会重复加入同一个实例。所以要先remove,并且重写destroyItem为空方法
                container.addView(imageView);
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                viewPagerhandler.removeCallbacksAndMessages(null);
                                break;

                            case MotionEvent.ACTION_MOVE:
                                //ACTION_MOVE在手指拖动时一般不止触发一次（可以打印信息观察），所以每次向Handler发信息前
                                //都要把之前的留在Query里的message清空，以防滚动不合理
                                viewPagerhandler.removeCallbacksAndMessages(null);
                                viewPagerhandler.sendEmptyMessageDelayed(NEXT_PICTURE, 4000);
                                break;


                            case MotionEvent.ACTION_UP:
                                //手指抬起时，不写时间，因为手指拖动后松手 可能没有触发，而ACTION_MOVE一定会有
                                break;

                            default:
                                break;

                        }
                        return false;
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch ((int)v.getTag()){
                            case 0:
                                if(!drawerIsOut&&!alreadyClick){
                                    alreadyClick = true;
                                    sendButtonRenewMessage();
                                    Intent intent = new Intent(v.getContext(), KnowledgeShow.class);
                                    startActivity(intent);
                                }
                                break;

                            case 1:
                                if (!drawerIsOut&&!alreadyClick){
                                    alreadyClick = true;
                                    sendButtonRenewMessage();
                                    Intent intent = new Intent(v.getContext(), HigherAccuracy.class);
                                    startActivity(intent);
                                }
                                break;

                            case 2:
                                if (!drawerIsOut&&!alreadyClick){
                                    alreadyClick = true;
                                    sendButtonRenewMessage();
                                    Intent intent = new Intent(v.getContext(), Meaning.class);
                                    startActivity(intent);
                                }
                                break;

                            default:
                                break;

                        }
                    }
                });
                return imageView;
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;  //设置为无限轮循
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        }


        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    break;




                default:
                    break;
            }
            return true;
        }


//        public void sendSwitchPageMessage()
//        {
//            Message message = new Message();
//            message.what = NEXT_PICTURE;
//            handler.sendMessageDelayed(message,4000);
//        }

        public void turnToTextSearchPage(ArrayList<String> hotWordList){
            if (hotWordList!=null&&!hotWordList.isEmpty()&&!drawerIsOut&&!alreadyClick) {
                alreadyClick = true;
                sendButtonRenewMessage();
                Intent intent = new Intent(MainActivity.this, TextSearch.class);
                intent.putStringArrayListExtra("hot_word", hotWordList);
//                setCityId();
                intent.putExtra("cityId", presenter.getCityId());
                intent.putExtra("cityName",presenter.getCityName());
                startActivity(intent);
            }

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.search_g :
                    if (presenter!=null) {
                        turnToTextSearchPage(presenter.getHotWordList());
                    }
                    break;

                case R.id.search_icon:
                        if (presenter!=null){
                            turnToTextSearchPage(presenter.getHotWordList());
                        }
                    break;

                case R.id.image_microphone:
                    if (!drawerIsOut&&!alreadyClick&&presenter!=null){
                        alreadyClick = true;
                        sendButtonRenewMessage();
                        Log.d(TAG, "调试 already2: "+alreadyClick);
                        Intent intent4 = new Intent(MainActivity.this, VoiceSearch.class);
//                        setCityId();
                        intent4.putExtra("cityId", presenter.getCityId());
                        intent4.putExtra("cityName",presenter.getCityName());
                        startActivity(intent4);
                    }

                    break;

                case R.id.image_camera:
                    if (!drawerIsOut&&!alreadyClick&&presenter!=null){
                        alreadyClick = true;
                        sendButtonRenewMessage();
                        Intent intent3 = new Intent(MainActivity.this, ImageSearch.class);
                        intent3.putExtra("cityId", presenter.getCityId());
                        intent3.putExtra("cityName",presenter.getCityName());
                        startActivity(intent3);
                    }

                    break;

                case R.id.text_history:
                    if (!alreadyClick){
                        alreadyClick = true;
                        sendButtonRenewMessage();
                        Intent intent5 = new Intent(MainActivity.this, History.class);
                        startActivity(intent5);
                    }

                    break;

                case R.id.text_clear_data:
                    //对话框提醒用户

                    if (!alreadyClick&&presenter!=null){
                        alreadyClick = true;
                        sendButtonRenewMessage();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("是否确认清空数据？");
                        dialog.setMessage("删除的数据(照片，录音文件)将无法恢复");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.clearFiles();
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    }

                    break;

                case R.id.checkbox_1:
                    if (checkBox1.isChecked()&&presenter!=null) {
                        presenter.setCityId("北京市");
                        uncheckOthers();
                    }
                    break;

                case R.id.checkbox_2:
                    if (checkBox2.isChecked()&&presenter!=null) {
                        presenter.setCityId("深圳市");
                        uncheckOthers();
                    }
                    break;

                case R.id.checkbox_3:
                    if (checkBox3.isChecked()&&presenter!=null) {
                        presenter.setCityId("上海市");
                        uncheckOthers();
                    }
                    break;

                case R.id.checkbox_4:
                    if (checkBox4.isChecked()&&presenter!=null) {
                        presenter.setCityId("西安市");
                        uncheckOthers();
                    }
                    break;

                case R.id.checkbox_5:
                    if (checkBox5.isChecked()&&presenter!=null) {
                        presenter.setCityId("宁波市");
                        uncheckOthers();
                    }
                    break;

                case R.id.image_arrow:
                    clickArrowCount++;
                    Log.d(TAG, "调试： onClick: ");
                    if (clickArrowCount % 2 == 1) {
                        arrow.setImageResource(R.mipmap.shangjiantou);
                        nowCity.setVisibility(View.VISIBLE);
                        ciyt1.setVisibility(View.VISIBLE);
                        ciyt2.setVisibility(View.VISIBLE);
                        ciyt3.setVisibility(View.VISIBLE);
                        ciyt4.setVisibility(View.VISIBLE);
                        ciyt5.setVisibility(View.VISIBLE);
                    } else {
                        arrow.setImageResource(R.mipmap.xialajiantouxiao);
                        ciyt5.setVisibility(View.INVISIBLE);
                        ciyt4.setVisibility(View.INVISIBLE);
                        ciyt3.setVisibility(View.INVISIBLE);
                        ciyt2.setVisibility(View.INVISIBLE);
                        ciyt1.setVisibility(View.INVISIBLE);
                        nowCity.setVisibility(View.INVISIBLE);

                    }
                    break;


                default:
                    break;

            }

        }

        public void setCityName(String s){
            nowCity.setText(s);
        }

        //取消 除已选中外的 其他城市 的选中状态
        public void uncheckOthers() {
            for (int i = 0; i < 5; i++) {
                if (presenter.getNowChecked() == i + 1) {
                    continue;
                }
                checkBoxesList.get(i).setChecked(false);
            }
        }

    @Override
    protected void onDestroy() {
//        dbHelper helper = new dbHelper(this,"SearchHistory.db",null,5);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("lastcity",presenter.getNowChecked());
        NewGarbageDatabase database = Room.databaseBuilder(this,NewGarbageDatabase.class,"NewDatabase04.db")
                .allowMainThreadQueries()
                .build();
        LastCityDao lastCityDao = database.getLastCityDao();
        LastCity lastCity = new LastCity(presenter.getCityName());
        lastCity.setId(1);
        lastCityDao.changeLastCity(lastCity);
        super.onDestroy();

    }



}
