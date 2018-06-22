package compleat.gui.elements;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import compleat.Manager;
import compleat.datatypes.Deck;

import org.eclipse.swt.widgets.Group;

public class StatusGroup extends Composite {
	
	//Log that shows below the decks
	
	
	//Tables
	HashMap<Deck, TableItem> decks = new HashMap<Deck, TableItem>();
	
	//Group Widget
	private Group group;
	private GridLayout gridLayout;
	
	//Table widget
	private Table table;
	private GridData table_gd;
	
	//Log Widget
	private GridData log_gd;
	private StyledText logWidget;
	private String logStr = "Idle";
	
	
	
	public StatusGroup(Composite parent)
	{
		super(parent, SWT.NONE);
		//this.setSize(300, 1024/2);
		
		group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLocation(0, 0);
		
		gridLayout = new GridLayout(1, false);
		
		group.setLayout(gridLayout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		group.setText("Decks");

		initTables(group);
		
		Label logLabel = new Label(group, SWT.BEGINNING);
		logLabel.setText("Log");
		
		initLog(group);
		
		
		
		//logWidget.pack();
		//logWidget.setLocation(20, heightMargin);
		//logWidget.pack();
		
		//System.out.println("Grid Columns: " + gridLayout);
		
		group.pack();
	}
	
	public void initLog(Group group)
	{
		logWidget = new StyledText(group, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		log_gd = new GridData();
		log_gd.horizontalAlignment 	= GridData.FILL;
		log_gd.verticalAlignment 	= GridData.FILL;
		log_gd.verticalSpan 		= 5;
		
		log_gd.grabExcessVerticalSpace = true;
		
		
		logWidget.setLayoutData(log_gd);
		//setLog(logStr);
	}
	
	public void setLog(String newStatus)
	{
		logStr = newStatus;
		logWidget.setText(logStr);
		//logWidget.pack();
		
		logWidget.update();
		this.update();
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
	}
	
	private void initTables(Group group)
	{
		table = new Table(group, SWT.BORDER);
		
		//GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		table_gd = new GridData();
		//gd.verticalSpan = 3;
		table_gd.horizontalAlignment 	= GridData.FILL;
		table_gd.verticalAlignment 		= GridData.FILL;
		table_gd.verticalSpan 			= 50;
		
		//gd.verticalSpan = 6;
		
		//gd.grabExcessHorizontalSpace = true;
		//gd.grabExcessVerticalSpace = true;
		
		//table.setLayout(new GridLayout(1, false));
        table.setLayoutData(table_gd);

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
	    
	    addDecks();
	}
}
