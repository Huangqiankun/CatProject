package com.hqk.catproject;

import android.app.Application;
import android.util.Log;

import com.hqk.catproject.http.HttpHelper;
import com.hqk.catproject.http.httpprocessor.impl.OkHttpProcessor;
import com.huantansheng.cameralibrary.util.LogUtil;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtil.e("MyApplication");

        initOkHttp();
    }

    private void initOkHttp() {
        HttpHelper.init(new OkHttpProcessor());
    }
}
