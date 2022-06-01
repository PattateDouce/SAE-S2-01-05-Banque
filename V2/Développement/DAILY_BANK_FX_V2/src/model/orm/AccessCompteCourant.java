package model.orm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.exception.*;

public class AccessCompteCourant {

	public AccessCompteCourant() {
	}


	/**
	 * Insertion d'un compte
	 *
	 * @param compte IN/OUT Tous les attributs IN sauf idNumCli en OUT
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void insertCompte(CompteCourant compte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO COMPTECOURANT VALUES (" + "seq_id_client.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, compte.debitAutorise);
			pst.setDouble(2, compte.solde);
			pst.setInt(3, compte.idNumCli);
			pst.setString(4, compte.estCloture);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_client.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numCliBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			compte.idNumCli = numCliBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.INSERT, "Erreur accès", e);
		}
	}


	/**
	 * Recherche des CompteCourant d'un client à partir de son id.
	 *
	 * @param idNumCli id du client dont on cherche les comptes
	 * @return Tous les CompteCourant de idNumCli (ou liste vide)
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<CompteCourant> getCompteCourants(int idNumCli)
			throws DataAccessException, DatabaseConnexionException {
		ArrayList<CompteCourant> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM CompteCourant where idNumCli = ?";
			query += " ORDER BY idNumCompte";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCli);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idNumCompte = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				alResult.add(new CompteCourant(idNumCompte, debitAutorise, solde, estCloture, idNumCliTROUVE));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}


	/**
	 * Recherche d'un CompteCourant à partir de son id (idNumCompte).
	 *
	 * @param idNumCompte id du compte (clé primaire)
	 * @return Le compte ou null si non trouvé
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public CompteCourant getCompteCourant(int idNumCompte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			CompteCourant cc;

			Connection con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM CompteCourant where" + " idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			System.err.println(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idNumCompteTROUVE = rs.getInt("idNumCompte");
				int debitAutorise = rs.getInt("debitAutorise");
				double solde = rs.getDouble("solde");
				String estCloture = rs.getString("estCloture");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				cc = new CompteCourant(idNumCompteTROUVE, debitAutorise, solde, estCloture, idNumCliTROUVE);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return cc;
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Mise à jour d'un CompteCourant.
	 * <p>
	 * cc.idNumCompte (clé primaire) doit exister seul cc.debitAutorise est mis à
	 * jour cc.solde non mis à jour (ne peut se faire que par une opération)
	 * cc.idNumCli non mis à jour (un cc ne change pas de client)
	 *
	 * @param cc IN cc.idNumCompte (clé primaire) doit exister seul
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation
	 */
	public void updateCompteCourant(CompteCourant cc) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException, ManagementRuleViolation {
		try {

			CompteCourant cAvant = this.getCompteCourant(cc.idNumCompte);
			if (cc.debitAutorise > 0) {
				cc.debitAutorise = -cc.debitAutorise;
			}
			if (cAvant.solde < cc.debitAutorise) {
				throw new ManagementRuleViolation(Table.CompteCourant, Order.UPDATE,
						"Erreur de règle de gestion : sole à découvert", null);
			}
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET " + "debitAutorise = ? " + "WHERE idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, cc.debitAutorise);
			pst.setInt(2, cc.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.UPDATE, "Erreur accès", e);
		}
	}


	/**
	 * Insertion d'un compte
	 *
	 * @param compte IN/OUT Tous les attributs IN sauf idNumCli en OUT
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void supprimerCompte(CompteCourant compte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE CompteCourant SET " + "solde = 0 " + ", " + " estCloture = 'O' " + "WHERE idNumCompte = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, compte.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}


			con.commit();

		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.UPDATE, "Erreur accès", e);
		}
	}

	public void generatePDF(CompteCourant cpt) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream("CompteCourant.pdf"));
			document.open();
			document.add(new Paragraph("Compte Courant"));
			document.add(new Paragraph("Numero de compte : " + cpt.idNumCompte));
			document.add(new Paragraph("Solde : " + cpt.solde));
			document.add(new Paragraph("Debit autorisé : " + cpt.debitAutorise));
			document.add(new Paragraph("Client : " + cpt.idNumCli));
			AccessOperation ao = new AccessOperation();
			float[] pointColumnWidths = {150F, 200F, 100F};
			PdfPTable table = new PdfPTable(pointColumnWidths);
			table.setHeaderRows(1);
			PdfPCell cellDate = new PdfPCell(
					new Phrase("Date", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellDate.setHorizontalAlignment(Element.ALIGN_CENTER);
			PdfPCell cellOperation = new PdfPCell(
					new Phrase("Opération", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellOperation.setHorizontalAlignment(Element.ALIGN_CENTER);
			PdfPCell cellMontant = new PdfPCell(
					new Phrase("Montant", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellMontant.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cellDate);
			table.addCell(cellOperation);
			table.addCell(cellMontant);
			Date date = Date.from(Instant.now());
			for (Operation op : ao.getOperations(cpt.idNumCompte)) {
				if (date.getMonth() == op.dateOp.getMonth()) {
					PdfPCell cellD = new PdfPCell(new Phrase(op.dateOp.toString()));
					PdfPCell cellT = new PdfPCell(new Phrase(op.idTypeOp));
					PdfPCell cellM = new PdfPCell(new Phrase(Double.toString(op.montant)));
					table.addCell(cellD);
					table.addCell(cellT);
					table.addCell(cellM);
				}
			}
			document.add(table);
			document.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
