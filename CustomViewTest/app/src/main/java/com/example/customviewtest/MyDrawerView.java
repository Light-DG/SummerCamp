package com.example.customviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MyDrawerView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "MyDrawerView";
    BottomSheetBehavior bottomSheetBehavior;
    LineView lineView;
    int clickCount = 0;

    public MyDrawerView(Context context) {
        this(context,null);
    }

    public MyDrawerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.my_drawer,this,false);
        addView(view);
        initDrawer();
        lineView = findViewById(R.id.mini_bar);
        lineView.setOnClickListener(this);

    }


    public void initDrawer() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.ll_content_bottom_sheet));
        bottomSheetBehavior.setPeekHeight(150);
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setExpandedOffset(50);
        bottomSheetBehavior.setHalfExpandedRatio((float) 0.3);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        lineView.setFlag(3);
                        Log.e(TAG, "=== STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e(TAG, "=== STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        lineView.setFlag(1);
                        Log.e(TAG, "=== STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e(TAG, "=== STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e(TAG, "=== STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        lineView.setFlag(2);
                        Log.e(TAG, "=== STATE_HALF_EXPANDED");
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mini_bar:
                int stateCode = bottomSheetBehavior.getState();
                if (stateCode == BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    lineView.setFlag(2);
                }else if (stateCode == BottomSheetBehavior.STATE_HALF_EXPANDED){
                    clickCount++;
                    if (clickCount % 2 ==1){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        lineView.setFlag(1);
                    }else if (clickCount % 2 ==0){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }else if (stateCode == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    lineView.setFlag(2);
                }

                break;


            default:
                break;

        }
    }




}
