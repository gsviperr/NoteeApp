package com.example.noteapp.Domain;

public class Note {
    private int N_ID;
    private String TITLE;
    private String DATETIME;
    private String NOTETEXT;

    public Note(int n_ID, String TITLE, String DATETIME, String NOTETEXT) {
        this.N_ID = n_ID;
        this.TITLE = TITLE;
        this.DATETIME = DATETIME;
        this.NOTETEXT = NOTETEXT;
    }
    public Note(String TITLE, String DATETIME, String NOTETEXT) {
        this.TITLE = TITLE;
        this.DATETIME = DATETIME;
        this.NOTETEXT = NOTETEXT;
    }

    public int getN_ID() {
        return N_ID;
    }

    public void setN_ID(int n_ID) {
        N_ID = n_ID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getDATETIME() {
        return DATETIME;
    }

    public void setDATETIME(String DATETIME) {
        this.DATETIME = DATETIME;
    }

    public String getNOTETEXT() {
        return NOTETEXT;
    }

    public void setNOTETEXT(String NOTETEXT) {
        this.NOTETEXT = NOTETEXT;
    }
}
