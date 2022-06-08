package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

/**
 * The type Compte editor pane.
 */
public class PrelevementEditorPane {

	private Stage primaryStage;
	private PrelevementEditorPaneController pepc;

    /**
     * Instantiates a new Compte editor pane.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("prelevementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un prélèvement");
			this.primaryStage.setResizable(false);

			this.pepc = loader.getController();
			this.pepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Do compte editor dialog compte courant.
     *
     * @param prelev the prelevement
     * @param cpte   the cpte
     * @param em     the em
     * @return the compte courant
     */
    public Prelevement doPrelevementEditorDialog(Prelevement prelev, CompteCourant cpte, EditionMode em) {
		return this.pepc.displayDialog(prelev, cpte, em);
	}
}
