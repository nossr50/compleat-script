package compleat.runnables;

import compleat.scripts.GuideScript;

public class GuideScriptThread implements Runnable {
    /**
     * Executes the main workhorse of our DeckScript and updates GUI widgets as needed, also updates the state of the script
     */
    @Override
    public void run() {
        System.out.println("Executing Threaded process: Guide Script");
        GuideScript.downloadDatabaseStandard();
        System.out.println("Finished Threaded process: Guide Script");
    }
}
