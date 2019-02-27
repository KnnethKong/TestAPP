package com.kxf.baselibrary.thread;

import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

/**
 * Created by Michael on 2016/12/26.
 * Copyright © 2015-2016. All rights reserved.
 */

public abstract class BackgroundTask<Param,Result> extends Task implements Runnable {
    @Nullable
    Param mParam;
    @Nullable
    Result mResult;
    @Override
    final public void run() {
        if (Looper.myLooper() != Looper.getMainLooper()){
            mResult = doInBackground(mParam);
            GlobalThreadQueue.shareInstance().postToMain(this);
        }
        else{
            onPostExecute(mResult);
            finish();
        }
    }

    @WorkerThread
    abstract protected @Nullable
    Result doInBackground(@Nullable Param param);

    @MainThread
    abstract protected void onPostExecute(@Nullable Result result);

    @Override
    public boolean perform() {
        run();
        return false;
    }
}
