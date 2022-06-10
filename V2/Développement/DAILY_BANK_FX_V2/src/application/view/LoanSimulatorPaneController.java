package application.view;

import application.tools.AlertUtilities;
import application.tools.LoanSimulatorTool;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.data.Emprunt;
import model.data.Periode;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoanSimulatorPaneController implements Initializable {

        // Fenêtre physique
        private Stage primaryStage;


        @FXML
        private TextField capital;

        @FXML
        private TextField duree;

        @FXML
        private TextField tauxAnnuel;

        /**
         * Init context.
         *
         * @param _primaryStage the primary stage
         * @param _dbstate      the dbstate
         */
        // Manipulation de la fenêtre
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
        }

        /**
         * Display dialog simulateur d'emprunt.
         *
         **/
        public void displayDialog() {
            primaryStage.showAndWait();
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            this.capital.focusedProperty().addListener((t, o, n) -> this.focusCapital(t, o, n));
            this.duree.focusedProperty().addListener((t, o, n) -> this.focusDuree(t, o, n));
            this.tauxAnnuel.focusedProperty().addListener((t, o, n) -> this.focusTaux(t, o, n));
        }

        @FXML
        private void doCancel() {
            this.primaryStage.close();
        }

        @FXML
        private void doSimulation() {
           if (isSaisieValide()) {

               Emprunt emprunt = new Emprunt();
               emprunt.capital = Double.valueOf(capital.getText());
               emprunt.tauxAnnuel = Double.valueOf(tauxAnnuel.getText());
               emprunt.duree = Integer.valueOf(duree.getText());


               List<Periode> periodes = LoanSimulatorTool.simulateLoan(emprunt);


               int numPeriodes = periodes.size();
               double mensualite = periodes.get(0).montantARembourser;

               AlertUtilities.showAlert(this.primaryStage, "Simulation", null,"Il faudra "+numPeriodes+" mois pour rembourser cet emprunt avec une mensualité de "+ Math.round(mensualite)+".",
                       Alert.AlertType.INFORMATION);
           }

           else {
               AlertUtilities.showAlert(this.primaryStage, "Erreur de simulation", "Vous avez entré des valeurs incorrectes", null,
                       Alert.AlertType.ERROR);
           }
        }

        /** Vérfifie si les saisie sont valides
         * @return Vrai si tout est correct, Faux sinon
         */
        private boolean isSaisieValide() {
            try {
               double capitalD = Double.valueOf(capital.getText());
               double tx =  Double.valueOf(tauxAnnuel.getText());
               int dureeI = Integer.valueOf(duree.getText());

               if (capitalD > 0 && tx > 0 && dureeI > 0) {
                   return true;
               }

            } catch (Exception e) {
                return false;
            }

            return false;
        }

		private Object focusCapital(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
		                            boolean newPropertyValue) {
		    if(oldPropertyValue) {
		        try {
		            int val;
		            val = Integer.parseInt(this.capital.getText().trim());
		            if (val <= 0) {
		                throw new NumberFormatException();
		            }
		        } catch (Exception e) {
		            this.capital.setText("1");
		        }
		    }
		    return null;
		}
		
		private Object focusDuree(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
		                            boolean newPropertyValue) {
		    if(oldPropertyValue) {
		        try {
		            int val;
		            val = Integer.parseInt(this.duree.getText().trim());
		            if (val <= 0) {
		                throw new NumberFormatException();
		            }
		        } catch (Exception e) {
		            this.duree.setText("1");
		        }
		    }
		    return null;
		}
		
		private Object focusTaux(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
		                            boolean newPropertyValue) {
		    if(oldPropertyValue) {
		        try {
		            Double val;
		            val = Double.parseDouble(this.tauxAnnuel.getText().trim());
		            if (val <= 0 || val >= 1) {
		                throw new NumberFormatException();
		            }
		        } catch (Exception e) {
		            this.tauxAnnuel.setText("1");
		        }
		    }
		    return null;
		}
}

