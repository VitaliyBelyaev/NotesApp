package com.example.vitaliybv.notesapp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.List;

public class WorkerThread extends HandlerThread {

    private final Looper looper;

    public WorkerThread(String name) {
        super(name);
        this.start();
        looper = getLooper();
    }

    public Handler newWorkerHandler(){
        return new Handler(looper);
    }
}
