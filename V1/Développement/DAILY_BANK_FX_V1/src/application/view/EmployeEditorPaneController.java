package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

/**
 * The type Client editor pane controller.
 */
public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Employe employeEdite;
	private EditionMode em;
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
	 * Initialise les labels et les events et d'autres objets
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Display dialog employe.
	 *
	 * @param employe the employe
	 * @param mode   the mode
	 * @return the employe
	 */
	public Employe displayDialog(Employe employe, EditionMode mode) {

		this.em = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResult = null;
		switch (mode) {
		case CREATION:
//			this.txtIdemp.setDisable(true);
//			this.txtNom.setDisable(false);
//			this.txtPrenom.setDisable(false);
//			this.txtLogin.setDisable(false);
//			this.txtMotPasse.setDisable(false);
//			this.rbActif.setSelected(true);
//			this.rbInactif.setSelected(false);
//			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
//				this.rbActif.setDisable(false);
//				this.rbInactif.setDisable(false);
//			} else {
//				this.rbActif.setDisable(true);
//				this.rbInactif.setDisable(true);
//			}
//			this.lblMessage.setText("Informations sur le nouveau client");
//			this.butOk.setText("Ajouter");
//			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
//			this.txtIdemp.setDisable(true);
//			this.txtNom.setDisable(false);
//			this.txtPrenom.setDisable(false);
//			this.txtLogin.setDisable(false);
//			this.txtMotPasse.setDisable(false);
//			this.rbActif.setSelected(true);
//			this.rbInactif.setSelected(false);
//			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
//				this.rbActif.setDisable(false);
//				this.rbInactif.setDisable(false);
//			} else {
//				this.rbActif.setDisable(true);
//				this.rbInactif.setDisable(true);
//			}
//			this.lblMessage.setText("Informations client");
//			this.butOk.setText("Modifier");
//			this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Clients :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
//			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE",
//					null);
//			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
//			ed.doExceptionDialog();

			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs
		this.txtIdemp.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
//		this.txtAdr.setText(this.employeEdite.adressePostale);
//		this.txtMail.setText(this.employeEdite.email);
//		this.txtTel.setText(this.employeEdite.telephone);
//
//		if (ConstantesIHM.estInactif(this.employeEdite)) {
//			this.rbInactif.setSelected(true);
//		} else {
//			this.rbInactif.setSelected(false);
//		}

		this.employeResult = null;

		this.primaryStage.showAndWait();
		return this.employeResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
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
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}

	}

	/** Vérifie la validiter des saisies
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
