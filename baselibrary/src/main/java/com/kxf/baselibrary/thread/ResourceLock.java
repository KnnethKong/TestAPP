package com.kxf.baselibrary.thread;



public class ResourceLock {
    private Thread mOccupiedThread;

    /**
     获取资源，如果资源已经被获取过则返回 false，否则返回 true

     @return 如果资源已经被获取过则返回 false，否则返回 true
     */
    public synchronized boolean obtain() {
        if (mOccupiedThread != null) {
            return false;
        }
        else {
            mOccupiedThread = Thread.currentThread();
            return true;
        }
    }

    /**
     等待资源可用
     如果资源没有被获取过则获取该资源并返回 true
     如果资源被其它线程获取则等待直到资源可用并返回 true
     如果资源是被同一线程获取则不会等待，并且返回 false
     @return true/false
     */
    public boolean obtainAndWait()
    {
        synchronized (this){
            if (mOccupiedThread == null){
                return obtain();
            }

            if (mOccupiedThread == Thread.currentThread()){
                return false;
            }

            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            return obtain();
        }
    }

    /**
     释放占用的资源锁
     必须在得到该锁的线程中释放
     */
    public synchronized void finish(){
        mOccupiedThread = null;
        synchronized (this) {
            notify();
        }
    }
}
