package sample;

import com.datastax.driver.core.LocalDate;

public class Patient {
    private int patient_id;
    private String maladie;
    private String symptomes;
    private LocalDate dateinscription;
    private String medecin_traitant;

    public Patient(int patient_id, String maladie, String symptomes, LocalDate dateinscription, String medecin_traitant) {
        this.patient_id = patient_id;
        this.maladie = maladie;
        this.symptomes = symptomes;
        this.dateinscription = dateinscription;
        this.medecin_traitant = medecin_traitant;
    }

    public Patient(int patient_id, String maladie, String symptomes) {
        this.patient_id = patient_id;
        this.maladie = maladie;
        this.symptomes = symptomes;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getMaladie() {
        return maladie;
    }

    public void setMaladie(String maladie) {
        this.maladie = maladie;
    }

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }

    public LocalDate getDateinscription() {
        return dateinscription;
    }

    public void setDateinscription(LocalDate dateinscription) {
        this.dateinscription = dateinscription;
    }

    public String getMedecin_traitant() {
        return medecin_traitant;
    }

    public void setMedecin_traitant(String medecin_traitant) {
        this.medecin_traitant = medecin_traitant;
    }

    public Patient() {
    }
}
