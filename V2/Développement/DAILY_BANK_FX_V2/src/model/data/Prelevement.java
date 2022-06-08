package model.data;

import java.util.Date;

public class Prelevement {

    private int idPrelevement;

    private double montant;
    private int dateRecurrente;
    private String beneficiaire;
    private int idNumCompte;

    public Prelevement(int idPrelevement, double montant, int dateRecurrente, String beneficiaire, int idNumCompte) {
        this.idPrelevement = idPrelevement;
        this.montant = montant;
        this.dateRecurrente = dateRecurrente;
        this.beneficiaire = beneficiaire;
        this.idNumCompte = idNumCompte;
    }

    public Prelevement(Prelevement p) {
        this(p.idPrelevement, p.montant, p.dateRecurrente, p.beneficiaire, p.idNumCompte);
    }

    public String toString() {
        return "id = " + idPrelevement + "  -  " + "montant = " + montant + "  -  " + "dateRecurrente = " + dateRecurrente + "  -  "
                + "beneficiaire = " + beneficiaire + "  -  " + "idNumCompte = " + idNumCompte;
    }

    public int getIdPrelevement() {
        return idPrelevement;
    }

    public void setIdPrelevement(int idPrelevement) {
        this.idPrelevement = idPrelevement;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public int getDatePrelevement() {
        return dateRecurrente;
    }

    public void setDatePrelevement(int datePrelevement) {
        this.dateRecurrente = datePrelevement;
    }

    public String getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(String beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    public int getIdNumCompte() {
        return idNumCompte;
    }

    public void setIdNumCompte(int idNumCompte) {
        this.idNumCompte = idNumCompte;
    }
}
