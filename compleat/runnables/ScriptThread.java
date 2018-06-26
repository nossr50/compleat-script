package compleat.runnables;

import compleat.Main;
import compleat.datatypes.enums.ScriptState;
import compleat.gui.ScriptGUI;
import compleat.scripts.DeckScript;

/**
 * This class is to run the IO work of our DeckScript in its own thread so that the GUI doesn't hang or lag
 * @author nossr50
 * @see DeckScript#writeFile(compleat.datatypes.Deck, String, String, ScriptGUI, boolean)
 */
public class ScriptThread implements Runnable {
	
	private ScriptGUI sGUI;
	private boolean force = false;

	/**
	 * Constructor for our thread
	 * @param scriptGUI a reference to our GUI for later use
	 * @param isForced whether or not we are forcing conversion of files
	 */
	public ScriptThread(ScriptGUI scriptGUI, boolean isForced) {
		sGUI = scriptGUI;
		force = isForced;
	}

	/**
	 * Executes the main workhorse of our DeckScript and updates GUI widgets as needed, also updates the state of the script
	 */
	@Override
	public void run() {
		System.out.println("Starting threaded process...");
		sGUI.setScriptState(ScriptState.RUNNING);
		DeckScript.processDeckFiles(Main.impDir, Main.expDir, sGUI, force);
		System.out.println("Finished threaded process!");
		
		sGUI.setScriptState(ScriptState.FINISHED);
		sGUI.asyncWidgetReset();
	}
}
