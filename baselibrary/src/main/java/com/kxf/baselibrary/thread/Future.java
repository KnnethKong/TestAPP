package com.kxf.baselibrary.thread;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.kxf.baselibrary.utils.KLog;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Snow on 2018/6/15.
 * Copyright © 2015-2018. All rights reserved.
 *
 * @see java.util.concurrent.Future
 */
public abstract class Future<T> {

    private static final String TAG = "Future";

    private FutureResult<T> mResult;
    private FutureCallback mCallback;
    private FutureCancelDelegate mCancelDelegate;

    private Lock mLock;
    private Condition mCondition;

    protected Future() {
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
    }

    /**
     * 是否被取消
     *
     * @return 如果调用了 {@link #setResultWithCanceled()} 方法则返回 true，否则返回 false
     */
    public boolean isCancelled() {
        return mResult != null && mResult.isCancelled();
    }

    /**
     * 是否完成
     *
     * @return 当操作正常结束、异常结束、被取消时返回 true，否则返回 false
     */
    public boolean isDone() {
        return mResult != null;
    }

    /**
     * 设置成功结果，结果只能设置一次，第二次调用无效
     *
     * @param result 操作结果
     * @return 是否设置成功
     */
    protected boolean setResultWithSuccessful(@Nullable T result) {
        if (mResult != null) {
            return false;
        }

        mLock.lock();
        try {
            if (mResult != null) {
                return false;
            }

            mResult = FutureResult.resultWithSuccessful(result);

            mCondition.signalAll();
        } finally {
            mLock.unlock();
        }

        FutureCallback callback;
        synchronized (this) {
            callback = mCallback;
        }
        invokeSuccessful(callback);

        return true;
    }

    /**
     * 设置失败结果，结果只能设置一次，第二次调用无效
     *
     * @param error 错误信息，不能为 null
     * @return 是否设置成功
     */
    protected boolean setResultWithError(@NonNull Throwable error) {
        if (mResult != null || error == null) {
            return false;
        }

        mLock.lock();
        try {
            if (mResult != null) {
                return false;
            }

            mResult = FutureResult.resultWithError(error);

            mCondition.signalAll();
        } finally {
            mLock.unlock();
        }

        FutureCallback callback;
        synchronized (this) {
            callback = mCallback;
        }
        invokeFailed(callback);

        return true;
    }

    /**
     * 设置结果被取消息，结果只能设置一次，第二次调用无效
     *
     * @return 是否设置成功
     */
    protected boolean setResultWithCanceled() {
        if (mResult != null) {
            return false;
        }

        mLock.lock();
        try {
            if (mResult != null) {
                return false;
            }
            mResult = FutureResult.resultWithCancelled();

            mCondition.signalAll();
        } finally {
            mLock.unlock();
        }

        FutureCallback callback;
        synchronized (this) {
            callback = mCallback;
        }
        invokeCanceled(callback);

        return true;
    }

    /**
     * 取消当前的操作
     * 操作未开始：无论设置参数为true还是false，都返回true；
     * 操作执行中：参数设置为true，则返回true，如果设置为false，则返回false；
     * 操作已结束：无论设置参数为true还是false，都返回false；
     *
     * @param mayInterruptIfRunning 是否可以中断正在执行中的操作
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        FutureCancelDelegate cancelDelegate;
        synchronized (this) {
            cancelDelegate = mCancelDelegate;
        }

        if (cancelDelegate == null) {
            setResultWithCanceled();
            return true;
        }
        return cancelDelegate.futureCancel(this, mayInterruptIfRunning);
    }

    /**
     * 返回唯一值，内部catch住异常，异常时返回 null
     *
     * @return
     */
    public FutureResult<T> unique() {
        try {
            return get();
        } catch (Throwable e) {
            KLog.e(TAG, "get exception");
            return null;
        }
    }

