package com.kxf.mvvm.config;

import android.app.Application;


import com.kxf.mvvm.base.IModuleInit;

import io.reactivex.annotations.Nullable;

/**
 * 初始化每个模块 AROUTER 通过反射机制，动态调用每个组件初始化逻辑->ARouter::Init::Invoke init(context) first!
 * app中AndroidManifest.xml不要忘记注册Activity
 */

public class ModuleLifecycleConfig {
    private final String BaseInit = "com.kxf.mvvm.base.BaseModuleInit";
    private final String WelcomeInit = "com.kxf.welcomemodule.SplashModuleInit";
    private final String UserInit = "com.kxf.userlibrary.UserModuleInit";
    private String[] initModuleNames = {BaseInit, WelcomeInit, UserInit};

    //内部类，在装载该内部类时才会去创建单例对象
    private static class SingletonHolder {
        public static ModuleLifecycleConfig instance = new ModuleLifecycleConfig();
    }

    public static ModuleLifecycleConfig getInstance() {
        return SingletonHolder.instance;
    }

    private ModuleLifecycleConfig() {
    }

    //初始化组件-靠前
    public void initModuleAhead(@Nullable Application application) {
        for (String moduleInitName : initModuleNames) {
            try {
                Class<?> clazz = Class.forName(moduleInitName);
                IModuleInit init = (IModuleInit) clazz.newInstance();
                //调用初始化方法
                init.onInitAhead(application);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //初始化组件-靠后
    public void initModuleLow(@Nullable Application application) {
        for (String moduleInitName : initModuleNames) {
            try {
                Class<?> clazz = Class.forName(moduleInitName);
                IModuleInit init = (IModuleInit) clazz.newInstance();
                init.onInitLow(application);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
