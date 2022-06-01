package application.view;

import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessClient;
import model.orm.AccessCompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

/**
 * The type Operation editor pane controller.
 */
public class OperationEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;
	private CompteCourant compteCible;

	// Elements de la fenetre de virement
	Label lblCompte;
	TextField txtCompte;

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
     * Display dialog operation.
     *
     * @param cpte the cpte
     * @param mode the mode
     * @return the operation
     */
    public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		switch (mode) {
		case DEBIT:

			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer Débit");
			this.btnCancel.setText("Annuler débit");

			ObservableList<String> list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_DEBIT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case CREDIT:
			String infoCredit = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde);
			this.lblMessage.setText(infoCredit);

			this.btnOk.setText("Effectuer crédit");
			this.btnCancel.setText("Annuler crédit");

			ObservableList<String> listCredit = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_CREDIT_GUICHET) {
				listCredit.add(tyOp);
			}

			this.cbTypeOpe.setItems(listCredit);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;

		case VIREMENT:
			String infoVirement = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(infoVirement);

			lblCompte = new Label("    N° compte à créditer");
			this.gridPane.add(lblCompte, 0, 2);

			txtCompte = new TextField();
			this.gridPane.add(txtCompte, 1, 2);

			this.btnOk.setText("Effectuer virement");
			this.btnCancel.setText("Annuler virement");

			ObservableList<String> listVirement = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_VIREMENT) {
				listVirement.add(tyOp);
			}

			this.cbTypeOpe.setItems(listVirement);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
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
	private Label lblMontant;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;
	@FXML
	private GridPane gridPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
		case DEBIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String typeOp = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
		case CREDIT:
			// règles de validation d'un crédit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence
			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			}
			catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String typeOp2 = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp2);
			this.primaryStage.close();
			break;

		case VIREMENT:
			// règles de validation d'un virement :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le virement ne doit pas amener le compte en dessous de son découvert autorisé,
			// - le compte source ne doit pas être le même que le compte cible
			montant = 0;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			this.lblCompte.getStyleClass().remove("borderred");
			this.txtCompte.getStyleClass().remove("borderred");
			String infoVirement = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(infoVirement);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				infoVirement = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(infoVirement);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String numCompteStr = this.txtCompte.getText().trim();
			int numCompte = 0;
			try {
				numCompte = Integer.parseInt(numCompteStr);
			} catch (NumberFormatException nfe) {
				infoVirement = "Numéro de compte invalide ! - Cpt. : " + this.compteEdite.idNumCompte + "  ";
				this.lblMessage.setText(infoVirement);
				this.txtCompte.getStyleClass().add("borderred");
				this.lblCompte.getStyleClass().add("borderred");
				this.txtCompte.requestFocus();
				return;
			}
			AccessCompteCourant acc = new AccessCompteCourant();
			AccessClient accl = new AccessClient();
			if (numCompte == this.compteEdite.idNumCompte) {
				infoVirement = "Le compte cible ne peut pas être le même que le compte source !";
				this.lblMessage.setText(infoVirement);
				this.txtCompte.getStyleClass().add("borderred");
				this.lblCompte.getStyleClass().add("borderred");
				this.txtCompte.requestFocus();
				return;
			}
			try {
				compteCible = acc.getCompteCourant(numCompte);
				if(compteCible == null || accl.getClient(compteCible.idNumCli).idAg != accl.getClient(this.compteEdite.idNumCli).idAg) {
					infoVirement = "Le compte cible n'existe pas ou n'appartient pas à votre agence !";
					this.lblMessage.setText(infoVirement);
					this.txtCompte.getStyleClass().add("borderred");
					this.lblCompte.getStyleClass().add("borderred");
					this.txtCompte.requestFocus();
					return;
				}
			} catch (RowNotFoundOrTooManyRowsException e) {
				e.printStackTrace();
			} catch (DatabaseConnexionException e) {
				e.printStackTrace();
			} catch (DataAccessException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			String typeOpV = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOpV);
			this.primaryStage.close();
			break;
		}
	}

	public CompteCourant getCompteCible() {
		return compteCible;
	}
}