    /**
     * 获取Future 结果，此方法会一直等待，直到操作完成
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public FutureResult<T> get() throws InterruptedException {
        if (mResult != null) {
            return mResult;
        }

        mLock.lock();
        try {
            if (mResult != null) {
                return mResult;
            }

            mCondition.await();
        } finally {
            mLock.unlock();
        }

        return mResult;
    }

    /**
     * 获取Future 结果，此方法会等待一段时间，时间由 timeout和unit 参数给出，如果操作仍未结束则立即返回
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public FutureResult<T> get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (mResult != null) {
            return mResult;
        }

        mLock.lock();
        try {
            if (mResult != null) {
                return mResult;
            }

            if (!mCondition.await(timeout, unit)) {
                throw new TimeoutException("Get result waiting for timeout");
            }
        } finally {
            mLock.unlock();
        }

        return mResult;
    }

    /**
     * 设置Future 回调
     *
     * @param callback
     */
    public void setCallback(FutureCallback callback) {
        if (callback == null) {
            return;
        }

        synchronized (this) {
            mCallback = callback;
        }


        if (mResult != null) {
            if (mResult.getError() != null) {
                invokeFailed(callback);
            } else if (mResult.isCancelled()) {
                invokeCanceled(callback);
            } else {
                invokeSuccessful(callback);
            }
        }
    }

    /**
     * 设置Future 取消委托
     *
     * @param cancelDelegate
     */
    protected void setCancelDelegate(FutureCancelDelegate cancelDelegate) {
        synchronized (this) {
            mCancelDelegate = cancelDelegate;
        }
    }

    private void invokeSuccessful(final FutureCallback callback) {
        if (callback == null) {
            return;
        }

        if (ThreadUtil.isMain()) {
            callback.onSuccessful(this, mResult.getResult());
            callback.onFinally(this);
        } else {
            GlobalThreadQueue.shareInstance().postToMain(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccessful(Future.this, mResult.getResult());
                    callback.onFinally(Future.this);
                }
            });
        }
    }

    private void invokeFailed(final FutureCallback callback) {
        if (callback == null) {
            return;
        }

        if (ThreadUtil.isMain()) {
            callback.onFailed(this, mResult.getError());
            callback.onFinally(this);
        } else {
            GlobalThreadQueue.shareInstance().postToMain(new Runnable() {
                @Override
                public void run() {
                    callback.onFailed(Future.this, mResult.getError());
                    callback.onFinally(Future.this);
                }
            });
        }
    }

    private void invokeCanceled(final FutureCallback callback) {
        if (callback == null) {
            return;
        }

        if (ThreadUtil.isMain()) {
            callback.onCanceled(this);
            callback.onFinally(this);
        } else {
            GlobalThreadQueue.shareInstance().postToMain(new Runnable() {
                @Override
                public void run() {
                    callback.onCanceled(Future.this);
                    callback.onFinally(Future.this);
                }
            });
        }
    }

    /**
     * Future 回调，回调在主线程中执行！！！，不要进行耗时操作
     */
    public interface FutureCallback<T> {

        /**
         * 操作成功回调
         *
         * @param future
         * @param result
         */
        @MainThread
        void onSuccessful(Future<T> future, T result);

        /**
         * 操作失败回调
         *
         * @param future
         * @param error
         */
        @MainThread
        void onFailed(Future<T> future, Throwable error);

        /**
         * 操作被取消回调
         *
         * @param future
         */
        @MainThread
        void onCanceled(Future<T> future);

        /**
         * Callback 流程结束回调，该事件是 Callback 流程的最后一个事件
         *
         * @param future
         */
        @MainThread
        void onFinally(Future<T> future);
    }

    public static abstract class FutureCallbackWrapper<T> implements FutureCallback<T> {

        /**
         * 操作成功回调
         *
         * @param future
         * @param result
         */
        @Override
        public void onSuccessful(Future<T> future, T result) {

        }

        /**
         * 操作失败回调
         *
         * @param future
         * @param error
         */
        @Override
        public void onFailed(Future<T> future, Throwable error) {

        }

        /**
         * 操作被取消回调
         *
         * @param future
         */
        @Override
        public void onCanceled(Future<T> future) {

        }

        /**
         * Callback 流程结束回调，该事件是 Callback 流程的最后一个事件
         *
         * @param future
         */
        @Override
        public void onFinally(Future<T> future) {

        }
    }

    /**
     * Future 取消委托，由具体执行者去取消
     */
    public interface FutureCancelDelegate {

        boolean futureCancel(@NonNull Future future, boolean mayInterruptIfRunning);
    }
}
