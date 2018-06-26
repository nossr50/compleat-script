package compleat;

import compleat.gui.ScriptGUI;

/**
 * The entry point for our program, initializing the GUI and the first steps of our application such as making sure the file directories exist and if they do exist checking them for files to be used later in our script.
 * 
 * <p> Contains Strings relevant to the operation of the script
 * 
 * <p> Compiling Dependencies: MTG-API Java SDK, Simple Widget Toolkit, Java SDK 1.8 or higher
 *
 * <p> Execution Dependencies: GNuPG (to send out and receive encrypted HTTPS data) -- NOTE: You must add this to your %PATH% on windows after installing
 * 
 * @author nossr50
 * 
 * @see <a href="https://magicthegathering.io/">MTG-API</a> 
 * @see <a href="https://mtgjson.com/">MTG JSON DB</a> 
 * @see <a href="https://www.eclipse.org/swt/">Simple Widget Toolkit</a> 
 * @see <a href="https://gnupg.org/">GNuPG</a> 
 */
public class Main {
    private static ScriptGUI   sGUI;              //The singleton instance of our GUI

    final public static String impDir = "import"; //Import directory name
    final public static String expDir = "export"; //Export directory name
    final public static String verNum = "v0.09b";  //Current version of the script

    /**
     * The main function of our Java application
     * @param args command line arguments, currently unused in our program
     */
    public static void main(String[] args) {
        //GUI init
        sGUI = new ScriptGUI();

        System.out.println("Script complete, exiting now!");
    }

    /**
     * Helper function to get the singleton representation of our GUI (ScriptGUI)
     * @return The singleton representation of our GUI
     */
    public static ScriptGUI getGUI() {
        return sGUI;
    }
}
