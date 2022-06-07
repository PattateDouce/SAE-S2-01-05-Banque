package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import application.view.LoanSimulatorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Emprunt;

public class LoanSimulatorPane {
    private Stage primaryStage;
    private LoanSimulatorPaneController cepc;

    /**
     * Instantiates a new Compte editor pane.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public LoanSimulatorPane(Stage _parentStage, DailyBankState _dbstate) {

        try {
            FXMLLoader loader = new FXMLLoader(LoanSimulatorPaneController.class.getResource("loansimulatorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage .initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Simulation d'un emprunt");
            this.primaryStage.setResizable(false);

            this.cepc = loader.getController();
            this.cepc.initContext(this.primaryStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to create a load simulation
     *
     * @return the simulated loan
     */
    public void doLoadSimulation() {
        this.cepc.displayDialog();
    }
}
