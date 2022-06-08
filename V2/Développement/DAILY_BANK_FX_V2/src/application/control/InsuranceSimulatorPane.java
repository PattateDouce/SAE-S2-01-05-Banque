package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.InsuranceSimulatorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InsuranceSimulatorPane {
    private Stage primaryStage;
    private InsuranceSimulatorPaneController ispc;

    /**
     * Instantiates a new Waranty Simulator pane.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public InsuranceSimulatorPane(Stage _parentStage, DailyBankState _dbstate) {
        try {
            FXMLLoader loader = new FXMLLoader(InsuranceSimulatorPaneController.class.getResource("insurancesimulatorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Simulateur d'assurance d'emprunt");
            this.primaryStage.setResizable(false);

            this.ispc = loader.getController();
            this.ispc.initContext(this.primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to create a insurance simulation
     */
    public void doSimulationDialog() {
    	this.ispc.displayDialog();
    }
}
