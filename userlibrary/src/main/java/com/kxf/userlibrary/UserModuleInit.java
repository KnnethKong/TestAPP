package com.kxf.userlibrary;

import android.app.Application;

import com.kxf.mvvm.base.IModuleInit;

public class UserModuleInit implements IModuleInit {
    @Override
    public boolean onInitAhead(Application application) {
        return false;
    }

    @Override
    public boolean onInitLow(Application application) {
        return false;
    }
}
