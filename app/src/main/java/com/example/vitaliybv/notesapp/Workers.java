package com.example.vitaliybv.notesapp;

public final class Workers {
    private Workers(){}

    public static final WorkerThread WORKER_THREAD = new WorkerThread("workerThread");

    public static WorkerThread getWorkerThread(){
        return WORKER_THREAD;
    }
}
