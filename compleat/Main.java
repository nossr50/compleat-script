package compleat;

import compleat.Converter;

public class Main {
	
	public static String impDir = "import";
	public static String expDir = "export";
	public static String verNum = "v0.04";
	
	//Global variables for use with GUI
	public static String curLog = "Idle";

	public static void main(String[] args) {
		
		//Prep files
		Converter.Init(impDir, expDir);
		
		//GUI init
		//ScriptGUI sGUI = new ScriptGUI();
		
		Converter.processDeckFiles(impDir, expDir);
		
		System.out.println("Script complete, exiting now!");
	}
}
