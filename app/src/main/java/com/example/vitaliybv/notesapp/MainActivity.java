package com.example.vitaliybv.notesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
    private Handler uiHandler;
    private Handler workerHandler;
    private Runnable refreshTask;

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

                final Note note = new Note(0, title, body);
                insert(note);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        uiHandler.removeCallbacksAndMessages(null);
        workerHandler.removeCallbacksAndMessages(null);
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
                        showToast("Note deleted");
                    }
                });

                refresh();
            }
        };
        // add task to the MessageQueue of WorkerThread
        workerHandler.post(insertTask);
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
                        showToast("Note added");
                    }
                });

                refresh();
            }
        };
        // add task to the MessageQueue of WorkerThread
        workerHandler.post(insertTask);
    }

    // perform getting actual list of notes from DB and refreshing UI
    private void refresh() {
        if (refreshTask == null) {
            refreshTask = new Runnable() {
                @Override
                public void run() {
                    final List<Note> reverseNotes = getApp().getDb().noteDao().getAllReverse();

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            noteAdapter.replaceWith(reverseNotes);
                        }
                    });
                }
            };
        }
        workerHandler.post(refreshTask);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);
    }

    private void initWorkerThread() {
        uiHandler = new Handler(Looper.getMainLooper());
        workerHandler = Workers.getWorkerThread().newWorkerHandler();
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
