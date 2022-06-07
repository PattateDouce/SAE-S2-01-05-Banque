package application;

import application.control.DailyBankMainFrame;
import application.tools.LoanSimulatorTool;

/**
 * The type Daily bank app.
 * Used as a launcher.
 */
public class DailyBankApp  {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		LoanSimulatorTool.runTests();
		DailyBankMainFrame.runApp();

	}
}
