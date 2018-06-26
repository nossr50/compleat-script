package compleat.gui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import compleat.Main;
import compleat.Manager;
import compleat.datatypes.Deck;
import compleat.datatypes.enums.ScriptState;
import compleat.gui.tools.CardImageManager;
import compleat.runnables.ScriptThread;
import compleat.scripts.DeckScript;
import io.magicthegathering.javasdk.resource.Card;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * This class represents the GUI for our application, when initialized it handles construction of the GUI through SWT. 
 * And all functionality related to the GUI is contained within this class.
 * 
 * @author nossr50
 * 
 * @see <a href="https://www.eclipse.org/swt/">Simple Widget Toolkit</a>
 */
public class ScriptGUI {
	/*
	 * Main components of our GUI
	 */
	public ScriptState curState;
	private Display displayMain;
	private Shell shellMain;

	/*
	 * vars for elements of our GUI
	 */

	private String appTitle = "The Compleat Tool - "+Main.verNum;

	/*
	 * Composites
	 */

	private Composite deckScriptComposite; //Contains all widgets associated with DeckScript functionality
	private Composite cardArtCanvas; //Used for displaying Card Art on the GUI

	/*
	 * Widgets
	 */

	//Buttons
	private Button btnStart;
	private Button btnRefresh;
	private Button btnForce;

	//Group Widgets
	private Group grpDeckScript;
	private Group grpCardInfoPanel;

	/*
	 * Helper classes
	 */

	//Helper class for managing and displaying Card Art onto the cardArtCanvas
	public CardImageManager cardImageManager;


	/*
	 * Widget Listeners
	 */
	private Listener listener_start;
	private Listener listener_refresh;
	private Listener listener_force;


	/*
	 * Labels
	 */
	private Label labelCardName;
	private Label logLabel;

	/*
	 * Tables
	 */
	private Table table;
	HashMap<Deck, TableItem> deckTableMap = new HashMap<Deck, TableItem>(); //Contains the data for our Table


	/*
	 * Styled Text
	 */

	private StyledText logWidget;
	private String logWidgetContent = "Idle";

	/*
	 * Progress Bars
	 */
	private ProgressBar progressBar;

	/**
	 * Preps the script for use and then initializes the GUI
	 */
	public ScriptGUI()
	{
		/*
		 * Some of our GUI elements depend on this being executed at least once, 
		 * since the GUI is the first thing to get created when you open our application we call it ourselves here
		 */
		DeckScript.Init();

		//Initialize the GUI now that the application is partially loaded
		Init();
	}

	/**
	 * Create a Display which represents an SWT session.
	   <p> Create one or more Shells which serve as the main window(s) for the application.
	   <p> Create any other widgets that are needed inside the shell.
	   <p> Initialize the sizes and other necessary state for the widgets. Register listeners for widget events that need to be handled.
	   <p> Open the shell window.
	   <p> Run the event dispatching loop until an exit condition occurs, which is typically when the main shell window is closed by the user.
	 */
	private void Init()
	{
		
		//Main shell & display used in our application
		displayMain = new Display();
		shellMain = new Shell (displayMain);
		curState = ScriptState.IDLE;

		//Properties of the shell
		shellMain.setSize(841, 462);
		shellMain.setText(appTitle);
		shellMain.update();

		//Initializes the widgets and properties of those widgets
		initWidgets(shellMain);
		
		//This is the LAST thing we do before starting the loop
		shellMain.open();
		
		//Main loop running while the applications window is open
		while (!shellMain.isDisposed ()) {
			if (!displayMain.readAndDispatch ()) displayMain.sleep ();
		}

		//Dispose of the display
		displayMain.dispose ();
	}

	/**
	 * Initializes all widgets and properties of those widgets
	 * @param shell The shell we are attaching these widgets to
	 */
	private void initWidgets(Shell shell)
	{
		//Widgets related to the DeckScript
		initDeckScriptWidgets(shell); 
		initButtonWidgets(shell);
		
		/*
		 * Widgets related to displaying and rendering card art
		 */
		
		grpCardInfoPanel = new Group(shellMain, SWT.NONE);
		grpCardInfoPanel.setText("Card Info");
		grpCardInfoPanel.setBounds(474, 5, 341, 410);

		cardArtCanvas = new Composite(grpCardInfoPanel, SWT.NONE);
		cardArtCanvas.setLocation(59, 24);
		cardArtCanvas.setSize(223, 311);

		cardImageManager = new CardImageManager(cardArtCanvas, displayMain);

		labelCardName = new Label(grpCardInfoPanel, SWT.NONE);
		labelCardName.setBounds(10, 341, 213, 15);
		labelCardName.setText("Card: ");
	}

