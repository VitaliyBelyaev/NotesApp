package com.example.vitaliybv.notesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NoteAdapter.NoteAdapterOnClickHandler {

    private EditText titleEditText;
    private EditText bodyEditText;
    private Button addButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private WorkerThread workerThread;
    private Handler uiHandler;

    public static final String LOG_TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.et_title);
        bodyEditText = findViewById(R.id.et_body);
        addButton = findViewById(R.id.button_add);
        initRecyclerView();
        initWorkerThread();

        refresh();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String body = bodyEditText.getText().toString();

                titleEditText.setText("");
                bodyEditText.setText("");
                hideKeyboard();

                final Note note = new Note(title, body);
                insert(note);
            }
        });
    }

    @Override
    public void onClick(int id, int position) {
        delete(id);
    }

    // perform deleting note from DB on background thread
    private void delete(final int id) {
        Runnable insertTask = new Runnable() {
            @Override
            public void run() {
                // delete note by id
                getApp().getDb().noteDao().deleteById(id);

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Note deleted",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                refresh();
            }
        };
        // add task to the MessageQueue of WorkerThread
        workerThread.postTask(insertTask);
    }

    // perform inserting note to DB on background thread
    private void insert(final Note note) {
        Runnable insertTask = new Runnable() {
            @Override
            public void run() {
                // insert note into DB
                getApp().getDb().noteDao().insert(note);

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Note added",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                refresh();
            }
        };
        // add task to the MessageQueue of WorkerThread
        workerThread.postTask(insertTask);
    }

    // perform getting actual list of notes from DB and refreshing UI
    private void refresh() {
        Runnable refreshTask = new Runnable() {
            @Override
            public void run() {
                List<Note> notes = getApp().getDb().noteDao().getAll();

                Collections.reverse(notes);
                final List<Note> reverseNotes = notes;
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        noteAdapter.replaceWith(reverseNotes);
                    }
                });
            }
        };
        workerThread.postTask(refreshTask);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);
    }

    private void initWorkerThread() {
        workerThread = new WorkerThread("workerThread");
        uiHandler = new Handler();
        workerThread.start();
        workerThread.prepareHandler();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private App getApp() {
        return ((App) getApplication());
    }
}
