package model.orm;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import model.data.Operation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AccessOperation {

	public AccessOperation() {
	}

	/**
	 * Recherche de toutes les opérations d'un compte.
	 *
	 * @param idNumCompte id du compte dont on cherche toutes les opérations
	 * @return Toutes les opérations du compte, liste vide si pas d'opération
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<Operation> getOperations(int idNumCompte) throws DataAccessException, DatabaseConnexionException {
		ArrayList<Operation> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Operation where idNumCompte = ?";
			query += " ORDER BY dateOp";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idOperation = rs.getInt("idOperation");
				double montant = rs.getDouble("montant");
				Date dateOp = rs.getDate("dateOp");
				Date dateValeur = rs.getDate("dateValeur");
				int idNumCompteTrouve = rs.getInt("idNumCompte");
				String idTypeOp = rs.getString("idTypeOp");

				alResult.add(new Operation(idOperation, montant, dateOp, dateValeur, idNumCompteTrouve, idTypeOp));
			}
			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Recherche d'une opération par son id.
	 *
	 * @param idOperation id de l'opération recherchée (clé primaire)
	 * @return une Operation ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public Operation getOperation(int idOperation)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		Operation operationTrouvee;

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Operation  where" + " idOperation = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idOperation);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idOperationTrouve = rs.getInt("idOperation");
				double montant = rs.getDouble("montant");
				Date dateOp = rs.getDate("dateOp");
				Date dateValeur = rs.getDate("dateValeur");
				int idNumCompteTrouve = rs.getInt("idNumCompte");
				String idTypeOp = rs.getString("idTypeOp");

				operationTrouvee = new Operation(idOperationTrouve, montant, dateOp, dateValeur, idNumCompteTrouve,
						idTypeOp);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				rs.close();
				pst.close();
				throw new RowNotFoundOrTooManyRowsException(Table.Operation, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return operationTrouvee;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Enregistrement d'un débit.
	 *
	 * Se fait par procédure stockée : - Vérifie que le débitAutorisé n'est pas
	 * dépassé - Enregistre l'opération - Met à jour le solde du compte.
	 *
	 * @param idNumCompte compte débité
	 * @param montant     montant débité
	 * @param typeOp      libellé de l'opération effectuée (cf TypeOperation)
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation
	 */
	public void insertDebit(int idNumCompte, double montant, String typeOp)
			throws DatabaseConnexionException, ManagementRuleViolation, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call Debiter (?, ?, ?, ?)}";
			// les ? correspondent aux paramètres : cf. déf procédure (4 paramètres)
			call = con.prepareCall(q);
			// Paramètres in
			call.setInt(1, idNumCompte);
			// 1 -> valeur du premier paramètre, cf. déf procédure
			call.setDouble(2, montant);
			call.setString(3, typeOp);
			// Paramètres out
			call.registerOutParameter(4, java.sql.Types.INTEGER);
			// 4 type du quatrième paramètre qui est déclaré en OUT, cf. déf procédure

			call.execute();

			int res = call.getInt(4);

			if (res != 0) { // Erreur applicative
				throw new ManagementRuleViolation(Table.Operation, Order.INSERT,
						"Erreur de règle de gestion : découvert autorisé dépassé", null);
			}
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}

	/**
	 * Enregistrement d'un virement.
	 *
	 * Se fait par procédure stockée : - Vérifie que le montant n'est pas négatif -
	 * Enregistre l'opération - Met à jour le solde du compte.
	 *
	 * @param idNumCompteDebite compte débiteur
	 * @param idNumCompteCredite compte créditeur
	 * @param montant montant débité
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation
	 */
	public void insertVirement(int idNumCompteDebite, int idNumCompteCredite, double montant)
			throws DatabaseConnexionException, ManagementRuleViolation, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call Virer (?, ?, ?, ?)}";
			// les ? correspondent aux paramètres : cf. déf procédure (3 paramètres)
			call = con.prepareCall(q);
			// Paramètres in
			call.setInt(1, idNumCompteDebite);
			call.setInt(2, idNumCompteCredite);
			call.setDouble(3, montant);
			// Paramètres out
			call.registerOutParameter(4, java.sql.Types.INTEGER);
			// 4 type du quatrième paramètre qui est déclaré en OUT, cf. déf procédure

			call.execute();

			int res = call.getInt(4);

			if (res != 0) { // Erreur applicative
				throw new ManagementRuleViolation(Table.Operation, Order.INSERT,
						"Erreur de règle de gestion : découvert autorisé dépassé", null);
			}
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}

	public void executerPrelevAuto() throws DataAccessException {

		try {
			Connection con = LogToDatabase.getConnexion();
			CallableStatement call;

			String q = "{call ExecuterPrelevAuto(?)}";

			call = con.prepareCall(q);

			call.registerOutParameter(1, Types.VARCHAR);

			call.execute();

			String res = call.getString(1);

			if(res != null)
				System.out.println(res);

		}catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		} catch (DatabaseConnexionException e) {
			e.printStackTrace();
		}
	}

}
