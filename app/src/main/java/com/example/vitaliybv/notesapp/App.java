package com.example.vitaliybv.notesapp;

import android.app.Application;
import android.arch.persistence.room.Room;

public class App extends Application {

    private NoteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        db = Room
                .databaseBuilder(getApplicationContext(), NoteDatabase.class,"notes")
                .build();
    }

    public NoteDatabase getDb() {
        return db;
    }
}

