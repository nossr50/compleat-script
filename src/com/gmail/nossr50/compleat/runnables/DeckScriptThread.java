package com.gmail.nossr50.compleat.runnables;

import com.gmail.nossr50.compleat.main.CompleatTool;
import com.gmail.nossr50.compleat.datatypes.enums.ScriptState;
import com.gmail.nossr50.compleat.gui.ScriptGUI;
import com.gmail.nossr50.compleat.scripts.DeckScript;

/**
 * This class is to run the IO work of our DeckScript in its own thread so that the GUI doesn't hang or lag
 * @author nossr50
 * @see DeckScript#writeFile(com.gmail.nossr50.compleat.datatypes.Deck, String, String, ScriptGUI, boolean)
 */
public class DeckScriptThread implements Runnable {
	
	private ScriptGUI sGUI;
	private boolean force = false;

	/**
	 * Constructor for our thread
	 * @param scriptGUI a reference to our GUI for later use
	 * @param isForced whether or not we are forcing conversion of files
	 */
	public DeckScriptThread(ScriptGUI scriptGUI, boolean isForced) {
		sGUI = scriptGUI;
		force = isForced;
	}

	/**
	 * Executes the main workhorse of our DeckScript and updates GUI widgets as needed, also updates the state of the script
	 */
	@Override
	public void run() {
		System.out.println("Executing Threaded process: Deck Script");
		sGUI.setScriptState(ScriptState.RUNNING);
		DeckScript.processDeckFiles(CompleatTool.impDir, CompleatTool.expDir, sGUI, force);
		System.out.println("Finished Threaded process: Deck Script");
		
		sGUI.setScriptState(ScriptState.FINISHED);
		sGUI.asyncWidgetReset();
	}
}