package com.example.noteapp.ui.RecycleBin;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.noteapp.Adapter.RecycleBInAdapter;
import com.example.noteapp.DbHelper;
import com.example.noteapp.Domain.Note;
import com.example.noteapp.R;
import com.example.noteapp.databinding.FragmentRecyclebinBinding;

import java.util.ArrayList;

public class RecycleBInFragment extends Fragment {

    private FragmentRecyclebinBinding binding;
    EditText otimkim;
    ImageView menu;
    RecyclerView bin;
    ArrayList<String> title,datetime,content,nid;
    DbHelper db;
    RecycleBInAdapter recycleBInAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecyclebinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        bin = root.findViewById(R.id.listtNode);
        otimkim = root.findViewById(R.id.edser);

        db = new DbHelper(getActivity());
        recycleBInAdapter = new RecycleBInAdapter(requireContext(), db.getAllNoteBin());
        bin.setAdapter(recycleBInAdapter);
        bin.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        displaydata();
        otimkim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchContent = editable.toString().trim();
                if (searchContent.isEmpty()) {
                    recycleBInAdapter.resetSearch();
                } else {
                    recycleBInAdapter.searchNotes(searchContent);
                }
            }
        });
        if (recycleBInAdapter != null) {
            recycleBInAdapter.setOnItemClickListener(new RecycleBInAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Note clickedNote = recycleBInAdapter.getItemAtPosition(position);
                    showPopupMenu(clickedNote, bin);
                }
            });
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void displaydata() {
        title = new ArrayList<>();
        datetime = new ArrayList<>();
        content = new ArrayList<>();
        nid = new ArrayList<>();

        Cursor cursor = db.getdataNote();
        int idColumnIndex = cursor.getColumnIndex("N_ID");
        int titleColumnIndex = cursor.getColumnIndex("title");
        int datetimeColumnIndex = cursor.getColumnIndex("datetime");
        int contentColumnIndex = cursor.getColumnIndex("notetext");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idColumnIndex);
            String noteTitle = cursor.getString(titleColumnIndex);
            String noteDatetime = cursor.getString(datetimeColumnIndex);
            String noteContent = cursor.getString(contentColumnIndex);

            Note note = new Note(id, noteTitle, noteDatetime, noteContent);
            nid.add(String.valueOf(id));
            title.add(noteTitle);
            datetime.add(noteDatetime);
            content.add(noteContent);
        }
        cursor.close();
    }
    private void showPopupMenu(Note note, View view) {
        if (note != null) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View popupView = inflater.inflate(R.layout.recyclebinmenu, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(popupView);
            AlertDialog dialog = builder.create();

            dialog.show();
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout deleteLayout = popupView.findViewById(R.id.xoaaaaa);
            LinearLayout restoreLayout = popupView.findViewById(R.id.Restore);

            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteNoteById(note.getN_ID());
                    recycleBInAdapter.updateBinNotes(db.getAllNoteBin());
                    dialog.dismiss();
                }
            });
            restoreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.restorNote(note.getN_ID());
                    recycleBInAdapter.updateBinNotes(db.getAllNoteBin());
                    dialog.dismiss();
                }
            });
        }
    }
}