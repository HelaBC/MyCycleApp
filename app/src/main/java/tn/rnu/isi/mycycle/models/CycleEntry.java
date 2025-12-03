package tn.rnu.isi.mycycle.models;

import java.util.List;
import tn.rnu.isi.mycycle.models.Symptom;

public class CycleEntry {
    private long id;
    private long userId;
    private String date;
    private String phase; // Menstrual, Follicular, Ovulation, Luteal
    private String mood; // 😊, 😐, 😔, 😡
    private String notes;
    private List<Symptom> symptoms;

    public CycleEntry(long id, long userId, String date, String phase, String mood, String notes) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.phase = phase;
        this.mood = mood;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }
}

