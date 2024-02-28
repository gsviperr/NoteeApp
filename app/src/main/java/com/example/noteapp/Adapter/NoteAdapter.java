package com.example.noteapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.Domain.Note;
import com.example.noteapp.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<Note> originalNotes; // Original list of notes
    private List<Note> notes; // Filtered list of notes
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.originalNotes = new ArrayList<>(noteList);
        this.notes = new ArrayList<>(noteList);
    }

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Interface for item long click events
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, View view);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Method to set the long click listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void updatenote(List<Note> newNotelist) {
        this.notes = newNotelist;
        notifyDataSetChanged();
    }

    public Note getItemAtPosition(int position) {
        return notes.get(position);
    }

    public void addNote(Note note) {
        originalNotes.add(note);
        notes.add(note);
        notifyDataSetChanged();
    }

    public void searchNotes(String searchContent) {
        searchContent = searchContent.trim().toLowerCase();
        notes = new ArrayList<>();

        for (Note note : originalNotes) {
            String title = note.getTITLE().toLowerCase();
            String content = note.getNOTETEXT().toLowerCase();

            if (title.contains(searchContent) || content.contains(searchContent)) {
                notes.add(note);
            }
        }

        // Update the dataset with filtered notes
        notifyDataSetChanged();
    }

    public void resetSearch() {
        notes = new ArrayList<>(originalNotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.titleTextView.setText(note.getTITLE());
        holder.datetimeTextView.setText(note.getDATETIME());
        holder.noteTextTextView.setText(note.getNOTETEXT());
        holder.nidTextView.setText(String.valueOf(note.getN_ID()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // View holder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView datetimeTextView;
        private TextView noteTextTextView;
        private TextView nidTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txt_TITLE);
            datetimeTextView = itemView.findViewById(R.id.txtDATETIME);
            noteTextTextView = itemView.findViewById(R.id.txtCONTENT);
            nidTextView = itemView.findViewById(R.id.txtNID);

            // Set click listener for the entire item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });

            // Set long click listener for the entire item view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemLongClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemLongClickListener.onItemLongClick(position, view);
                        }
                    }
                    return true;
                }
            });
        }
    }
}
