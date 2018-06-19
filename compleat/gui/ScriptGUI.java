package compleat.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import compleat.Main;

public class ScriptGUI {
	
	//Local vars
	Label statusLabel = null;
	
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
			
			Display display = createDisplay();
			Shell shell = createShell(display);
			
			shell.setSize(640, 800);
			
			addWidgets(shell);
			
			
		    shell.open ();
		     
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
			addTextLabels(shell);
			
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
			statusLabel = new Label (shell, SWT.LEFT);
			statusLabel.setLocation(shell.getBounds().height / 2, shell.getBounds().width);
			
			//set default status
			statusLabel.setText ("Current Task: " + Main.curLog);
			statusLabel.setBounds (shell.getClientArea ());
		}
}
