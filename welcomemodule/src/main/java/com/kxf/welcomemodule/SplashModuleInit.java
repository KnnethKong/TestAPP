package com.kxf.welcomemodule;

import android.app.Application;

import com.kxf.mvvm.base.IModuleInit;

// 启动页module
public class SplashModuleInit implements IModuleInit {
    @Override
    public boolean onInitAhead(Application application) {
        return false;
    }

    @Override
    public boolean onInitLow(Application application) {
        return false;
    }
}
