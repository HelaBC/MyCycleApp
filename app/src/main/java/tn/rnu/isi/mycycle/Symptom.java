package tn.rnu.isi.mycycle;

public class Symptom {
    private long id;
    private long entryId;
    private String name; // Cramps, Headache, Fatigue, Mood Swings, Bloating, etc.
    private int intensity; // 0-10 scale

    public Symptom(long id, long entryId, String name, int intensity) {
        this.id = id;
        this.entryId = entryId;
        this.name = name;
        this.intensity = intensity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}

