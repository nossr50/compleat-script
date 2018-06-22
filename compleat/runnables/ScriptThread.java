package compleat.runnables;

import compleat.Main;
import compleat.gui.ScriptGUI;
import compleat.scripts.DeckScript;

public class ScriptThread implements Runnable {
	
	private ScriptGUI sGUI;

	public ScriptThread(ScriptGUI scriptGUI) {
		sGUI = scriptGUI;
	}

	@Override
	public void run() {
		System.out.println("Starting threaded process...");
		DeckScript.processDeckFiles(Main.impDir, Main.expDir, sGUI);
		System.out.println("Finished threaded process!");
	}

}
