package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.view.DailyBankMainFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.data.AgenceBancaire;
import model.orm.AccessAgenceBancaire;
import model.orm.AccessOperation;
import model.orm.LogToDatabase;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

/**
 * The type Daily bank main frame.
 * Control the main window.
 */
public class DailyBankMainFrame extends Application {

	private DailyBankState dbs;
	private Stage primaryStage;

	/**
	 * Créer l'état de l'app, charge le FXML et son controleur
	 * @param primaryStage	stage given by JavaFX
	 */
	@Override
	public void start(Stage primaryStage) throws DataAccessException {

		AccessOperation ao = new AccessOperation();
		ao.executerPrelevAuto();
		this.primaryStage = primaryStage;

		try {
			this.dbs = new DailyBankState();
			this.dbs.setAgAct(null);
			this.dbs.setChefDAgence(false);
			this.dbs.setEmpAct(null);

			FXMLLoader loader = new FXMLLoader(
					DailyBankMainFrameController.class.getResource("dailybankmainframe.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("DailyBank");

			DailyBankMainFrameController dbmc = loader.getController();
			dbmc.initContext(primaryStage, this, this.dbs);

			dbmc.displayDialog();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

    /**
     * Run the app.
     */
    public static void runApp() {
		Application.launch();
	}

    /**
     * Réinitialise l'état de l'app, et ferme la connexion à la BD
     * Disconnect.
     */
    public void disconnect() {
		this.dbs.setAgAct(null);
		this.dbs.setEmpAct(null);
		this.dbs.setChefDAgence(false);
		try {
			LogToDatabase.closeConnexion();
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
		}
	}

    /**
     * Charge la fenêtre de login et si a sa fermeture l'employé actif n'est pas null
     * alors, on donne la BD à l'état de l'app
     * Login.
     */
    public void login() {
		LoginDialog ld = new LoginDialog(this.primaryStage, this.dbs);
		ld.doLoginDialog();

		if (this.dbs.getEmpAct() != null) {
			this.dbs.setChefDAgence(this.dbs.getEmpAct().droitsAccess);
			try {
				AccessAgenceBancaire aab = new AccessAgenceBancaire();
				AgenceBancaire agTrouvee;

				agTrouvee = aab.getAgenceBancaire(this.dbs.getEmpAct().idAg);
				this.dbs.setAgAct(agTrouvee);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.dbs.setAgAct(null);
				this.dbs.setEmpAct(null);
				this.dbs.setChefDAgence(false);
			} catch (ApplicationException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.dbs.setAgAct(null);
				this.dbs.setEmpAct(null);
				this.dbs.setChefDAgence(false);
			}
		}
	}

    /**
     * Gestion clients.
     */
    public void gestionClients() {
		ClientsManagement cm = new ClientsManagement(this.primaryStage, this.dbs);
		cm.doClientManagementDialog();
	}
    
    /**
     * Gestion des employés par un chef d'agence
     */
    public void gestionEmployes() {
    	EmployesManagement em = new EmployesManagement(this.primaryStage, this.dbs);
		em.doEmployeManagementDialog();
    }


	/**
	 * Permet de simuler un emprunt
	 */
	public void simulerEmprunt() {
		LoanSimulatorPane loanSimulatorPane = new LoanSimulatorPane(this.primaryStage,this.dbs);
		loanSimulatorPane.doLoadSimulation();
	}
	
	/**
	 * Permet de simuler une assurance d'emprunt
	 */
	public void simulerAssurance() {
		InsuranceSimulatorPane warantySimulatorPane = new InsuranceSimulatorPane(this.primaryStage,this.dbs);
		warantySimulatorPane.doSimulationDialog();
	}
}
