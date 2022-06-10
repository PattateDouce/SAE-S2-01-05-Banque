package application.view;

import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Prelevement;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The type Comptes management controller.
 */
public class PrelevementsManagementController implements Initializable {

	// Etat application
	private PrelevementsManagement pm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CompteCourant compte;
	private ObservableList<Prelevement> olPrelevements;

	/**
	 * Init context.
	 * @param _primaryStage the primary stage
	 * @param _pm           the pm
	 * @param compte		the account
	 */
// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, PrelevementsManagement _pm, CompteCourant compte) {
		this.pm = _pm;
		this.primaryStage = _primaryStage;
		this.compte = compte;
		this.configure();
	}

	/**
	 * Initialise les labels et les events et d'autres objets
	 */
	private void configure() {
		String info;

		this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
				doCancel();
			} } );

		this.olPrelevements = FXCollections.observableArrayList();
		this.lvPrelev.setItems(this.olPrelevements);
		this.lvPrelev.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelev.getFocusModel().focus(-1);
		this.lvPrelev.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = "Numéro de compte : " + this.compte.idNumCompte;
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

	// Attributs de la scene + actions
	@FXML
	private Label lblInfos;
	@FXML
	private ListView<Prelevement> lvPrelev;
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
		int selectedIndice = this.lvPrelev.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelevEdite = this.lvPrelev.getItems().get(selectedIndice);
			Prelevement result = this.pm.modifierPrelev(prelevEdite);
			if (result != null) {
				this.olPrelevements.set(selectedIndice, result);
			}
		}
	}

	@FXML
	private void doSupprimer() {
		int selectedIndice = this.lvPrelev.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelevSuppr = this.lvPrelev.getItems().get(selectedIndice);
			Prelevement result = this.pm.supprimerPrelev(prelevSuppr);
			if (result != null) {
				this.olPrelevements.set(selectedIndice, result);
			}
		}
	}

	@FXML
	private void doNouveau() {
		Prelevement nouveau = this.pm.nouveauPrelev();
		if (nouveau != null) {
			this.olPrelevements.add(nouveau);
		}
	}

	private void loadList () {
		ArrayList<Prelevement> listePrelev;
		listePrelev = this.pm.getListePrelev();
		this.olPrelevements.clear();
		for (Prelevement prelev : listePrelev) {
			this.olPrelevements.add(prelev);
		}
	}

	private void validateComponentState() {

		int selectedIndice = this.lvPrelev.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifier.setDisable(false);
			this.btnSuppr.setDisable(false);

			Prelevement prelev = lvPrelev.getItems().get(selectedIndice);

			if(prelev.getDatePrelevement() == 0) {
				this.btnSuppr.setDisable(true);
			}
		} else {
			this.btnModifier.setDisable(true);
			this.btnSuppr.setDisable(true);
		}

	}
}