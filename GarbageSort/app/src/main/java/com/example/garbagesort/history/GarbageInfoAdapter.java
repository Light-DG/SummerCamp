package com.example.garbagesort.history;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.garbagesort.DataBase.Garbage;
import com.example.garbagesort.DataBase.GarbageDao;
import com.example.garbagesort.DataBase.NewGarbageDatabase;
import com.example.garbagesort.R;
import com.example.garbagesort.DataBase.dbHelper;
import com.example.garbagesort.show.Show;

import java.util.ArrayList;
import java.util.List;

public class GarbageInfoAdapter extends RecyclerView.Adapter<GarbageInfoAdapter.ViewHolder>   {

    List<BaseInfo> mList;
    private static final String TAG = "GarbageInfoAdapter";



    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView GarbageName;
        TextView GarbageKind;
        TextView SearchTime;
        TextView Model;
        TextView Detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            GarbageName = (TextView) itemView.findViewById(R.id.history_name);
            GarbageKind = (TextView) itemView.findViewById(R.id.history_kind);
            Model = (TextView)itemView.findViewById(R.id.history_model);
            SearchTime = (TextView) itemView.findViewById(R.id.history_time);
            Detail = (TextView)itemView.findViewById(R.id.history_detail);
        }
    }

    public GarbageInfoAdapter(List<BaseInfo> list){
        mList = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.Detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        ArrayList<String> list = new ArrayList<String>();
                        NewGarbageDatabase database = Room.databaseBuilder(v.getContext(),NewGarbageDatabase.class,"NewDatabase04.db").build();
                        GarbageDao garbageDao = database.getGarbageDao();
                        List<Garbage> tempList = garbageDao.queryAll();
                        Intent intent = new Intent(v.getContext(), Show.class);
                        for (int i =0 ;i<tempList.size();i++)
                        {
                            Garbage garbage  = tempList.get(i);
                            String time = garbage.getTime();
                            if (time.equals(holder.SearchTime.getText())){
                                list.add("success");
                                list.add(garbage.getName());
                                list.add(garbage.getKind());
                                list.add(garbage.getPs());
                                list.add(garbage.getConfidence());
                                list.add(garbage.getPath());
                                intent.putExtra("model02",garbage.getModel());
                                intent.putExtra("cityName",garbage.getCity());
                                break;
                            }
                        }
                        intent.putExtra("model","history");
                        intent.putStringArrayListExtra("garbage_info",list);
                        v.getContext().startActivity(intent);
                    }
                }.start();

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GarbageInfoAdapter.ViewHolder holder, int position) {
        BaseInfo baseInfo = mList.get(position);
        holder.GarbageName.setText(baseInfo.getName());
        holder.GarbageKind.setText(baseInfo.getKind());
        holder.Model.setText(baseInfo.getModel());
        holder.SearchTime.setText(baseInfo.getTime());
        Log.d(TAG, "调试： onBind里的时间： "+holder.SearchTime.getContext());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
