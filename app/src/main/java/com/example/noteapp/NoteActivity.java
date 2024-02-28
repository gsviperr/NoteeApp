package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.noteapp.Domain.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {
    EditText write, titleee;
    ImageView back, savee, thongbao;
    DbHelper db;
    private boolean isNoteSaved = false;
    private int noteId = -1; // Default value indicating a new note
    private boolean isRotating = false; // Variable to track rotation

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        back = findViewById(R.id.backbtn);
        write = findViewById(R.id.notewrite);
        savee = findViewById(R.id.saveee);
        titleee = findViewById(R.id.titl);
        thongbao = findViewById(R.id.notfication);
        db = new DbHelper(this);

        write.requestFocus();
        write.setSelection(0);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1); // Get noteId if available

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        titleee.setText(title);
        write.setText(content);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button click
                onBackPressed();
                saveNote();
            }
        });

        savee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                // Khi người dùng bấm nút "Save", chuyển đến trang chính
                Intent intent1 = new Intent(NoteActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        thongbao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the correct title to the NotificationReceiver
                String title = titleee.getText().toString().trim();
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(NoteActivity.this, NotificationReceiver.class);
                intent.putExtra("noteTitle", title); // Pass the note title
                pendingIntent = PendingIntent.getBroadcast(NoteActivity.this, noteId, intent, 0);

                isNoteSaved = true;
                long triggerTime = System.currentTimeMillis() + 15000; // 5 second
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
                Toast.makeText(getApplicationContext(), "Notification successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the rotating state
        outState.putBoolean("isNoteSaved", isNoteSaved);
        outState.putBoolean("isRotating", isRotating);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the rotating state
        super.onRestoreInstanceState(savedInstanceState);
        isNoteSaved = savedInstanceState.getBoolean("isNoteSaved");
        isRotating = savedInstanceState.getBoolean("isRotating");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Check if the activity is finishing (not due to rotation)
        if (!isFinishing()) {
            // Reopen the NoteActivity with the same noteId
            String title = titleee.getText().toString().trim();
            String noteContent = write.getText().toString().trim();
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("noteId", noteId);
            intent.putExtra("title", title);
            intent.putExtra("content", noteContent);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("NoteActivity", "onDestroy called");
        // Save the note when the activity is destroyed and not rotating
        String title = titleee.getText().toString().trim();
        String noteContent = write.getText().toString().trim();
        if (!isNoteSaved && !noteContent.isEmpty() && !isChangingConfigurations()) {
            saveNote();
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Set the rotating variable based on the new configuration
        isRotating = true;
    }

    private void saveNote() {
        String title = titleee.getText().toString().trim();
        String noteContent = write.getText().toString().trim();

        if (noteContent.isEmpty()) {
            // Show an error message if note content is empty
            Toast.makeText(NoteActivity.this, "Please enter note content", Toast.LENGTH_SHORT).show();
        } else if (title == null || title.isEmpty()) {
            // If title is null or empty, set it to "Note" + current date
            String currentDate = new SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(new Date());
            title = "Note " + currentDate;
            titleee.setText(title);
            String datetime = new SimpleDateFormat("dd MM yyyy HH:mm a", Locale.getDefault())
                    .format(new Date());

            Note note = new Note(title, datetime, noteContent);
            if (noteId != -1) {
                // If noteId is available, it's an existing note, so update it
                note.setN_ID(noteId);
            }
            // Call addOrUpdateNote to handle insertion or update
            db.addOrUpdateNote(note);

            // Set the flag to indicate that the note is saved
            isNoteSaved = true;

            // Return to MainActivity with the updated note information
            setResultAndFinish();
            Toast.makeText(NoteActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        } else {
            // Save the note if note content is provided
            String datetime = new SimpleDateFormat("EEEE, dd MM yyyy HH:mm a", Locale.getDefault())
                    .format(new Date());

            Note note = new Note(title, datetime, noteContent);
            if (noteId != -1) {
                // If noteId is available, it's an existing note, so update it
                note.setN_ID(noteId);
            }

            // Call addOrUpdateNote to handle insertion or update
            db.addOrUpdateNote(note);

            // Set the flag to indicate that the note is saved
            isNoteSaved = true;

            // Return to MainActivity with the updated note information
            setResultAndFinish();
            Toast.makeText(NoteActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void setResultAndFinish() {
        // Return to MainActivity with a result code indicating whether the note was saved
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isNoteSaved", isNoteSaved);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
