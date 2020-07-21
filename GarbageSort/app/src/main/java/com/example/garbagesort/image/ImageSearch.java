package com.example.garbagesort.image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.garbagesort.Parse;
import com.example.garbagesort.R;
import com.example.garbagesort.show.Show;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageSearch extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnCancelListener ,ImageContract.imageView{

    private static final String TAG = "ImageSearch";
    ImageContract.imagePresent present;

    ImageView cameraIcon;
    ImageView albumIcon;

//    Parse myParse = null;

    String takePhotoName = null;
    String PhotoPath = null;
    Uri takePhotoUri = null;

    public static final int TAKE_PHOTO = 1;
    public static final int IMAGE_SHOW = 2;
    public static final int SHOW_PROGRESS = 3;
    public static final int CHOOSE_PHOTO = 4;
    public static final int DISMISS_PORGRESS = 5;
    private static final int NOT_CALCELED = 6;
    private static final int BUTTON_CLICKABLE = 7;
//    ArrayList<String> responseList = null;
    ProgressDialog progressDialog =null;
    boolean isCanceled = false;
    String cityName;
    boolean alreadyClick = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_search);

        present = new ImagePresenter(this);


        cameraIcon = (ImageView)findViewById(R.id.image_camera2);
        albumIcon = (ImageView)findViewById(R.id.image_album);

        cityName = getIntent().getStringExtra("cityName");
        present.setCity(cityName);

        cameraIcon.setOnClickListener(this);
        albumIcon.setOnClickListener(this);

//        myParse = new Parse();

//        responseList = new ArrayList<String>();
        //设置进度框
        progressDialog = new ProgressDialog(ImageSearch.this);
        progressDialog.setTitle("查询中");
        progressDialog.setMessage("请耐心等待");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(ImageSearch.this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
        }



    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case IMAGE_SHOW:
                    if (!isCanceled&&present.getResponseList().get(0).equals("success")){
                        Intent intent = new Intent(ImageSearch.this, Show.class);
                        intent.putStringArrayListExtra("garbage_info",present.getResponseList());
                        intent.putExtra("model","image");
                        intent.putExtra("model02","NO");
                        intent.putExtra("cityName",cityName);
                        progressDialog.dismiss();
                        startActivity(intent);
                    }else if (!isCanceled&&present.getResponseList().get(0).equals("failure")){
                        progressDialog.dismiss();
                        showDialog();
                    }
                    break;

                case SHOW_PROGRESS:
                    progressDialog.show();
                    break;

                case DISMISS_PORGRESS:
                    progressDialog.dismiss();
                    break;

                case BUTTON_CLICKABLE:
                    alreadyClick = false;
                    break;


                default:
                    break;



            }
        }
    };

    public void resultShow(){
        if(!isCanceled)
        {
            Message message = new Message();
            message.what = IMAGE_SHOW;
            handler.sendMessage(message);
        }
    }

    public void sendButtonRenewMessage(){
        Message message = new Message();
        message.what = BUTTON_CLICKABLE;
        handler.sendMessageDelayed(message,400);
    }

    public String getPhotoPath() {
        return PhotoPath;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_camera2:
                //动态申请相机权限
                isCanceled = false;
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                        !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},2);
                }
                else{
                    if (!alreadyClick)
                    {
                        alreadyClick = true;
                        sendButtonRenewMessage();
                        switchToCamera();
                    }
                }
                break;

            case R.id.image_album:
                Log.d(TAG, "调试： onClick: ");
                if (!alreadyClick)
                {
                    alreadyClick = true;
                    sendButtonRenewMessage();
                    openAlbum();
                }
                break;

            default:
                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 2:
                //获得权限后立刻打开相机
                switchToCamera();
                break;

            default:
                break;


        }
    }



    public void switchToCamera(){
        takePhotoName = String.valueOf(System.currentTimeMillis())+".jpg";
        PhotoPath = getExternalCacheDir()+"/"+takePhotoName;
//        File outputImage = new File(getExternalCacheDir(),takePhotoName);
        File outputImage = new File(PhotoPath);
        Log.d(TAG, "调试： path "+PhotoPath);
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            takePhotoUri = FileProvider.getUriForFile(ImageSearch.this,
                    "com.example.garbagesort.fileprovider",outputImage);
        }else {
            takePhotoUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,takePhotoUri);
        startActivityForResult(intent,TAKE_PHOTO);
//        progressDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    Message message = new Message();
                    message.what = SHOW_PROGRESS;
                    handler.sendMessage(message);
                    present.handlePhoto();
//                    new Thread(new handleImageThread()).start();

                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK) {
                    Message message2 = new Message();
                    message2.what = SHOW_PROGRESS;
                    handler.sendMessage(message2);
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                    present.handlePhoto();
//                    new Thread(new handleImageThread()).start();
                }else if (resultCode==RESULT_CANCELED){
                    Toast.makeText(ImageSearch.this,"未选择照片",Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;

        }
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        isCanceled = true;
        File file1 = new File(PhotoPath);
        if (file1.exists()){
            file1.delete();
        }
        Toast.makeText(ImageSearch.this,"取消查询",Toast.LENGTH_SHORT).show();
    }



    public void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
        Log.d(TAG, "调试： openAlbum: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        Log.d(TAG, "调试：handleImageOnKitKat: ");
        Uri uri = data.getData();
        Log.d(TAG, "调试： Uri: "+uri);
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                PhotoPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                PhotoPath = getImagePath(uri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){

            PhotoPath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){

            PhotoPath = uri.getPath();
        }

    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri= data.getData();
        PhotoPath = getImagePath(uri,null);
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null)
        {
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ImageSearch.this);
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
