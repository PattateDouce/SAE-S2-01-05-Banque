package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.control.EmployesManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.data.Employe;

/**
 * Contrôleur de la liste des employés
 */
public class EmployesManagementController implements Initializable {

	// Etat application
	private EmployesManagement em;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> ole;

    /**
     * Init context.
     *
     * @param _primaryStage the primary stage
     * @param _em           the em
     */
	public void initContext(Stage _primaryStage, EmployesManagement _em) {
		this.em = _em;
		this.primaryStage = _primaryStage;
		this.configure();
	}

	/**
	 * Initialise les labels et les events et d'autres objets
	 */
	private void configure() {
		this.ole = FXCollections.observableArrayList();
		this.lvEmploye.setItems(this.ole);
		this.lvEmploye.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmploye.getFocusModel().focus(-1);
		this.lvEmploye.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Employe> lvEmploye;
	@FXML
	private Button btnSuppEmploye;
	@FXML
	private Button btnModifEmploye;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des employés en BD. cf. AccessEmploye > getEmployes(.)
		// idEmp != -1 => recherche sur idEmp
		// idEmp != -1 et debutNom non vide => recherche nom/prenom
		// idEmp != -1 et debutNom vide => recherche tous les employés
		ArrayList<Employe> listeEmp;
		listeEmp = this.em.getlisteEmploye(numCompte, debutNom, debutPrenom);

		this.ole.clear();
		for (Employe emp : listeEmp) {
			this.ole.add(emp);
		}

		this.validateComponentState();
	}

	@FXML
	private void doModifierEmploye() {
		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.ole.get(selectedIndice);
			Employe result = this.em.modifierEmploye(empMod);
			if (result != null) {
				this.ole.set(selectedIndice, result);
			}
		}
	}

	@FXML
	private void doSuppEmploye() {
		if (AlertUtilities.confirmYesCancel(this.primaryStage, "Suppression d'employé",
				"Êtes vous sur de vouloir supprimer l'employé ?", null, AlertType.CONFIRMATION)) {
			int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				Employe empMod = this.ole.get(selectedIndice);
				if (this.em.supprimerEmploye(empMod.idEmploye)) {
					this.ole.remove(selectedIndice);
				}
			}
		}
	}

	@FXML
	private void doNouvelEmploye() {
		Employe employe;
		employe = this.em.nouvelEmploye();
		if (employe != null) {
			this.ole.add(employe);
		}
	}

	/**
	 * Vérifie si certain bouton doivent être activable ou non
	 */
	private void validateComponentState() {
		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifEmploye.setDisable(false);
			this.btnSuppEmploye.setDisable(false);
		} else {
			this.btnModifEmploye.setDisable(true);
			this.btnSuppEmploye.setDisable(true);
		}
	}
}
