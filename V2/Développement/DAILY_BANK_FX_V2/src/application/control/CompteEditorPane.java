package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

/**
 * The type Compte editor pane.
 */
public class CompteEditorPane {

	private Stage primaryStage;
	private CompteEditorPaneController cepc;

    /**
     * Instantiates a new Compte editor pane.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public CompteEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("compteeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un compte");
			this.primaryStage.setResizable(false);

			this.cepc = loader.getController();
			this.cepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Do compte editor dialog compte courant.
     *
     * @param client the client
     * @param cpte   the cpte
     * @param em     the em
     * @return the compte courant
     */
    public CompteCourant doCompteEditorDialog(Client client, CompteCourant cpte, EditionMode em) {
		return this.cepc.displayDialog(client, cpte, em);
	}
}