	/**
	 * Initializes all widgets associated with DeckScript and properties of those widgets
	 * @param shell The shell we are attaching these widgets to
	 */
	private void initDeckScriptWidgets(Shell shell)
	{
		shellMain.setLayout(null);
		//Make the parent object
		deckScriptComposite 				= new Composite(shell, SWT.NONE);
		deckScriptComposite.setBounds(5, 5, 463, 435);
		deckScriptComposite.setLayout(null);

		for(Deck curDeck : Manager.getDecks())
		{
			DeckScript.checkIfCompleat(curDeck, Main.impDir, Main.expDir, this);
		}

		grpDeckScript = new Group(deckScriptComposite, SWT.NONE);

		grpDeckScript.setText("IO Script");
		grpDeckScript.setBounds(0, 0, 463, 410);

		table = new Table(grpDeckScript, SWT.BORDER);
		table.setLocation(10, 20);
		table.setSize(430, 202);

		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		TableColumn column2 = new TableColumn(table, SWT.LEFT);
		TableColumn column3 = new TableColumn(table, SWT.LEFT);


		column1.setText("Deck");
		column2.setText("Progress");
		column3.setText("Info");


		column1.setWidth(200);
		column2.setWidth(70);
		column3.setWidth(150);

		table.setHeaderVisible(true);
		logLabel = new Label(grpDeckScript, SWT.BEGINNING);
		logLabel.setLocation(10, 228);
		logLabel.setSize(20, 15);
		logLabel.setText("Log");
		logWidget = new StyledText(grpDeckScript, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		logWidget.setLocation(10, 248);
		logWidget.setSize(430, 123);

		initTables();
	}

	/**
	 * Initializes all button widgets and attaches listeners to them
	 * @param shell The shell we are attaching these widgets to
	 */
	public void initButtonWidgets(Shell shell)
	{
		/*
		 * Buttons
		 */
		btnStart = new Button(grpDeckScript, SWT.NONE);
		btnStart.setBounds(10, 375, 75, 25);
		btnStart.setText("Start");

		btnRefresh = new Button(grpDeckScript, SWT.NONE);
		btnRefresh.setBounds(91, 375, 75, 25);
		btnRefresh.setText("Refresh");

		btnForce = new Button(grpDeckScript, SWT.NONE);
		btnForce.setBounds(172, 375, 75, 25);
		btnForce.setText("Force Start");

		progressBar = new ProgressBar(grpDeckScript, SWT.NONE);
		progressBar.setBounds(36, 228, 170, 17);
		progressBar.setVisible(false);

		//Check if the decks are converted
		if(Manager.areDecksCompleat())
		{
			btnStart.setEnabled(false);
			setLog("Decks are already compleat!");
		} else {
			setLog("Ready!");
		}
		
		/*
		 * Listeners
		 */
		
		listener_start = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
				{
					btnStartPressed();
					break;
				}
				}
			}
		};

		listener_refresh = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
				{
					btnRefreshPressed();
					break;
				}
				}
			}
		};

		listener_force = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
				{
					btnForcePressed();
					break;
				}
				}
			}
		};
		
		/*
		 * Attach listeners to Buttons
		 */

		btnStart.addListener(SWT.Selection, listener_start);
		btnRefresh.addListener(SWT.Selection, listener_refresh);
		btnForce.addListener(SWT.Selection, listener_force);
	}

	/**
	 * Updates the text based widgets asynchronously
	 * @param deck The deck to update
	 */
	public void asyncUpdateTextWidgets(Deck deck)
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				//System.out.println("Updating deck GUI --");
				updateDeckProgressLabels(deck);
			}
		});
	}

	/**
	 * Updates the Card Image related widgets asynchronously
	 * @param card The card we want to load the image for
	 */
	public void asyncUpdateCardImage(Card card)
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				
				cardImageManager.loadImage(displayMain, card); //Load new image
				if(card.getName() != null)
					labelCardName.setText("Card: "+card.getName()); //Update labels related to the new image

				labelCardName.update();
			}
		});
	}


	/**
	 * Checks whether or not to start the script when the Start Button is pressed
	 */
	private void btnStartPressed()
	{
		if(btnStart.isEnabled())
		{
			//Disable the buttons
			disableButtons();

			progressBar.setVisible(true);
			progressBar.update();

			//Executes the DeckScript in a new thread
			ScriptThread st = new ScriptThread(this, false);
			Thread thread = new Thread(st);

			thread.start();
		}
	}

	/**
	 * Checks whether or not to force the script to start when the force button is pressed
	 */
	public void btnForcePressed()
	{
		if(btnForce.isEnabled())
		{
		    //This is incase the force button is ran multiple times
		    for(Deck curDeck : Manager.getDecks())
		    {
		        curDeck.setProgressInt(0);
		    }
		    
			System.out.println("Forced!");
			
			//Disable the buttons
			disableButtons();

			progressBar.setVisible(true);
			progressBar.update();

			//Executes the DeckScript in a new thread
			ScriptThread st = new ScriptThread(this, true);
			Thread thread = new Thread(st);

			thread.start();
		}
	}

	/**
	 * Refreshes the state of many GUI elements related to DeckScript, useful
	 * for when you add new files to the import folder after starting the application.
	 */
	public void btnRefreshPressed()
	{
		if(btnRefresh.isEnabled())
		{
			disableButtons();
			
			DeckScript.Init();

			setScriptState(ScriptState.IDLE);
			resetWidgets();
		}
	}
	
	/**
	 * Resets widgets based on the current state of the script and or application
	 * <p> Useful because we often need to change many similar GUI elements at the same time
	 */
	synchronized public void resetWidgets()
	{
		//System.out.println(curState.toString());
		
		if(curState != ScriptState.RUNNING)
		{
			if(Manager.areDecksCompleat())
			{
				btnStart.setEnabled(false);
				btnRefresh.setEnabled(true);
				btnForce.setEnabled(true);
				
				setLog("Decks are already compleat!");
			} else {
				btnStart.setEnabled(false);
				btnForce.setEnabled(true);
				btnRefresh.setEnabled(true);
				setLog("Ready!");
			}
			
			//Update button widgets
			btnStart.update();
			btnRefresh.update();
			btnForce.update();
			
		} else {
			disableButtons();
		}
		
		//Set whether or not the progress bar is visible based on state
		updateProgressBar();
	}
	
	synchronized private void updateProgressBar()
	{
		switch(curState)
		{
		case FINISHED:
			progressBar.setVisible(false);
			break;
		case IDLE:
			progressBar.setVisible(false);
			break;
		case RUNNING:
			progressBar.setVisible(true);
			break;
		default:
			System.out.println("State default?");
			break;
		}
		
		progressBar.update();
	}
	
	/**
	 * Disables all buttons
	 */
	private void disableButtons()
	{
		btnStart.setEnabled(false);
		btnRefresh.setEnabled(false);
		btnForce.setEnabled(false);
		
		btnStart.update();
		btnRefresh.update();
		btnForce.update();
	}

	/**
	 * Changes the text field of the Log
	 * @param newStatus The new value for the Log widget's text field
	 */
	public void setLog(String newStatus)
	{
		logWidgetContent = newStatus;
		logWidget.setText(logWidgetContent);

		logWidget.update();
		deckScriptComposite.update();
	}

	/**
	 * Adds a table item for each deck if it doesn't exist already
	 */
	public void addDecks()
	{
		for(Deck deck : Manager.getDecks())
		{
			if(deckTableMap.get(deck) == null)
			{
				System.out.println("Adding deck status to GUI");
				addDeckTableItem(deck);
			}
		}
	}
	
	/**
	 * Adds a table item for a deck, which contains fields representing its status and progress relative to the script
	 * @param deck The deck to generate and add the new TableItem for
	 */
	public void addDeckTableItem(Deck deck)
	{
		//Make a table entry for the deck
		TableItem newDeckTableItem = new TableItem(table, SWT.NONE);
		newDeckTableItem.setText(new String[] { deck.getName(), deck.getFileProgess(), deck.getFileStatus() });
		deckTableMap.put(deck, newDeckTableItem);
	}

	/**
	 * Updates the fields of the TableItem for a given deck based on its current state
	 * @param deck The deck to update
	 */
	public void updateDeckProgressLabels(Deck deck)
	{
		TableItem ti = deckTableMap.get(deck);
		ti.setText(1, deck.getFileProgess());
		ti.setText(2, deck.getFileStatus());
		table.update();


		progressBar.setMaximum(deck.getFileLineCount());
		progressBar.setSelection(deck.getFileProgressInt());
		progressBar.update();
	}

	/**
	 * Fills the table with TableItems for each deck
	 */
	private void initTables()
	{

		addDecks();
	}
	
	/**
	 * Changes the current state of the script
	 * @param newState the value to change the state of the script to
	 */
	synchronized public void setScriptState(ScriptState newState)
	{
		curState = newState;
	}
	
	/**
	 * Synchronized method
	 * <p> Resets widgets based on the state of the application, run asynchronously
	 */
	synchronized public void asyncWidgetReset()
	{
	    Display.getDefault().asyncExec(new Runnable() {
	        public void run() {
	            resetWidgets();
	        }
	    });
	}
}
