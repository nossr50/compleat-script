package compleat;

import javax.swing.JOptionPane;

import compleat.Converter;
import compleat.gui.ScriptGUI;

public class Main {
	
	public static String impDir = "import";
	public static String expDir = "export";
	public static String verNum = "v0.04";
	
	//Global variables for use with GUI
	public static String curLog = "Idle";

	public static void main(String[] args) {
		
		//GUI init
		//ScriptGUI sGUI = new ScriptGUI();
		
		Converter.Convert(impDir, expDir);
		System.out.println("Done!");
		JOptionPane.showMessageDialog(null, "Script executed successfully");
		//Converter.TestCardApi();

	}

}
