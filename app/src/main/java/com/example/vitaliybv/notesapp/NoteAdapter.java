package com.example.vitaliybv.notesapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NoteAdapterOnClickHandler onClickHandler;

    interface NoteAdapterOnClickHandler {
        void onClick(int id, int position);
    }

    public NoteAdapter(NoteAdapterOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

    public void replaceWith(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NoteViewHolder(inflater
                .inflate(R.layout.note_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String title = notes.get(position).getTitle();
        String body = notes.get(position).getBody();

        holder.titleTextView.setText(title);
        holder.bodyTextView.setText(body);
    }

    @Override
    public int getItemCount() {
        if(notes != null){
            return notes.size();
        }
        return 0;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView titleTextView;
        TextView bodyTextView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            this.container = itemView.findViewById(R.id.container_text);
            this.titleTextView = itemView.findViewById(R.id.tv_title);
            this.bodyTextView = itemView.findViewById(R.id.tv_body);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int id = notes.get(position).getId();
                    onClickHandler.onClick(id, position);
                }
            });
        }
    }
}
