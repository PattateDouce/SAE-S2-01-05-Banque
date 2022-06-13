package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.LoanSimulatorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoanSimulatorPane {
    private Stage primaryStage;
    private LoanSimulatorPaneController lspc;

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

            this.lspc = loader.getController();
            this.lspc.initContext(this.primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to create a loan simulation
     *
     */
    public void doLoadSimulation() {
        this.lspc.displayDialog();
    }
}
