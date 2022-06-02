package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class WarantySimulatorPaneController implements Initializable {

        // Etat application
        private DailyBankState dbs;

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
         * @param _dbstate      the dbstate
         */
        public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
            this.primaryStage = _primaryStage;
            this.dbs = _dbstate;
            this.configure();
        }

        /**
         * Initialise les labels et les events et d'autres objets
         */
        private void configure() {
            this.primaryStage.setOnCloseRequest(e -> {e.consume(); this.closeWindow();} );
        }

        /**
         * Display dialog simulation.
         **/
        public void displayDialog() {
            this.primaryStage.showAndWait();
        }

        /**
         * Fermeture de la fenêtre
         */
        private void closeWindow() {
            this.doCancel();
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
        
        /** Converti un String en Double
         * @param s		chaîne à convertir
         * @return Un Double si la chaîne correspond à un Double, sinon -1
         */
        private double toDouble(String s) {
        	try {
        		double number = Double.parseDouble(s);
        		return number;
        	} catch(Exception e) {
        		return -1;
        	}
        }

        /** Vérifie si les entrée sont valides
         * @return Vrai si l'entrée est valide, Faux sinon
         */
        private boolean isSaisieValide() {
        	this.capitalEmprunt = toDouble(this.tfCapitalEmprunt.getText().trim());
        	this.dureeAnnee = toDouble(this.tfDureeAnnee.getText().trim());
        	this.tauxAssurance = toDouble(this.tfTauxAssurance.getText().trim());
        	this.fraisDossier = toDouble(this.tfFraisDossier.getText().trim());

    		if (capitalEmprunt == -1 || capitalEmprunt < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le capital doit être un nombre (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfCapitalEmprunt.requestFocus();
    			return false;
    		}
    		if (dureeAnnee == -1 || dureeAnnee < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La durée doit être un nombre entier d'années (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfDureeAnnee.requestFocus();
    			return false;
    		}
    		if (tauxAssurance == -1 || tauxAssurance < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux doit être un nombre (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfTauxAssurance.requestFocus();
    			return false;
    		}
    		if (fraisDossier == -1 || fraisDossier < 0) {
    			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les frais de dossier doivent être un nombre (sans espaces, les virgules doivent être des points)",
    					AlertType.WARNING);
    			this.tfFraisDossier.requestFocus();
    			return false;
    		}
    		
            return true;
        }
    }
