package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmployeEditorPaneController;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

/**
 * Classe "Pane" de la modification/création d'un employé
 */
public class EmployeEditorPane {

	private Stage primaryStage;
	private EmployeEditorPaneController eepc;

    /**
     * Instantiates a new Employe editor pane.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(EmployesManagementController.class.getResource("employeeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un client");
			this.primaryStage.setResizable(false);

			this.eepc = loader.getController();
			this.eepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Show employe editor dialog.
     *
     * @param employe 	the employe to display
     * @return the employe
     */
    public Employe doEmployeEditorDialog(Employe employe) {
		return this.eepc.displayDialog(employe);
	}
}
