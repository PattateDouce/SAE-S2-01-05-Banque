package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.orm.AccessClient;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * The type Clients management.
 */
public class ClientsManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private ClientsManagementController cmc;

    /**
     * Instantiates a new Clients management.
     *
     * @param _parentStage the parent stage
     * @param _dbstate     the dbstate
     */
    public ClientsManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clientsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des clients");
			this.primaryStage.setResizable(false);

			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Do client management dialog.
     */
    public void doClientManagementDialog() {
		this.cmc.displayDialog();
	}

    /**
     * Modifier client.
     *
     * @param c the client
     * @return the client
     */
    public Client modifierClient(Client c) {
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dbs);
		Client result = cep.doClientEditorDialog(c, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessClient ac = new AccessClient();
				ac.updateClient(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
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
     * Nouveau client.
     *
     * @return the client
     */
    public Client nouveauClient() {
		Client client;
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dbs);
		client = cep.doClientEditorDialog(null, EditionMode.CREATION);
		if (client != null) {
			try {
				AccessClient ac = new AccessClient();

				ac.insertClient(client);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				client = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				client = null;
			}
		}
		return client;
	}

    /**
     * Gerer comptes du client.
     *
     * @param c the client
     */
    public void gererComptesClient(Client c) {
		ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dbs, c);
		cm.doComptesManagementDialog();
	}

    /**
     * Gets comptes from num compte.
     *
     * @param _numCompte   the num compte
     * @param _debutNom    the debut nom
     * @param _debutPrenom the debut prenom
     * @return the comptes
     */
    public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Client> listeCli = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte != -1 et debutNom non vide => recherche nom/prenom
			// numCompte != -1 et debutNom vide => recherche tous les clients

			AccessClient ac = new AccessClient();
			listeCli = ac.getClients(this.dbs.getEmpAct().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCli = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCli = new ArrayList<>();
		}
		return listeCli;
	}
}
