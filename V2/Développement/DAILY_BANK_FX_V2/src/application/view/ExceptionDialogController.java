package application.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.orm.exception.ApplicationException;

/**
 * The type Exception dialog controller.
 */
public class ExceptionDialogController implements Initializable {

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ApplicationException ae;
	// Manipulation de la fenêtre

    /**
     * Init context.
     *
     * @param _primaryStage the primary stage
     * @param _ae           the ae
     */
    public void initContext(Stage _primaryStage, ApplicationException _ae) {
		this.primaryStage = _primaryStage;
		this.ae = _ae;
		this.configure();
	}

	/**
	 * Initialise les labels et les events et d'autres objets
	 */
	private void configure() {
		this.lblTitre.setText(this.ae.getMessage());
		this.txtTable.setText(this.ae.getTableName().toString());
		this.txtOpe.setText(this.ae.getOrder().toString());
		this.txtException.setText(this.ae.getClass().getName());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.ae.printStackTrace(pw);
		this.txtDetails.setText(sw.toString());
	}

    /**
     * Display dialog.
     */
    public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblTitre;
	@FXML
	private TextField txtTable;
	@FXML
	private TextField txtOpe;
	@FXML
	private TextField txtException;
	@FXML
	private TextArea txtDetails;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Ferme la fenêtre
	 */
	@FXML
	private void doOK() {
		this.primaryStage.close();
	}
}
