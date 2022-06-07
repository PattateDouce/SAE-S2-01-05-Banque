package application.view;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The type Comptes management controller.
 */
public class PrelevementsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private PrelevementsManagement pm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CompteCourant compte;
	private ObservableList<CompteCourant> olCompteCourant;

	/**
	 * Init context.
	 *  @param _primaryStage the primary stage
	 * @param _pm           the pm
	 * @param _dbstate      the dbstate
	 */
// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, PrelevementsManagement _pm, DailyBankState _dbstate, CompteCourant compte) {
		this.pm = _pm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.compte = compte;
		this.configure();
	}

	/**
	 * Initialise les labels et les events et d'autres objets
	 */
	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvPrelev.setItems(this.olCompteCourant);
		this.lvPrelev.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelev.getFocusModel().focus(-1);
		this.lvPrelev.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = String.valueOf(this.compte.idNumCompte);
		this.lblInfos.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Display dialog.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblInfos;
	@FXML
	private ListView<CompteCourant> lvPrelev;
	@FXML
	private Button btnModifier;
	@FXML
	private Button btnSuppr;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doModif() {

	}

	@FXML
	private void doSupprimer() {

	}

	@FXML
	private void doNouveau() {

	}

	private void loadList () {
		/*ArrayList<CompteCourant> listeCpt;
		listeCpt = this.pm.
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}*/
	}

	private void validateComponentState() {

		int selectedIndice = this.lvPrelev.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifier.setDisable(false);
			this.btnSuppr.setDisable(false);


			CompteCourant cpt = lvPrelev.getItems().get(selectedIndice);

			if (cpt != null) {

				if (cpt.estCloture.equals("O") || cpt.solde != 0) {
					btnModifier.setDisable(true);
					btnSuppr.setDisable(true);
				}
			}

		} else {
			this.btnModifier.setDisable(true);
			this.btnSuppr.setDisable(true);
		}


	}
}