package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;
import model.data.Prelevement;

/**
 * The type Daily bank state.
 */
public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	 * Gets employe actif.
	 *
	 * @return the employe actif
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}

	/**
	 * Sets employe actif.
	 *
	 * @param employeActif the employe actif
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}

	/**
	 * Gets agence active.
	 *
	 * @return the agence active
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}

	/**
	 * Sets agence active.
	 *
	 * @param agenceActive the agence active
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}

	/**
	 * Is chef d'agence boolean.
	 *
	 * @return the boolean
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	 * Sets chef d'agence.
	 *
	 * @param isChefDAgence the is chef d agence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}

	/**
	 * Sets chef d'agence.
	 *
	 * @param droitsAccess the droits access
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
