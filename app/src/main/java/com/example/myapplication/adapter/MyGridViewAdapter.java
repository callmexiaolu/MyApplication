package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.util.Contast;

import java.util.List;

/**
 * Create By LuKaiqi
 * Describe:展示发帖时选择上传的图片适配器
 */
public class MyGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private LayoutInflater inflater;

    public MyGridViewAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        //return mList.size() + 1;//因为最后多了一个添加图片的ImageView
        int count = mList == null ? 1 : mList.size() + 1;
        if (count > Contast.MAX_SELECTED_NUMBER) {
            return mList.size();
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.public_selected_image_gridview_item, parent,false);
        //ImageView iv = convertView.findViewById(R.id.iv_public_selected_show);
        if (position < mList.size()) {
            //代表+号之前的需要正常显示图片
            String picUrl = mList.get(position); //图片路径
           // Glide.with(mContext).load(picUrl).into(iv);
        }
        return convertView;
    }
}
