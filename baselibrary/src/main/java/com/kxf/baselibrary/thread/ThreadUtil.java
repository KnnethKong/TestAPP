package com.kxf.baselibrary.thread;

import android.os.Looper;

public class ThreadUtil {
    public static void wait(Object obj){
        synchronized (obj){
            try {
                obj.wait();
            } catch (Exception e) {}
        }
    }

    public static void notify(Object obj){
        synchronized (obj){
            obj.notify();
        }
    }

    public static void notifyAll(Object obj){
        synchronized (obj){
            obj.notifyAll();
        }
    }

    public static void wait(Object obj, long millis){
        synchronized (obj){
            try {
                obj.wait(millis);
            } catch (Exception e) {}
        }
    }

    public static void wait(Object obj, long millis, int nanos){
        synchronized (obj){
            try {
                obj.wait(millis,nanos);
            } catch (Exception e) {}
        }
    }

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (Exception e) {}
    }

    public static void sleep(long millis,int nanos){
        try {
            Thread.sleep(millis,nanos);
        } catch (Exception e) {}
    }

    public static boolean isMain(){
       return Looper.getMainLooper() == Looper.myLooper();
    }
}
