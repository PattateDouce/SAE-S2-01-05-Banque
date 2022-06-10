package model.orm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.exception.*;

public class AccessCompteCourant {

	/**
	 * Insertion d'un compte
	 *
	 * @param compte IN/OUT Tous les attributs IN sauf idNumCli en OUT
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void insertCompte(CompteCourant compte)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException, SoldeNotNullException {

		if (compte.solde != 0) {
			throw new SoldeNotNullException();
		}
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

	/**
	 * Génération d'un relevé mensuel d'un compte en pdf
	 *
	 * @param cpt IN le compte concerné
	 *
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public void generatePDF(CompteCourant cpt) {
		try {
			LocalDateTime date = LocalDateTime.now();

			Document document = new Document();
			PdfWriter.getInstance(document, Files.newOutputStream(Paths.get("CompteCourant.pdf")));
			document.open();

			AccessClient ac = new AccessClient();
			Client cli = ac.getClient(cpt.idNumCli);

			document.addTitle("Relevé mensuel du compte " + cpt.idNumCompte);

			Paragraph titre = new Paragraph("Relevé du mois de " + getMonth(date.getMonthValue()-1) + " " + date.getYear() + "\n\n", FontFactory.getFont(FontFactory.defaultEncoding, 22, Font.BOLD));
			titre.setAlignment(Element.ALIGN_CENTER);
			document.add(titre);

			Paragraph client = new Paragraph("Propriétaire : " + cli.nom.toUpperCase() + " " + cli.prenom, FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD));
			document.add(client);

			Paragraph compte = new Paragraph("N° de compte : " + cpt.idNumCompte, FontFactory.getFont(FontFactory.defaultEncoding, 16, Font.BOLD));
			document.add(compte);

			document.add(new Paragraph("Solde actuel : " + cpt.solde + " €"));
			document.add(new Paragraph("Débit autorisé : " + cpt.debitAutorise + " €"));

			document.add(new Paragraph("\n\n"));

			AccessOperation ao = new AccessOperation();
			float[] pointColumnWidths = {200F, 600F, 200F, 200F};
			PdfPTable table = new PdfPTable(pointColumnWidths);
			table.setWidthPercentage(90);
			table.setHeaderRows(1);

			PdfPCell cellDate = new PdfPCell(
					new Phrase("Date", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellDate.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellDate.setBorder(Rectangle.BOTTOM);
			cellDate.setFixedHeight(30);
			cellDate.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cellDate.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cellOperation = new PdfPCell(
					new Phrase("Opération", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellOperation.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellOperation.setBorder(Rectangle.BOTTOM);
			cellOperation.setFixedHeight(30);
			cellOperation.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cellOperation.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cellDebit = new PdfPCell(
					new Phrase("Débit", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellDebit.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellDebit.setBorder(Rectangle.BOTTOM);
			cellDebit.setFixedHeight(30);
			cellDebit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cellDebit.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cellCredit = new PdfPCell(
					new Phrase("Crédit", FontFactory.getFont(FontFactory.defaultEncoding, 12, Font.BOLD))
			);
			cellCredit.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellCredit.setBorder(Rectangle.BOTTOM);
			cellCredit.setFixedHeight(30);
			cellCredit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cellCredit.setVerticalAlignment(Element.ALIGN_MIDDLE);

			table.addCell(cellDate);
			table.addCell(cellOperation);
			table.addCell(cellDebit);
			table.addCell(cellCredit);

			PdfPCell cellVide = new PdfPCell(new Phrase(""));
			cellVide.setBorder(Rectangle.NO_BORDER);
			cellVide.setFixedHeight(20);

			BaseColor light_light_gray = new BaseColor(224, 224, 224);
			BaseColor color = BaseColor.WHITE;

			for (Operation op : ao.getOperations(cpt.idNumCompte)) {
				if (date.getMonthValue()-1 == op.dateOp.getMonth()) {
					PdfPCell cellD = new PdfPCell(new Phrase(op.dateOp.toString()));
					cellD.setBorder(Rectangle.NO_BORDER);
					cellD.setBackgroundColor(color);
					cellD.setFixedHeight(20);

					PdfPCell cellT = new PdfPCell(new Phrase(op.idTypeOp));
					cellT.setBorder(Rectangle.NO_BORDER);
					cellT.setBackgroundColor(color);
					cellT.setFixedHeight(20);

					PdfPCell cellM = new PdfPCell(new Phrase(op.montant + " €"));
					cellM.setBorder(Rectangle.NO_BORDER);
					cellM.setBackgroundColor(color);
					cellM.setFixedHeight(20);
					cellM.setHorizontalAlignment(Element.ALIGN_CENTER);

					cellVide.setBackgroundColor(color);

					table.addCell(cellD);
					table.addCell(cellT);
					if(op.montant < 0) {
						table.addCell(cellM);
						table.addCell(cellVide);
					} else {
						table.addCell(cellVide);
						table.addCell(cellM);
					}
					if(color == light_light_gray) color = BaseColor.WHITE;
					else color = light_light_gray;
				}
			}
			cellVide.setBorder(Rectangle.TOP);
			cellVide.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cellVide);
			table.addCell(cellVide);

			document.add(table);

			document.close();

			Process p = Runtime.getRuntime().exec("cmd /c start CompteCourant.pdf");
			p.waitFor();
			p.destroy();

		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		} catch (RowNotFoundOrTooManyRowsException e) {
			throw new RuntimeException(e);
		} catch (DatabaseConnexionException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private String getMonth(int month) {
		switch (month) {
			case 0:
				return "Janvier";
			case 1:
				return "Février";
			case 2:
				return "Mars";
			case 3:
				return "Avril";
			case 4:
				return "Mai";
			case 5:
				return "Juin";
			case 6:
				return "Juillet";
			case 7:
				return "Août";
			case 8:
				return "Septembre";
			case 9:
				return "Octobre";
			case 10:
				return "Novembre";
			case 11:
				return "Décembre";
			default:
				return "";
		}
	}

	public ArrayList<Prelevement> getPrelevs(int idNumCpt) throws DataAccessException {
		ArrayList<Prelevement> alResult = new ArrayList<>();
		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PrelevementAutomatique where idNumCompte = ? ORDER BY dateRecurrente";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCpt);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelev = rs.getInt("idPrelev");
				double montant = rs.getDouble("montant");
				int dateRecurrente = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompte = rs.getInt("idNumCompte");

				alResult.add(new Prelevement(idPrelev, montant, dateRecurrente, beneficiaire, idNumCompte));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		} catch (DatabaseConnexionException e) {
			throw new RuntimeException(e);
		}
		return alResult;
	}

	public Prelevement getPrelev(int idPrelev)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Prelevement prelev;

			Connection con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM PrelevementAutomatique where" + " idPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idPrelev);

			System.err.println(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				double montant = rs.getDouble("montant");
				int dateRecurrente = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompte = rs.getInt("idNumCompte");

				prelev = new Prelevement(idPrelev, montant, dateRecurrente, beneficiaire, idNumCompte);
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
			return prelev;
		} catch (SQLException e) {
			throw new DataAccessException(Table.CompteCourant, Order.SELECT, "Erreur accès", e);
		}
	}

	public void updatePrelevement(Prelevement prelev) throws DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PrelevementAutomatique SET MONTANT=?, DATERECURRENTE=?, BENEFICIAIRE=? WHERE IDPRELEV = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelev.getMontant());
			pst.setInt(2, prelev.getDatePrelevement());
			pst.setString(3, prelev.getBeneficiaire());
			pst.setInt(4, prelev.getIdPrelevement());

			System.out.println(prelev);
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
		} catch (RowNotFoundOrTooManyRowsException e) {
			throw new RuntimeException(e);
		} catch (DatabaseConnexionException e) {
			throw new RuntimeException(e);
		}
	}

	public void addPrelevement(Prelevement prelev) throws DataAccessException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (SEQ_ID_PRELEVAUTO.NEXTVAL, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelev.getMontant());
			pst.setInt(2, prelev.getDatePrelevement());
			pst.setString(3, prelev.getBeneficiaire());
			pst.setInt(4, prelev.getIdNumCompte());

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Client, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT SEQ_ID_PRELEVAUTO.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numPrelev = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			prelev.setIdPrelevement(numPrelev);
		} catch (SQLException | RowNotFoundOrTooManyRowsException e) {
			throw new DataAccessException(Table.Client, Order.INSERT, "Erreur accès", e);
		} catch (DatabaseConnexionException e) {
			throw new RuntimeException(e);
		}
	}
}
