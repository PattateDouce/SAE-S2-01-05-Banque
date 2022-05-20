package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * The type Clients management.
 */
public class EmployesManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private EmployesManagementController emc;

    /**
     * Instantiates a new Clients management.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public EmployesManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployesManagementController.class.getResource("employesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employés");
			this.primaryStage.setResizable(false);

			this.emc = loader.getController();
			this.emc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Do client management dialog.
     */
    public void doEmployeManagementDialog() {
		this.emc.displayDialog();
	}

    /**
     * Modifier un employé.
     *
     * @param e the employe
     * @return the employe
     */
    public Employe modifierEmploye(Employe e) {
		EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = cep.doEmployeEditorDialog(e, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.updateEmploye(result);
			} catch (DatabaseConnexionException dce) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, dce);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

    /**
     * Nouvel employé.
     *
     * @return the employe
     */
    public Employe nouvelEmploye() {
    	Employe employe;
		EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dbs);
		employe = cep.doEmployeEditorDialog(null, EditionMode.CREATION);
		if (employe != null) {
			try {
				AccessEmploye ae = new AccessEmploye();
				ae.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}

    /** Supprime un compte d'employé
     * @param idEmp		ID de l'employé
     * @return true si cela fonction, false sinon
     */
    public boolean supprimerEmploye(int idEmp) {
    	try {
			AccessEmploye ae = new AccessEmploye();
			ae.supprimerCompte(idEmp);
			return true;

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();

		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
		}

    	return false;
    }
    
    /**
     * Gets comptes from num compte.
     *
     * @param _idEmp   the num compte
     * @param _debutNom    the debut nom
     * @param _debutPrenom the debut prenom
     * @return the comptes
     */
    public ArrayList<Employe> getlisteEmploye(int _idEmp, String _debutNom, String _debutPrenom) {
		ArrayList<Employe> listeEmp = new ArrayList<>();
		try {
			// Recherche des employés en BD. cf. AccessEmploye > getEmployes(.)
			// idEmp != -1 => recherche sur idEmp
			// idEmp != -1 et debutNom non vide => recherche nom/prenom
			// idEmp != -1 et debutNom vide => recherche tous les employés

			AccessEmploye ae = new AccessEmploye();
			listeEmp = ae.getEmployes(this.dbs.getEmpAct().idAg, _idEmp, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeEmp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeEmp = new ArrayList<>();
		}
		return listeEmp;
	}

}
