package sample;

import com.datastax.driver.core.LocalDate;

public class Radio {
    private int radio_id;
    private String type;
    private String medecin_traitant;
    private LocalDate date_radio;

    public int getRadio_id() {
        return radio_id;
    }

    public void setRadio_id(int radio_id) {
        this.radio_id = radio_id;
    }

    public Radio(int radio_id, String type, String medecin_traitant, LocalDate date_radio) {
        this.radio_id = radio_id;
        this.type = type;
        this.medecin_traitant = medecin_traitant;
        this.date_radio = date_radio;
    }



    public String getMedecin_traitant() {
        return medecin_traitant;
    }

    public void setMedecin_traitant(String medecin_traitant) {
        this.medecin_traitant = medecin_traitant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    public LocalDate getDate_radio() {
        return date_radio;
    }

    public void setDate_radio(LocalDate date_radio) {
        this.date_radio = date_radio;
    }
}
