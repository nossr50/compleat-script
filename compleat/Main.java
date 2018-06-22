package compleat;

import compleat.gui.ScriptGUI;

public class Main {
	//Global variables for use with GUI
	public static ScriptGUI sGUI;
	
	final public static String impDir = "import";
	final public static String expDir = "export";
	final public static String verNum = "v0.05";

	public static void main(String[] args) {
		//GUI init
		sGUI = new ScriptGUI();
		
		System.out.println("Script complete, exiting now!");
	}
}
