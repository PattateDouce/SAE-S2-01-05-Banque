package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.WarantySimulatorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WarantySimulatorPane {
    private Stage primaryStage;
    private WarantySimulatorPaneController wspc;

    /**
     * Instantiates a new Waranty Simulator pane.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public WarantySimulatorPane(Stage _parentStage, DailyBankState _dbstate) {
        try {
            FXMLLoader loader = new FXMLLoader(WarantySimulatorPaneController.class.getResource("warantysimulatorpane.fxml"));
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

            this.wspc = loader.getController();
            this.wspc.initContext(this.primaryStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to create a waranty simulation
     *
     * @return the simulated waranty
     */
    public void doSimulationDialog() {
    	this.wspc.displayDialog();
    }
}
