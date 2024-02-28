// RecycleBinAdapter.java
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

public class RecycleBInAdapter extends RecyclerView.Adapter<RecycleBInAdapter.ViewHolder> {
    private Context context;
    private List<Note> binNotes; // List of notes in the recycle bin
    private List<Note> originalBinNotes; // Original list of notes for resetting search
    private OnItemClickListener onItemClickListener;

    public RecycleBInAdapter(Context context, List<Note> binNotes) {
        this.context = context;
        this.binNotes = binNotes;
        this.originalBinNotes = new ArrayList<>(binNotes);
    }

    public void updateBinNotes(List<Note> newBinNoteList) {
        this.binNotes = newBinNoteList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public Note getItemAtPosition(int position) {
        return binNotes.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void searchNotes(String searchContent) {
        searchContent = searchContent.trim().toLowerCase();
        List<Note> filteredBinNotes = new ArrayList<>();

        for (Note binNote : originalBinNotes) {
            String title = binNote.getTITLE().toLowerCase();
            String content = binNote.getNOTETEXT().toLowerCase();

            if (title.contains(searchContent) || content.contains(searchContent)) {
                filteredBinNotes.add(binNote);
            }
        }

        // Update the dataset with filtered bin notes
        updateBinNotes(filteredBinNotes);
    }

    public void resetSearch() {
        binNotes = new ArrayList<>(originalBinNotes);
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
        Note binNote = binNotes.get(position);

        holder.titleTextView.setText(binNote.getTITLE());
        holder.datetimeTextView.setText(binNote.getDATETIME());
        holder.noteTextTextView.setText(binNote.getNOTETEXT());
        holder.nidTextView.setText(String.valueOf(binNote.getN_ID()));
    }

    @Override
    public int getItemCount() {
        return binNotes.size();
    }

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
        }
    }
}
