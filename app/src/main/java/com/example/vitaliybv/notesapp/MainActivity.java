package com.example.vitaliybv.notesapp;

import android.content.Context;
import android.os.AsyncTask;
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

    public static final String LOG_TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.et_title);
        bodyEditText = findViewById(R.id.et_body);
        addButton = findViewById(R.id.button_add);
        initRecyclerView();
        new RefreshAsyncTask().execute();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String body = bodyEditText.getText().toString();

                titleEditText.setText("");
                bodyEditText.setText("");
                hideKeyboard();


                Note note = new Note(title, body);
                new InsertAsyncTask().execute(note);
            }
        });
    }

    @Override
    public void onClick(int id, int position) {
        new DeleteAsyncTask().execute(id);

    }


    public class RefreshAsyncTask extends AsyncTask<Void, Void, List<Note>> {

        @Override
        protected List<Note> doInBackground(Void... voids) {
            List<Note> notes = getApp().getDb().noteDao().getAll();
            return notes;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            Collections.reverse(notes);
            noteAdapter.replaceWith(notes);
        }
    }

    public class InsertAsyncTask extends AsyncTask<Note, Void, Void> {


        @Override
        protected Void doInBackground(Note... notes) {
            getApp().getDb().noteDao().insertAll(notes);
            Log.i(LOG_TAG,"in doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(LOG_TAG,"in postExecute before starting refresh");
            Toast.makeText(getApplicationContext(),"Note inserted",Toast.LENGTH_SHORT).show();
            new RefreshAsyncTask().execute();
        }
    }

    public class DeleteAsyncTask extends AsyncTask<Integer, Void, Void> {


        @Override
        protected Void doInBackground(Integer... ids) {
            getApp().getDb().noteDao().deleteById(ids[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Note deleted",Toast.LENGTH_SHORT).show();
            new RefreshAsyncTask().execute();
        }
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    private App getApp(){
        return ((App) getApplication());
    }
}
