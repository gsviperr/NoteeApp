package com.example.noteapp.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.noteapp.Adapter.NoteAdapter;
import com.example.noteapp.DbHelper;
import com.example.noteapp.Domain.Note;
import com.example.noteapp.NoteActivity;
import com.example.noteapp.R;
import com.example.noteapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView list;
    private EditText otimkiem;
    private NoteAdapter noteAdapter;
    private DbHelper db;
    private ArrayList<String> title, datetime, content, nid;

    private static final int NOTE_ACTIVITY_REQUEST_CODE = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        list = root.findViewById(R.id.listtNode);
        otimkiem = root.findViewById(R.id.edser);
        db = new DbHelper(requireContext());

        // Tạo adapter và đặt nó vào RecyclerView
        noteAdapter = new NoteAdapter(requireContext(), db.getALLNote());
        list.setAdapter(noteAdapter);

        // Đặt layout manager
        list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        displaydata();

        // Thêm TextWatcher để theo dõi sự thay đổi trong ô tìm kiếm
        otimkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Trước khi văn bản thay đổi
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Khi văn bản đang thay đổi
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Sau khi văn bản đã thay đổi
                // Lấy chuỗi tìm kiếm từ văn bản đã chỉnh sửa
                String searchContent = editable.toString().trim();

                // Kiểm tra nếu ô tìm kiếm trống, đặt lại dữ liệu ban đầu
                if (searchContent.isEmpty()) {
                    noteAdapter.resetSearch();
                } else {
                    // Ngược lại, thực hiện tìm kiếm và hiển thị kết quả
                    noteAdapter.searchNotes(searchContent);
                }
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open NoteActivity when fab is clicked
                Intent intent = new Intent(requireContext(), NoteActivity.class);
                startActivity(intent);
            }
        });

        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Lấy dữ liệu cho phần tử đã nhấp vào
                Note clickedNote = noteAdapter.getItemAtPosition(position);

                // Truyền dữ liệu đến NoteActivity
                Intent intent = new Intent(requireContext(), NoteActivity.class);
                intent.putExtra("noteId", clickedNote.getN_ID()); // Pass noteId to NoteActivity
                intent.putExtra("title", clickedNote.getTITLE());
                intent.putExtra("content", clickedNote.getNOTETEXT());
                startActivityForResult(intent, NOTE_ACTIVITY_REQUEST_CODE);
            }
        });

        noteAdapter.setOnItemLongClickListener(new NoteAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                Note clickedNote = noteAdapter.getItemAtPosition(position);
                showPopupMenu(clickedNote, view);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NOTE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Kiểm tra nếu note được lưu, cập nhật dữ liệu trên RecyclerView
                noteAdapter.updatenote(db.getALLNote());
            }
        }
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
        noteAdapter.updatenote(db.getALLNote());
        cursor.close();
    }

    private void showPopupMenu(Note note, View view) {
        if (note != null) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View popupView = inflater.inflate(R.layout.notemenu, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(popupView);
            AlertDialog dialog = builder.create();

            dialog.show();
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout deleteLayout = popupView.findViewById(R.id.xoaaaaa);
            LinearLayout blockLayout = popupView.findViewById(R.id.blockkkk);

            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteNote(note.getN_ID());
                    noteAdapter.updatenote(db.getALLNote());
                    dialog.dismiss();
                }
            });
        }
    }
}
