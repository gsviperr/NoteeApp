package com.example.noteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.noteapp.Domain.Note;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Note";
    private static final String TABLE_NOTE = "Notes";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NOTE + " (N_ID INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, datetime TEXT, notetext TEXT,status)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(db);
    }

    public void addOrUpdateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", note.getTITLE());
        values.put("datetime", note.getDATETIME());
        values.put("notetext", note.getNOTETEXT());
        String stt = "active";
        values.put("status",stt);

        Cursor cursor = db.query(TABLE_NOTE, null, "N_ID=?", new String[]{String.valueOf(note.getN_ID())}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // Note exists, perform an update
            int noteId = cursor.getInt(cursor.getColumnIndex("N_ID"));
            db.update(TABLE_NOTE, values, "N_ID=?", new String[]{String.valueOf(noteId)});
        } else {
            // Note doesn't exist, perform an insert
            db.insert(TABLE_NOTE, null, values);
        }

        if (cursor != null) {
            cursor.close();
        }


    }

    public List<Note> getALLNote() {
        List<Note> noteList = new ArrayList<>();

        String selectQuery = "SELECT * FROM notes WHERE status = 'active'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndex("N_ID")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("datetime")),
                        cursor.getString(cursor.getColumnIndex("notetext"))
                );

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return noteList;
    }
    public List<Note> getAllNoteBin() {
        List<Note> noteList = new ArrayList<>();

        String selectQuery = "SELECT * FROM notes WHERE status = 'disable'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndex("N_ID")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("datetime")),
                        cursor.getString(cursor.getColumnIndex("notetext"))
                );

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return noteList;
    }
    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "disable");

        db.update(TABLE_NOTE, values, "N_ID = ?", new String[]{String.valueOf(noteId)});

    }
    public void restorNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "active");

        db.update(TABLE_NOTE, values, "N_ID = ?", new String[]{String.valueOf(noteId)});

    }
    public void deleteNoteById(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, "N_ID = ?", new String[]{String.valueOf(noteId)});
    }
    public Cursor getdataNote() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE status = ?", new String[]{"active"});
    }
    public Cursor getdataNoteBin() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTE + " WHERE status = ?", new String[]{"disable"});
    }
}
