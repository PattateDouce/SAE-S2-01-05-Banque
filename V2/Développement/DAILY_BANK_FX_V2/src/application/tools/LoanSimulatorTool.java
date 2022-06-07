package application.tools;
import model.data.Emprunt;
import model.data.Periode;

import java.util.ArrayList;
import java.util.List;

public class LoanSimulatorTool {


    public static List<Periode> simulateLoan(Emprunt emprunt) {
        List<Periode> periods = new ArrayList<>();

        double tauxApplicable = (emprunt.tauxAnnuel /100) /12;
        int numPeriode = emprunt.duree * 12;
        double capitalRestant = emprunt.capital;

        for (int i = 0 ; i < numPeriode ; ++i) {
            Periode p = new Periode();
            p.id = i+1;
            p.restantEnDebut = capitalRestant;
            p.montantInterets = capitalRestant * tauxApplicable;
            p.montantARembourser = emprunt.capital * (tauxApplicable / (1-Math.pow(1+tauxApplicable,numPeriode * -1)));
            p.montantPrincipal = p.montantARembourser - p.montantInterets;
            p.restantFin = capitalRestant - p.montantPrincipal;

            capitalRestant = p.restantFin;

            if (p.id  == numPeriode) {
                p.restantFin = 0;
            }

            periods.add(p);
        }

        return periods;
    }


    public static void runTests() {
        Emprunt emprunt = new Emprunt();
        emprunt.capital = 40000;
        emprunt.duree = 10;
        emprunt.tauxAnnuel = 0.8;

        List<Periode> periodes = simulateLoan(emprunt);

        for (Periode p : periodes) {
            System.out.println(p.toString());
        }


    }}


