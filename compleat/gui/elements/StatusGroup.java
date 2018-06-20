package compleat.gui.elements;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;

public class StatusGroup extends Composite {
	
	private String 			statusStr 		= "Idle"; //Default state is idle
	private Label 			statusLabel;
	HashMap<String, Label> 	decks 			= new HashMap<String, Label>();
	private Group group;
	
	public StatusGroup(Composite parent)
	{
		super(parent, SWT.NONE);
		this.setSize(300, 300);
		
		group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLocation(0, 0);
		
		group.setText("Compleat Script Log");
		
		statusLabel = new Label(group, SWT.NONE);
		statusLabel.setText("Status: "+statusStr);
		statusLabel.setLocation(20,20);
		statusLabel.pack();
		
		
		/**
		Button button = new Button(group, SWT.PUSH);
		button.setText("Push button in Group");
		button.setLocation(20,45);
		button.pack();
		**/
		
		group.pack();
	}
	
	public void setStatus(String newStatus)
	{
		statusStr = newStatus;
		
		statusLabel.setText("Status: "+statusStr);
		statusLabel.pack();
		
		this.update();
	}
	
	public void addDeckLabel(String deckName)
	{
		Label newDeckLabel = new Label(group, SWT.NONE);
		decks.put(deckName, newDeckLabel); //Add deck to our map
		
		newDeckLabel.setText(deckName + ": WAITING"); //Default state for our deck label
	}
}
