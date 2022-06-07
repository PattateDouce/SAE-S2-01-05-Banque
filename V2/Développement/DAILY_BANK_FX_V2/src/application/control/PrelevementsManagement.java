package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

/**
 * The type Operations management.
 */
public class PrelevementsManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private PrelevementsManagementController pmc;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

    /**
     * Instantiates a new Operations management.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     * @param compte       the compte
     */
    public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, CompteCourant compte) {

		this.compteConcerne = compte;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélévements");
			this.primaryStage.setResizable(false);

			this.pmc = loader.getController();
			this.pmc.initContext(this.primaryStage, this, _dbstate, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Do prelevements management dialog.
	 */
	public void doPrelevementsManagement() {
		this.pmc.displayDialog();
	}

}
