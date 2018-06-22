package compleat.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import compleat.Main;
import compleat.Manager;
import compleat.datatypes.Deck;
import compleat.gui.elements.StatusGroup;
import compleat.runnables.ScriptThread;
import compleat.scripts.DeckScript;

public class ScriptGUI {
	
	//Local vars
	Label statusLabel = null;
	Button startButton;
	Button refreshButton;
	
	int mainWindowWidth 	= 1024/2;
	int mainWindowHeight 	= 768/2 + 100;
	
	private String gui_log 	= "";
	private Composite composite;
	private StatusGroup deckStatusGroup;
	
	String appTitle = "The Compleat Tool - "+Main.verNum;
	
	Button startScriptButton;
	
	//Listeners
	Listener listener_start;
	
	
		public ScriptGUI()
		{
			Init();
		}

		public void Init()
		{
			/*
			 * 	Create a Display which represents an SWT session.
				Create one or more Shells which serve as the main window(s) for the application.
				Create any other widgets that are needed inside the shell.
				Initialize the sizes and other necessary state for the widgets. Register listeners for widget events that need to be handled.
				Open the shell window.
				Run the event dispatching loop until an exit condition occurs, which is typically when the main shell window is closed by the user.
				Dispose the display.
			 */
			
			//Prep files
			DeckScript.Init();
			
			Display display = createDisplay();
			Shell shell = createShell(display);
			
			
			shell.setSize(mainWindowWidth, mainWindowHeight);
			shell.setText(appTitle);
			shell.update();
			
			initLayout(shell);
		     
		     while (!shell.isDisposed ()) {
		        if (!display.readAndDispatch ()) display.sleep ();
		     }
		     
		     display.dispose ();
		}
		
		public Display createDisplay()
		{
			/*
			 * The Display represents the connection between SWT and the underlying platform's GUI system. Displays are primarily used to manage the platform event loop and control communication between the UI thread and other threads. (See Threading issues for clients for a complete discussion of UI threading issues.)

				For most applications you can follow the pattern that is used above. You must create a display before creating any windows, and you must dispose of the display when your shell is closed. You don't need to think about the display much more unless you are designing a multi-threaded application.
			 */
			return new Display();
		}
		
		public Shell createShell(Display display)
		{
			
			/*
			 * A Shell is a "window" managed by the OS platform window manager. Top level shells are those that are created as a child of the display. These windows are the windows that users move, resize, minimize, and maximize while using the application. Secondary shells are those that are created as a child of another shell. These windows are typically used as dialog windows or other transient windows that only exist in the context of another window.
			 */
			return new Shell (display);
		}
		
		public void addWidgets(Shell shell)
		{
			//Info text
			//addTextLabels(shell);
			
			//Status bar
			
		}
		
		public void addTextLabels(Shell shell)
		{
			//This ones static so we don't need to keep a reference to it
			Label label = new Label (shell, SWT.LEFT);
		    label.setText ("Compleat Script\nAuthor: nossr50\nVersion number : " + Main.verNum);
		    label.setBounds (shell.getClientArea ());
		    
		    //Status bar
		    addStatusBar(shell);
		}
		
		public void addStatusBar(Shell shell)
		{
			//init
			//statusLabel = new Label (shell, SWT.LEFT);
			//statusLabel.setLocation(shell.getBounds().height / 2, shell.getBounds().width);
			
			//set default status
			//statusLabel.setText ("Current Task: " + Main.curLog);
			//statusLabel.setBounds (shell.getClientArea ());
		}
		
		public void initLayout(Shell shell)
		{
			GridLayout gl = new GridLayout();
			gl.numColumns = 2; //Always set first
			shell.setLayout(gl);
			
			initWidgets(shell);
			
			//shell.pack();
			shell.open();
		}
		
		public void initWidgets(Shell shell)
		{
			initGroup(shell);
			initButtonWidgets(shell);
		}
		
		public void initGroup(Shell shell)
		{
			//Make the parent object
			composite 				= new Composite(shell, SWT.NONE);
			
			//Set the layout for the parent object
			composite.setLayout(new GridLayout(1, false));
	        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        
	        //Make our deck group object
			deckStatusGroup 		= new StatusGroup(composite);
			
			//Have it use the grid layout as well
			deckStatusGroup.setLayout(new GridLayout(1, false));
	        deckStatusGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        
	        for(Deck curDeck : Manager.getDecks())
			{
				DeckScript.checkIfCompleat(curDeck, Main.impDir, Main.expDir, this);
			}
		}
		
		public void initButtonWidgets(Shell shell)
		{
			startButton 	= new Button(deckStatusGroup, SWT.PUSH);
			//refreshButton 	= new Button(deckStatusGroup, SWT.PUSH);
			
	        //startButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	        //refreshButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			startButton.setText("Start");
			//refreshButton.setText("Refresh");
			
			startButton.update();
			//refreshButton.update();
			
			if(Manager.areDecksCompleat())
			{
				startButton.setEnabled(false);
				deckStatusGroup.setLog("Decks are already compleat!");
			} else {
				deckStatusGroup.setLog("Ready!");
			}
			
			listener_start = new Listener() {
			      public void handleEvent(Event e) {
				        switch (e.type) {
				        case SWT.Selection:
				        {
				          startButtonPressed();
				          break;
				        }
				        }
				      }
				    };
			
			startButton.addListener(SWT.Selection, listener_start);
		}
		
		/***
		 * Updates the text based widgets asynchronously
		 * @param deck
		 */
		public void asyncUpdateTextWidgets(Deck deck)
		{
			Display.getDefault().asyncExec(new Runnable() {
		        public void run() {
		        	//System.out.println("Updating deck GUI --");
					deckStatusGroup.updateDeckProgressLabels(deck);
		        }
		    });
		}
		
		public void startButtonPressed()
		{
			startButton.setEnabled(false);
			startButton.update();
			startButton.removeListener(SWT.Selection, listener_start);
			
			ScriptThread st = new ScriptThread(this);
			Thread thread = new Thread(st);
			
			thread.start();
		}
}
