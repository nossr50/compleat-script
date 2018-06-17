package compleat;

import javax.swing.JOptionPane;

import compleat.Converter;

public class Main {
	
	public static String impDir = "import";
	public static String expDir = "export";

	public static void main(String[] args) {
		Converter.Convert(impDir, expDir);
		System.out.println("Done!");
		JOptionPane.showMessageDialog(null, "Script executed successfully");
		//Converter.TestCardApi();
	}

}
