package tn.rnu.isi.mycycle.models;

public class SymptomFrequency {
    private String symptomName;
    private int count;

    public SymptomFrequency(String symptomName, int count) {
        this.symptomName = symptomName;
        this.count = count;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

