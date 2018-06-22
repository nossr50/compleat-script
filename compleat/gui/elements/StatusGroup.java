package compleat.gui.elements;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import compleat.Manager;
import compleat.datatypes.Deck;

import org.eclipse.swt.widgets.Group;

public class StatusGroup extends Composite {
	
	private String 			statusStr 		= "Idle"; //Default state is idle
	private Label 			statusLabel;
	HashMap<Deck, TableItem> 	decks 			= new HashMap<Deck, TableItem>();
	private Group group;
	//private int heightMargin = 20;
	private Table table;
	
	public StatusGroup(Composite parent)
	{
		super(parent, SWT.NONE);
		//this.setSize(300, 1024/2);
		
		group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLocation(0, 0);
		
		group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		group.setText("Compleat Script Log");
		
		/**
		statusLabel = new Label(group, SWT.NONE);
		statusLabel.setText("Status: "+statusStr);
		statusLabel.setLocation(20, heightMargin);
		statusLabel.pack();
		**/
		
		initTables(group);
		group.pack();
	}
	
	public void setStatus(String newStatus)
	{
		statusStr = newStatus;
		
		statusLabel.setText("Status: "+statusStr);
		statusLabel.pack();
		
		this.update();
	}
	
	public void addDeckTableItem(Deck deck)
	{
		//Make a table entry for the deck
		TableItem newDeckTableItem = new TableItem(table, SWT.NONE);
		newDeckTableItem.setText(new String[] { deck.getName(), "Queued" });
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
	
	public String getDeckProgress(Deck deck)
	{
		return deck.getFileProgess() + "/" + deck.getFileLineCount();
	}
	
	public void updateDeckProgressLabels(Deck deck)
	{
		TableItem ti = decks.get(deck);
		ti.setText(1, getDeckProgress(deck));
		table.update();
	}
	
	private void initTables(Group group)
	{
		table = new Table(group, SWT.BORDER);
		
		table.setLayout(new GridLayout(1, false));
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    TableColumn column1 = new TableColumn(table, SWT.LEFT);
	    TableColumn column2 = new TableColumn(table, SWT.LEFT);
	    
	    
	    column1.setText("Deck");
	    column2.setText("Progress");
	    
	    
	    column1.setWidth(200);
	    column2.setWidth(70);
	    table.setHeaderVisible(true);
	    
	    addDecks();
	}
}
