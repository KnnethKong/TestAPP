package com.kxf.testapp;

import com.alibaba.android.arouter.launcher.ARouter;
import com.kxf.baselibrary.base.BaseApplication;
import com.kxf.baselibrary.http.OkHttpUtils;
import com.kxf.mvvm.config.ModuleLifecycleConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 *
 */
public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化arouter
        ModuleLifecycleConfig.getInstance().initModuleAhead(this);
        //初始化网络请求
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}
