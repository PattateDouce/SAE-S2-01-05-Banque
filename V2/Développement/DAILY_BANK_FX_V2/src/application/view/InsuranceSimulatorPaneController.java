package application.view;

import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InsuranceSimulatorPaneController implements Initializable {

        // Fenêtre physique
        private Stage primaryStage;

        // Données de la fenêtre
        private double capitalEmprunt;
        private double dureeAnnee;
        private double tauxAssurance;
        private double fraisDossier;

        /**
         * Init context.
         *
         * @param _primaryStage the primary stage
         */
        public void initContext(Stage _primaryStage) {
            this.primaryStage = _primaryStage;
            this.configure();
        }

    	/**
    	 * Initialise les labels et les events et d'autres objets
    	 */
    	private void configure() {
    		this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ENTER) {
    				doSimulation();
    			} } );
    		this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
    				doCancel();
    			} } );
    		this.tfCapitalEmprunt.requestFocus();
    	}
        
        /**
         * Display dialog simulation.
         **/
        public void displayDialog() {
            this.primaryStage.showAndWait();
        }
        
        
        // Attributs de la scene + actions
    	@FXML
    	private TextField tfCapitalEmprunt;
    	@FXML
    	private TextField tfDureeAnnee;
    	@FXML
    	private TextField tfTauxAssurance;
    	@FXML
    	private TextField tfFraisDossier;
    	@FXML
    	private Label lbTotal;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
        }

        @FXML
        private void doCancel() {
            this.primaryStage.close();
        }

        @FXML
        private void doSimulation() {
        	if (isSaisieValide()) {
        		double mensualite = tauxAssurance/100 * (capitalEmprunt/12);
        		double dureeMois = dureeAnnee * 12;
        		double coutAssurance = mensualite * dureeMois;
        		double total = coutAssurance + fraisDossier;
        		this.lbTotal.setText("Total : " + String.valueOf(total) + " €");
        	}
        }
        
        /** Convertie un String en Double
         * @param number	chaîne à convertir
         * @return Le Double correspondant à la chaîne, ou -1 si elle ne correspond pas à un Double
         */
        private double toDouble(String number) {
        	try {
        		return Double.parseDouble(number);
        	} catch(Exception e) {
        		return -1;
        	}
        }

        /** Vérifie si les entrées sont valides
         * @return Vrai si les entrées sont valides, Faux sinon
         */
        private boolean isSaisieValide() {
        	this.capitalEmprunt = toDouble(this.tfCapitalEmprunt.getText());
        	this.dureeAnnee = toDouble(this.tfDureeAnnee.getText());
        	this.tauxAssurance = toDouble(this.tfTauxAssurance.getText());
        	this.fraisDossier = toDouble(this.tfFraisDossier.getText());

    		if (capitalEmprunt < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le capital doit être un nombre (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfCapitalEmprunt.requestFocus();
    			return false;
    		}
    		if (dureeAnnee < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La durée doit être un nombre entier d'années (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfDureeAnnee.requestFocus();
    			return false;
    		}
    		if (tauxAssurance < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux doit être un nombre (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfTauxAssurance.requestFocus();
    			return false;
    		}
    		if (fraisDossier < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les frais de dossier doivent être un nombre (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfFraisDossier.requestFocus();
    			return false;
    		}
    		
            return true;
        }
    }
