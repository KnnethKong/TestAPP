package com.kxf.baselibrary.thread;

/**
 * Created by Snow on 2018/6/15.
 * Copyright © 2015-2018. All rights reserved.
 * <p>
 * Future 结果，操作层不可以直接创建该类的对象，也不可以设置属性值
 */
public class FutureResult<T> {

    private T result;
    private Throwable error;
    private boolean cancelled;

    private FutureResult() {

    }

    public boolean isSuccess() {
        return !cancelled && error == null;
    }

    public T getResult() {
        return result;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    static <T> FutureResult<T> resultWithSuccessful(T result) {
        FutureResult<T> futureResult = new FutureResult<>();
        futureResult.result = result;
        return futureResult;
    }

    static <T> FutureResult<T> resultWithError(Throwable error) {
        FutureResult<T> futureResult = new FutureResult<>();
        futureResult.error = error;
        return futureResult;
    }

    static <T> FutureResult<T> resultWithCancelled() {
        FutureResult<T> futureResult = new FutureResult<>();
        futureResult.cancelled = true;
        return futureResult;
    }
}
