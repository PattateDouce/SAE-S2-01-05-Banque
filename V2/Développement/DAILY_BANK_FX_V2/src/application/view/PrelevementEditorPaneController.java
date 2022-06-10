package application.view;

import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Prelevement;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Compte editor pane controller.
 */
public class PrelevementEditorPaneController implements Initializable {

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode em;
	private Prelevement prelevEdite;
	private Prelevement prelevResult;

    /**
     * Init context.
     *
     * @param _primaryStage the primary stage
     */
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage) {
		this.primaryStage = _primaryStage;
		this.configure();
	}

	/**
	 * Initialise les labels et les events et d'autres objets
	 */
	private void configure() {
		this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
				doAjouter();
			} } );
		this.primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
				doCancel();
			} } );
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		
		this.txtMontant.focusedProperty().addListener((t, o, n) -> this.focusMontant(t, o, n));
		this.txtDateRecurrente.focusedProperty().addListener((t, o, n) -> this.focusDate(t, o, n));
		this.txtBeneficiaire.focusedProperty().addListener((t, o, n) -> this.focusBeneficiaire(t, o, n));

	}

    /**
     * Display dialog compte courant.
     *
     * @param cpte   the cpte
     * @param mode   the mode
     * @return the compte courant
     */
    public Prelevement displayDialog(Prelevement prelev, CompteCourant cpte, EditionMode mode) {
		this.em = mode;
		if (prelev == null) {
			this.prelevEdite = new Prelevement(0, 0, 0, "benef", cpte.idNumCompte);
		} else {
			this.prelevEdite = new Prelevement(prelev);
		}
		this.prelevResult = null;
		this.txtIdPrelev.setDisable(true);
		this.txtIdNumCompte.setDisable(true);
		switch (mode) {
		case CREATION:
			this.lblMessage.setText("Informations sur le nouveau prélèvement");
			this.btnOk.setText("Ajouter");
			this.btnCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.lblMessage.setText("Modifications du prélèvement");
			this.btnOk.setText("Modifier");
			this.btnCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			this.txtMontant.setDisable(true);
			this.txtBeneficiaire.setDisable(true);
			this.txtDateRecurrente.setDisable(true);
			this.lblMessage.setText("Voulez-vous supprimer ce prélèvement ?");
			this.btnOk.setText("Ok");
			this.btnCancel.setText("Annuler");
			break;
		}

		// initialisation du contenu des champs
		this.txtIdPrelev.setText("" + this.prelevEdite.getIdPrelevement());
		this.txtIdNumCompte.setText("" + cpte.idNumCompte);
		this.txtMontant.setText("" + this.prelevEdite.getMontant());
		this.txtBeneficiaire.setText(this.prelevEdite.getBeneficiaire());
		this.txtDateRecurrente.setText("" + this.prelevEdite.getDatePrelevement());

		this.prelevResult = null;

		this.primaryStage.showAndWait();
		return this.prelevResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	private Object focusMontant(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if(oldPropertyValue) {
			try {
				int val;
				val = Integer.parseInt(this.txtMontant.getText().trim());
				if (val <= 0) {
					throw new NumberFormatException();
				}
				this.prelevEdite.setMontant(val);
			} catch (NumberFormatException nfe) {
				this.txtMontant.setText("" + this.prelevEdite.getMontant());
			}
		}
		return null;
	}

	private Object focusDate(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
								boolean newPropertyValue) {
		if(oldPropertyValue) {
			try {
				int val;
				val = Integer.parseInt(this.txtDateRecurrente.getText().trim());
				if (val <= 0 || val > 28) {
					throw new NumberFormatException();
				}
				this.prelevEdite.setDatePrelevement(val);
			} catch (NumberFormatException nfe) {
				this.txtDateRecurrente.setText("" + this.prelevEdite.getDatePrelevement());
			}
		}
		return null;
	}

	private Object focusBeneficiaire(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
								boolean newPropertyValue) {
		if(oldPropertyValue) {
			try {
				String val = this.txtBeneficiaire.getText().trim();
				if(this.txtBeneficiaire.getText().trim().isEmpty()) {
					throw new NumberFormatException();
				}
				this.prelevEdite.setBeneficiaire(val);
			} catch (NumberFormatException nfe) {
				this.txtBeneficiaire.setText("" + this.prelevEdite.getBeneficiaire());
			}
		}
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdPrelev;
	@FXML
	private TextField txtIdNumCompte;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtDateRecurrente;
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.prelevResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.prelevResult = this.prelevEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.prelevResult = this.prelevEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			if (prelevEdite != null) {
				this.prelevEdite.setBeneficiaire("Supprimé (old : date " + prelevEdite.getDatePrelevement() +
												" / montant " + prelevEdite.getMontant() + ")");
				this.prelevEdite.setMontant(0);
				this.prelevEdite.setDatePrelevement(0);
			}
			this.prelevResult = this.prelevEdite;
			this.primaryStage.close();
			break;
		}

	}

	private boolean isSaisieValide() {
		if(this.txtMontant.getText().trim().isEmpty() || Double.parseDouble(txtMontant.getText().trim()) <= 0) {
			this.txtMontant.setText("" + this.prelevEdite.getMontant());
			return false;
		}
		double date = Double.parseDouble(txtDateRecurrente.getText().trim());
		if(this.txtDateRecurrente.getText().trim().isEmpty() || date <= 0 || date > 28) {
			this.txtDateRecurrente.setText("" + this.prelevEdite.getDatePrelevement());
			return false;
		}
		if(this.txtBeneficiaire.getText().trim().isEmpty()) {
			this.txtBeneficiaire.setText("" + this.prelevEdite.getBeneficiaire());
			return false;
		}
		return true;
	}


}
