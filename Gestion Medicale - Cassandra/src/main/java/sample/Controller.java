package sample;

import com.datastax.driver.core.*;
import com.datastax.driver.core.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // variables utilisés pour la connection et le requetage
    public static Cluster cluster;
    public static Session session;
    public static PreparedStatement prepared;
    public static BoundStatement bound;
    public static ResultSet results;

    // variable determinant l'id du patient selectionné
    public static int idpatient;


    public Controller() {
    }

    // éléments graphiques
    @FXML
    public ComboBox<String> cb;

    @FXML
    public DatePicker dpmin;
    @FXML
    public DatePicker dpmax;

    @FXML
    public TableView<Patient> tv;
    @FXML
    public TableColumn<Patient, LocalDate> c_dateins;
    @FXML
    public TableColumn<Patient, String> c_maladie;
    @FXML
    public TableColumn<Patient, String> c_symptomes;
    @FXML
    public TableColumn<Patient, String> c_medecin;


    @FXML
    public Text t_nom;
    @FXML
    public Text t_datenaissance;
    @FXML
    public Text t_telephone;
    @FXML
    public Text t_adresse;

    @FXML
    public Button btn;
    @FXML
    public Button btnRadio;


    //Cette fonction permet de selectionner un patient dans une liste deroulante
    @FXML
    public void oncbselection(){
        // creation du query pour récuperer le patient ayant un nom donné


        prepared = session.prepare(
                "select * from patient where nom = ? ;");
        String nom_select = cb.getSelectionModel().getSelectedItem();
        bound = prepared.bind(nom_select);
        ResultSet rs = session.execute(bound);
        ObservableList<Patient> patientinfo = FXCollections.observableArrayList();


        //iterer sur le resultat du query pour soustraire le patient


        for (Row row : rs) {
            patientinfo.add(new Patient(row.getInt("patient_id"), row.getString("maladie"), row.getString("symptomes"), row.getDate("dateinscription"), row.getString("medecin_traitant")));
        }

        Row r = session.execute(bound).one();


        //populer les champs vides et le tableau avec les données du patient


        idpatient = r.getInt("patient_id");
        t_nom.setText(r.getString("nom"));
        t_telephone.setText(r.getString("telephone"));
        t_adresse.setText(r.getString("adresse"));
        t_datenaissance.setText(r.getDate("datenaissance").toString());
        c_maladie.setCellValueFactory(new PropertyValueFactory<Patient, String>("maladie"));
        c_symptomes.setCellValueFactory(new PropertyValueFactory<Patient, String>("symptomes"));
        c_dateins.setCellValueFactory(new PropertyValueFactory<Patient, LocalDate>("dateinscription"));
        c_medecin.setCellValueFactory(new PropertyValueFactory<Patient, String>("medecin_traitant"));
        tv.getItems().setAll(patientinfo);
        btn.setDisable(false);
        btnRadio.setDisable(false);

    }


    //cette fonction se declenche lors de la recherche par date d'inscription
    @FXML
    public void onbtnselection(){
        //creation de la requete pour la recherche par dateinscription


        prepared = session.prepare(
                "select * from patient where dateinscription > ? and dateinscription < ? and nom = ? ALLOW FILTERING;");
        LocalDate datemin = LocalDate.fromYearMonthDay(dpmin.getValue().getYear(), dpmin.getValue().getMonthValue(), dpmin.getValue().getDayOfMonth());
        LocalDate datemax = LocalDate.fromYearMonthDay(dpmax.getValue().getYear(), dpmax.getValue().getMonthValue(), dpmax.getValue().getDayOfMonth());
        String nom_select = cb.getSelectionModel().getSelectedItem();
        bound = prepared.bind(datemin, datemax, nom_select);
        ResultSet rs = session.execute(bound);
        ObservableList<Patient> patientinfo = FXCollections.observableArrayList();


        //iterer sur le resultat du query pour soustraire les consultation


        for (Row row : rs) {
            patientinfo.add(new Patient(row.getInt("patient_id"), row.getString("maladie"), row.getString("symptomes"), row.getDate("dateinscription"), row.getString("medecin_traitant")));
        }

        //populer le tableau par le resultat de la recherche


        c_maladie.setCellValueFactory(new PropertyValueFactory<Patient, String>("maladie"));
        c_symptomes.setCellValueFactory(new PropertyValueFactory<Patient, String>("symptomes"));
        c_dateins.setCellValueFactory(new PropertyValueFactory<Patient, LocalDate>("dateinscription"));
        tv.getItems().setAll(patientinfo);
    }


    //lors du click sur le boutton " consulter les radios "
    @FXML
    public void onradiobtnclick() throws Exception {

        //fournir à a nouvelle page le nom du patient


        Controller2.patient = idpatient;
        Controller2.nompatient = cb.getSelectionModel().getSelectedItem();
                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL("file:/D:\\Intellij Projects\\Gestion Medicale - Cassandra\\src\\main\\java\\sample\\sample2.fxml"));
                AnchorPane root = loader.<AnchorPane>load();
                Scene dialogScene = new Scene(root);
                dialog.setScene(dialogScene);
                dialog.show();

            }

    // cette fonction est executée à l'ouverture de la fenetre
    @FXML
    public void initialize(URL location, ResourceBundle resources){
        //connection à cassandra
        cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9042).build();
        session = cluster.connect("gestionmedicale");


        //creation du query qui recupere le nom de tout les patients


        results = session.execute("select nom from patient group by patient_id allow filtering;");
        ObservableList<String> data = FXCollections.observableArrayList();
        for (Row row : results) {
            data.add(row.getString("nom"));
        }
        cb.setItems(data);
        btn.setDisable(true);
        btnRadio.setDisable(true);




    }

    // ce code commenté represente comment prendre une image du systeme de fichier
    // et la stocker sous format blob  dans cassandra


    /*public static void insertRadios(String image) throws IOException {
        FileInputStream fis=new FileInputStream(image);
        int lengthImage = fis.available()+1;
        byte[] b= new byte[lengthImage];
        fis.read(b);

        ByteBuffer buffer =ByteBuffer.wrap(b);


        PreparedStatement ps = session.prepare("update radio_by_patient set radio_image = ? where patient_id = ? AND maladie = ? AND radio_id = ?;");
        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute(  boundStatement.bind( buffer ,5,"Intoxication",51));
    }*/

}

