package com.myselfkiller.superbluetoothlibrary.b2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Killer on 2018/7/18.
 */

public class DeviceItem extends LinearLayout {
    private Context context;
    private LinearLayout layout;
    public DeviceItem(Context context) {
        super(context);
        this.context = context;
        creatView();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceType")
    private void creatView(){
        layout = new LinearLayout(context);
        layout.setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        TextView nameview = new TextView(context);
        LayoutParams namePa = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        namePa.setMargins(30,30,30,0);
        nameview.setLayoutParams(namePa);
//        nameview.setTextColor(0xFF000000);
        TypedValue typedValue = new  TypedValue();
        context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        nameview.setTextColor(color);
//        nameview.setTextColor(ContextCompat.getColor(context, android.support.v7.appcompat.R.attr.colorPrimary));
        nameview.setText("名称");
        nameview.setTextSize(16);
        TextView addrview = new TextView(context);
        LayoutParams addrPa = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addrPa.setMargins(30,0,30,30);
        addrview.setLayoutParams(addrPa);
//        TypedValue typedaddr = new  TypedValue();
//        context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, typedaddr, true);
//        int coloraddr = typedValue.data;
        addrview.setTextColor(color);
        addrview.setTextSize(16);
        addrview.setText("地址");
        layout.addView(nameview);
        layout.addView(addrview);
    }

    @Override
    public View getChildAt(int index) {
        return layout.getChildAt(index);
    }

    public View getView() {
        return layout;
    }
}
