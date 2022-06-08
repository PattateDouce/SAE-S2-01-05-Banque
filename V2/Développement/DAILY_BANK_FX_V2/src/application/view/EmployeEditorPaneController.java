package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.data.Employe;

/**
 * Contrôleur de la page de modification/création d'un employé
 */
public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Employe employeEdite;
	private Employe employeResult;

	/**
	 * Init context.
	 *
	 * @param _primaryStage the primary stage
	 * @param _dbstate      the dbstate
	 */
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Initialise la fermeture de l'application voulue
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.doCancel() );
	}

	/**
	 * Display dialog employe.
	 *
	 * @param employe the employe
	 * @return the employe
	 */
	public Employe displayDialog(Employe employe) {

		// employe == null -> création d'un employé / employe != null -> modification d'un employé
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}

		// initialisation du contenu des champs
		this.txtIdemp.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtLogin.setText(this.employeEdite.login);
		this.txtMotPasse.setText(this.employeEdite.motPasse);

		if (this.employeEdite.droitsAccess.equals("chefAgence")) {
			this.guichetier.setSelected(false);
			this.chefAgence.setSelected(true);
		} else {
			this.guichetier.setSelected(true);
			this.chefAgence.setSelected(false);
		}

		// affichage de la fenêtre
		this.primaryStage.showAndWait();
		return this.employeResult;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdemp;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private RadioButton guichetier;
	@FXML
	private RadioButton chefAgence;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtMotPasse;
	@FXML
	private ToggleGroup droitsacces;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.employeResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		if (this.isSaisieValide()) {
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
		}
	}

	/** Vérifie la validité des saisies
	 * @return True si tout est correct, False sinon
	 */
	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		this.employeEdite.motPasse = this.txtMotPasse.getText().trim();
		if (this.guichetier.isSelected()) {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_GUICHETIER;
		} else {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_CHEF;
		}

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}
		if (this.employeEdite.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide",
					AlertType.WARNING);
			this.txtLogin.requestFocus();
			return false;
		}
		if (this.employeEdite.motPasse.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe ne doit pas être vide",
					AlertType.WARNING);
			this.txtMotPasse.requestFocus();
			return false;
		}

		return true;
	}
}
