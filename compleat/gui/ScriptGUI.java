package compleat.gui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import compleat.gui.elements.CardImageManager;
import compleat.runnables.ScriptThread;
import compleat.scripts.DeckScript;
import io.magicthegathering.javasdk.resource.Card;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ProgressBar;

public class ScriptGUI {
	
	private Display display;
	
	//Local vars
	Label statusLabel = null;
	Button btnStart;
	Button btnRefresh;
	Button btnForceStart;
	
	int mainWindowWidth 	= 1024;
	int mainWindowHeight 	= 768/2 + 100;
	
	//int deckStatusGroup_width = mainWindowWidth / 2;
	//int deckStatusGroup_height = mainWindowHeight;
	
	//Main composite
	private Composite mainComposite;
	
	//Deck Conversion stuff
	private Group deckGroup;
	
	//Card Art stuff
	private Composite cardArtCanvas;
	public CardImageManager cardImageManager;
	private GridData img_gd;
	
	String appTitle = "The Compleat Tool - "+Main.verNum;
	
	//Listeners
	Listener listener_start;
	Listener listener_refresh;
	Listener listener_force;
	
	private Shell shell_1;
	private Group grpCardinfogroup;
	private Label ci_label_cardname;
	
	//Tables
	HashMap<Deck, TableItem> decks = new HashMap<Deck, TableItem>();
		
	//Table widget
	private Table table;
		
	//Log Label
	private Label logLabel;
	private StyledText logWidget;
	private String logStr = "Idle";
	
	private ProgressBar progressBar;
	
	
	
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
			
			display = createDisplay();
			shell_1 = createShell(display);
			
			
			shell_1.setSize(841, 462);
			shell_1.setText(appTitle);
			shell_1.update();
			
			initLayout(shell_1);
			
			grpCardinfogroup = new Group(shell_1, SWT.NONE);
			grpCardinfogroup.setText("Card Info");
			grpCardinfogroup.setBounds(474, 5, 341, 410);
			
			cardArtCanvas = new Composite(grpCardinfogroup, SWT.NONE);
			cardArtCanvas.setLocation(59, 24);
			cardArtCanvas.setSize(223, 311);
			
			cardImageManager = new CardImageManager(cardArtCanvas, display);
			
			ci_label_cardname = new Label(grpCardinfogroup, SWT.NONE);
			ci_label_cardname.setBounds(10, 341, 213, 15);
			ci_label_cardname.setText("Card: ");
		     
		     while (!shell_1.isDisposed ()) {
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
			shell_1.setLayout(null);
			//Make the parent object
			mainComposite 				= new Composite(shell, SWT.NONE);
			mainComposite.setBounds(5, 5, 463, 435);
	        mainComposite.setLayout(null);
	        
	        for(Deck curDeck : Manager.getDecks())
			{
				DeckScript.checkIfCompleat(curDeck, Main.impDir, Main.expDir, this);
			}
	        
	        deckGroup = new Group(mainComposite, SWT.NONE);
			
			deckGroup.setText("IO Script");
			deckGroup.setBounds(0, 0, 463, 410);
			
			table = new Table(deckGroup, SWT.BORDER);
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
			logLabel = new Label(deckGroup, SWT.BEGINNING);
			logLabel.setLocation(10, 228);
			logLabel.setSize(20, 15);
			logLabel.setText("Log");
			logWidget = new StyledText(deckGroup, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			logWidget.setLocation(10, 248);
			logWidget.setSize(430, 123);
				    
			initTables(deckGroup);
			
			
		}
		
		public void initButtonWidgets(Shell shell)
		{
			btnStart = new Button(deckGroup, SWT.NONE);
			btnStart.setBounds(10, 375, 75, 25);
			btnStart.setText("Start");
			
			btnRefresh = new Button(deckGroup, SWT.NONE);
			btnRefresh.setBounds(91, 375, 75, 25);
			btnRefresh.setText("Refresh");
			
			btnForceStart = new Button(deckGroup, SWT.NONE);
			btnForceStart.setBounds(172, 375, 75, 25);
			btnForceStart.setText("Force Start");
			
			progressBar = new ProgressBar(deckGroup, SWT.NONE);
			progressBar.setBounds(36, 228, 170, 17);
			progressBar.setVisible(false);
			
			if(Manager.areDecksCompleat())
			{
				btnStart.setEnabled(false);
				setLog("Decks are already compleat!");
			} else {
				setLog("Ready!");
			}
			
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
			
		    btnStart.addListener(SWT.Selection, listener_start);
		    btnRefresh.addListener(SWT.Selection, listener_refresh);
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
					updateDeckProgressLabels(deck);
		        }
		    });
		}
		
		public void asyncUpdateCardInfo(Card card)
		{
			Display.getDefault().asyncExec(new Runnable() {
		        public void run() {
		        	UpdateCardInfoWidgets(card);
		        }
		    });
		}
		
		synchronized public void UpdateCardInfoWidgets(Card card)
		{
			cardImageManager.loadImage(display, card);
			if(card.getName() != null)
			ci_label_cardname.setText("Card: "+card.getName());
			
			ci_label_cardname.update();
		}
		
		public void btnStartPressed()
		{
			if(btnStart.isEnabled())
			{
				btnStart.setEnabled(false);
				btnStart.update();
				//btnStart.removeListener(SWT.Selection, listener_start);
				
				progressBar.setVisible(true);
				progressBar.update();
				
				ScriptThread st = new ScriptThread(this);
				Thread thread = new Thread(st);
				
				thread.start();
			}
		}
		
		public void btnRefreshPressed()
		{
			DeckScript.Init();
			
			if(Manager.areDecksCompleat())
			{
				btnStart.setEnabled(false);
				setLog("Decks are already compleat!");
			} else {
				btnStart.setEnabled(true);
				setLog("Ready!");
			}
		}
		
		public void initLog(Group group)
		{
			//setLog(logStr);
		}
		
		public void setLog(String newStatus)
		{
			logStr = newStatus;
			logWidget.setText(logStr);
			//logWidget.pack();
			
			logWidget.update();
			mainComposite.update();
		}
		
		public void addDeckTableItem(Deck deck)
		{
			//Make a table entry for the deck
			TableItem newDeckTableItem = new TableItem(table, SWT.NONE);
			newDeckTableItem.setText(new String[] { deck.getName(), deck.getFileProgess(), deck.getFileStatus() });
			decks.put(deck, newDeckTableItem);
		}
		
		public void addDecks()
		{
			for(Deck deck : Manager.getDecks())
			{
				System.out.println("Adding deck status to GUI");
				addDeckTableItem(deck);
			}
		}
		
		public void updateDeckProgressLabels(Deck deck)
		{
			TableItem ti = decks.get(deck);
			ti.setText(1, deck.getFileProgess());
			ti.setText(2, deck.getFileStatus());
			table.update();
			
			
			progressBar.setMaximum(deck.getFileLineCount());
			progressBar.setSelection(deck.getFileProgressInt());
			progressBar.update();
			
		}
		
		private void initTables(Group group)
		{
		    
		    addDecks();
		}
}
