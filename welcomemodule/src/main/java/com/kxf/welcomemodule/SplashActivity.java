package com.kxf.welcomemodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.kxf.baselibrary.thread.GlobalThreadQueue;
import com.kxf.baselibrary.utils.StatusBarCompat;
import com.kxf.mvvm.router.RouterActivityPath;

//冷启动
@Route(path = RouterActivityPath.PAGER_SPLASH)
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarCompat.compat(this,R.color.background_blue);
        GlobalThreadQueue.shareInstance().postToMain(new Runnable() {
            @Override
            public void run() {
                ARouter.getInstance().build(RouterActivityPath.PAGER_LOGIN).navigation();

//                Toast.makeText(SplashActivity.this,"1222",Toast.LENGTH_SHORT).show();
            }
        },1000);
    }
}
