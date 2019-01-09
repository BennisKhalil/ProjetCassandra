package sample;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.Bytes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import static sample.Controller.results;

public class Controller2 implements Initializable {

    public Controller2() {
    }
    //variables pour la connection et le requetage
    public static Cluster cluster;
    public static Session session;
    public static PreparedStatement prepared;
    public static PreparedStatement prepared2;
    public static BoundStatement bound;
    public static BoundStatement bound2;
    public static ResultSet results;
    public static ResultSet results2;

    // variables pour stocker respectivement l'id du patient , son nom et l'image
    public static int patient;
    public static String nompatient;
    public Image image2;



    //variables graphiques
    @FXML
    public Text title;

    @FXML
    public Button btn;

    @FXML
    public ImageView image3;

    @FXML
    public ComboBox<String> cb;
    @FXML
    public ComboBox<Integer> cb2;

    @FXML
    public DatePicker dpmin;
    @FXML
    public DatePicker dpmax;

    @FXML
    public TableView<Radio> tv;
    @FXML
    public TableColumn<Radio, String> c_type;
    @FXML
    public TableColumn<Radio, LocalDate> c_date;
    @FXML
    public TableColumn<Radio, String> c_medecin;
    @FXML
    public TableColumn<Radio, Integer> c_id;


    //Cette fonction est déclenchée lors du choix de la maladie dans une liste deroulante
    @FXML
    public void oncbselection(){
        //initialiser l'image et creation de la requete qui permet de recuperer les données sur les radios ayant une maladie comme input


        image3.setImage(null);
        prepared2 = session.prepare("select * from radio_by_patient where patient_id = ? and maladie = ?;");
        bound2 = prepared2.bind(patient, cb.getSelectionModel().getSelectedItem());
        ObservableList<Radio> radios = FXCollections.observableArrayList();
        ObservableList<Integer> radios_id = FXCollections.observableArrayList();
        results2 = session.execute(bound2);
        for (Row row : results2){
            radios.add(new Radio(row.getInt("radio_id"),row.getString("type"), row.getString("medecin_traitant"), row.getDate("date_radio")));
            radios_id.add(row.getInt("radio_id"));
        }

        // populer le tableau par les données


        c_id.setCellValueFactory(new PropertyValueFactory<Radio, Integer>("radio_id"));
        c_type.setCellValueFactory(new PropertyValueFactory<Radio, String>("type"));
        c_medecin.setCellValueFactory(new PropertyValueFactory<Radio, String>("medecin_traitant"));
        c_date.setCellValueFactory(new PropertyValueFactory<Radio, LocalDate>("date_radio"));
        System.out.println(radios.toString());
        tv.getItems().setAll(radios);
        cb2.setItems(radios_id);
    }

    //Cette fonction permet de filtrer les radios par date
    @FXML
    public void ondateselection(){
        //creation de la requete pour filtrer sur la date du radio


        prepared = session.prepare(
                "select * from radio_by_patient where date_radio > ? and date_radio < ? and maladie = ? and patient_id = ? ALLOW FILTERING;");
        LocalDate datemin = LocalDate.fromYearMonthDay(dpmin.getValue().getYear(), dpmin.getValue().getMonthValue(), dpmin.getValue().getDayOfMonth());
        LocalDate datemax = LocalDate.fromYearMonthDay(dpmax.getValue().getYear(), dpmax.getValue().getMonthValue(), dpmax.getValue().getDayOfMonth());
        bound = prepared.bind(datemin, datemax, cb.getSelectionModel().getSelectedItem(), patient);
        ResultSet rs = session.execute(bound);
        ObservableList<Radio> radios = FXCollections.observableArrayList();
        for (Row row : rs){
            radios.add(new Radio(row.getInt("radio_id"),row.getString("type"), row.getString("medecin_traitant"), row.getDate("date_radio")));
        }

        //repopuler le tableau


        c_id.setCellValueFactory(new PropertyValueFactory<Radio, Integer>("radio_id"));
        c_type.setCellValueFactory(new PropertyValueFactory<Radio, String>("type"));
        c_medecin.setCellValueFactory(new PropertyValueFactory<Radio, String>("medecin_traitant"));
        c_date.setCellValueFactory(new PropertyValueFactory<Radio, LocalDate>("date_radio"));
        tv.getItems().setAll(radios);
    }


    //Cette fonction permet de selectionner la radio à afficher
    @FXML
    public void onradioselection(){
            //creation requete pour recuperer l'image par id


            cb2.getSelectionModel().getSelectedItem();
            PreparedStatement ps = session.prepare("select radio_image from radio_by_patient where radio_id = ? AND patient_id = ? allow filtering;");
            BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs =session.execute ( boundStatement.bind(cb2.getSelectionModel().getSelectedItem(), patient));
            ByteBuffer bImage;
            InputStream fis ;
            for (Row row : rs) {
                bImage = row.getBytes("radio_image");
                if(bImage != null) {
                    //initialiser l'image dans la fenetre
                    byte image[] = Bytes.getArray(bImage);
                    fis = new ByteArrayInputStream(image);
                    image2 = new Image(fis);
                    image3.setImage(image2);
                }

            }



    }

    @FXML
    public void initialize(URL location, ResourceBundle resources){

        //instancier les variables pour la connection


        cluster = Controller.cluster;
        session = Controller.session;
        title.setText("Radiographie de "+ nompatient);

        //creation du query qui permet de recuperer toutes les données sur les radios d'un patient + toutes les maladies du patient


        prepared  = Controller.session.prepare("select maladie from radio_by_patient where patient_id = ? group by patient_id, maladie allow filtering;");
        bound = prepared.bind(patient);
        prepared2 = session.prepare("select * from radio_by_patient where patient_id = ?;");
        bound2 = prepared2.bind(patient);
        ObservableList<String> data = FXCollections.observableArrayList();
        ObservableList<Radio> radios = FXCollections.observableArrayList();
        results = session.execute(bound);
        results2 = session.execute(bound2);
        for (Row row : results) {
            data.add(row.getString("maladie"));
        }

        for (Row row : results2){
            radios.add(new Radio(row.getInt("radio_id"),row.getString("type"), row.getString("medecin_traitant"), row.getDate("date_radio")));
        }

        //populer la table avec les données sur les radio


        c_id.setCellValueFactory(new PropertyValueFactory<Radio, Integer>("radio_id"));
        c_type.setCellValueFactory(new PropertyValueFactory<Radio, String>("type"));
        c_medecin.setCellValueFactory(new PropertyValueFactory<Radio, String>("medecin_traitant"));
        c_date.setCellValueFactory(new PropertyValueFactory<Radio, LocalDate>("date_radio"));
        tv.getItems().setAll(radios);

        //populer le combobox avec toute les maladies du patient


        cb.setItems(data);



    }




}
