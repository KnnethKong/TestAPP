package com.kxf.baselibrary.thread;

/**
 * Created by Michael on 2018/6/21.
 * Copyright © 2015-2018. All rights reserved.
 */
public class BlockVariable<T> {
    private T mValue;

    public BlockVariable(){
    }

    public BlockVariable(T v){
        mValue = v;
    }
    public void set (T v){
        mValue = v;
    }

    public T get (){
        return mValue;
    }
}
