package compleat.runnables;

import compleat.Main;
import compleat.ScriptIO;
import compleat.gui.ScriptGUI;

public class ScriptThread implements Runnable {
	
	private ScriptGUI sGUI;

	public ScriptThread(ScriptGUI scriptGUI) {
		sGUI = scriptGUI;
	}

	@Override
	public void run() {
		System.out.println("Starting threaded process...");
		ScriptIO.processDeckFiles(Main.impDir, Main.expDir, sGUI);
		System.out.println("Finished threaded process!");
	}

}
