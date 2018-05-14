package com.example.vitaliybv.notesapp;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

public class WorkerThread extends HandlerThread {

    private Handler mWorkerHandler;

    public WorkerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        mWorkerHandler.post(task);
    }

    public void prepareHandler(){
        mWorkerHandler = new Handler(getLooper());
    }
}
