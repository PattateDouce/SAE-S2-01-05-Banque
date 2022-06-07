package model.data;

public class Periode {
        public int id;
        public double restantEnDebut;
        public double montantInterets;
        public double montantPrincipal;
        public double montantARembourser;
        public double restantFin;




        public String toString() {//overriding the toString() method
            return "Id"+id +" "+"restantEnDebut: "+restantEnDebut+" "+"montantInterets: "+montantInterets+" "+"montantPrincipal: "+montantPrincipal+" "+"montantARembourser: "+montantARembourser+" "+ "restantFin: "+restantFin;
        }


}

