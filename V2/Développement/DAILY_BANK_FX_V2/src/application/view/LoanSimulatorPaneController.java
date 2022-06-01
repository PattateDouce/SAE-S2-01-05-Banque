package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Emprunt;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoanSimulatorPaneController implements Initializable {

        // Etat application
        private DailyBankState dbs;

        // Fenêtre physique
        private Stage primaryStage;

        // Données de la fenêtre
        private EditionMode em;
        private Client clientDuCompte;
        private CompteCourant compteEdite;
        private CompteCourant compteResult;

        private boolean isConfirmed;
        /**
         * Init context.
         *
         * @param _primaryStage the primary stage
         * @param _dbstate      the dbstate
         */
        // Manipulation de la fenêtre
        public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
            this.primaryStage = _primaryStage;
            this.dbs = _dbstate;
            this.configure();
        }

        /**
         * Initialise les labels et les events et d'autres objets
         */
        private void configure() {
            this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        }

        /**
         * Display dialog compte courant.
         *
         **/
        public Emprunt displayDialog() {

            AlertUtilities.showAlert(this.primaryStage, "Non implémenté", "Modif de compte n'est pas implémenté", null,
                            Alert.AlertType.ERROR);
            return null;



        }

        // Gestion du stage
        private Object closeWindow(WindowEvent e) {
            this.doCancel();
            e.consume();
            return null;
        }






        @Override
        public void initialize(URL location, ResourceBundle resources) {
        }

        @FXML
        private void doCancel() {
            this.compteResult = null;
            this.primaryStage.close();
        }

        @FXML
        private void doSimulation() {
            // todo

        }

        private boolean isSaisieValide() {

            return true;
        }


    }

