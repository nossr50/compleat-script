package compleat.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import compleat.Main;
import compleat.Manager;
import compleat.datatypes.Category;
import compleat.datatypes.Deck;
import compleat.datatypes.enums.BoardType;
import compleat.gui.ScriptGUI;
import compleat.tools.Checksum;

/**
 * This class contains the main script for converting MTGA export dumps or MTG cards into a format for the Compleat website
 * <p> This class also features many helper functions related to operations of its main script, and helper functions related to updating the GUI as the script does its job.
 * @author nossr50
 *
 */
public class DeckScript {

	/**
	 * Performs the basic operations required for our script to function
	 * Makes sure we have directories for our script (import/export)
	 * Adds any decks found in the import directory to our HashMap
	 */
	public static void Init()
	{
		//Create file directories if they don't exist
		makedir(Main.impDir);
		makedir(Main.impDir+"/guides");
		makedir(Main.expDir+"/guides");
		
		//Make Deck objects out of our files to prep for IO
		addDecks(Main.impDir, Main.expDir);
	}
	
	/**
	 * Creates File directory if it doesn't exist
	 * @param newDirectory Directory name
	 */
	private static void makedir (String newDirectory)
	{
		String PATH = newDirectory + File.separator; //System specific directory shit

	    File directory = new File(PATH);
	    if (! directory.exists()){
	        directory.mkdirs();
	        //This is only shown if the import directory had to be created
	        JOptionPane.showMessageDialog(null, "Add files into the import directory and refresh the application!");
	    } else
	    {
	    	//JOptionPane.showMessageDialog(null, "dir exists");
	    }
	    System.out.println(newDirectory);
	}
	
	/**
	 * This is the main workhorse in our script, it converts MTGA deck exports into data and code for the Compleat website
	 * @param impDir import directory
	 * @param expDir export directory
	 * @param sGUI a reference to our GUI object
	 * @param force whether or not to force the script to convert the import file
	 */
	public static void processDeckFiles (String impDir, String expDir, ScriptGUI sGUI, boolean force)
	{
		//Start the conversion script on each deck file
		for(Deck curDeck : Manager.getDecks())
		{
			writeFile(curDeck, impDir, expDir, sGUI, force);
		}
	}
	
	/**
	 * Initializes a Deck object for each file found in the import directory
	 * @param impDir import directory
	 * @param expDir export directory
	 */
	private static void addDecks (String impDir, String expDir)
	{
		File dir = new File(impDir + File.separator);
		
		File[] files = dir.listFiles();
		
		//Initialize each one
		for (File curFile : files )
		{
		    if(!curFile.isDirectory())
		        Manager.addDeck(curFile);
		}
		
		System.out.println("[DEBUG] Found "+files.length+" decks!");
		
		if(files.length == 0) {
			JOptionPane.showMessageDialog(null, "Directories are made, add the files to the import directory and run the application again!");
		}
	}
	
	/**
	 * Checks whether or not a Deck's export file contains an md5 checksum identical to the import file stored in its metadata
	 * and if it does, that means we have already converted it before and therefor it is "compleat"
	 * @param curDeck The deck to check
	 * @param impDir Import directory path
	 * @param expDir Export directory path
	 * @param sGUI reference to our sGUI object
	 * @return true if matching md5 checksum was found in the export file, false otherwise
	 */
	synchronized public static boolean checkIfCompleat(Deck curDeck, String impDir, String expDir, ScriptGUI sGUI)
	{
		System.out.println("Checking to see if deck is compleat...");
		String fileName = curDeck.getName();
		String impFilePATH = impDir + File.separator + fileName;
		String expFilePATH = expDir + File.separator + fileName;
		
		File expFile = new File(expFilePATH);
		
		if(!expFile.exists())
		{
			System.out.println("File doesn't exist");
			return false;
		}
		
		String imp_md5;
		String exp_metadata_md5 = Checksum.readAttributes(expFilePATH); //The export file contains the MD5 of the import file in its metadata
		
		try {
				imp_md5 = Checksum.getMD5Checksum(impFilePATH);
				
				if(imp_md5 != null)
					System.out.println("IMP : "+imp_md5);
				
				if(exp_metadata_md5 != null)
					System.out.println("EXP : "+exp_metadata_md5);
				
				if(imp_md5.equals(exp_metadata_md5))
				{
					System.out.println("Deck is compleat!");
					curDeck.setCompleat();
					updateDeckGUIElements(curDeck, sGUI, null, null);
					System.out.println("Deck is compleat 2!");
					
					return true;
				} else {
					System.out.println("Deck isn't compleat!");
					curDeck.setFileStatus("Idle");
					return false;
				}
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This function parses every line of a decks import file for relevant information and then dumps data constructed from that file into an export file of the same name
	 * <p> This is the main workhorse of our DeckScript
	 * @param curDeck The deck we are currently processing and writing an export file for
	 * @param impDir import directory
	 * @param expDir export directory
	 * @param sGUI instance of our GUI
	 * @param force whether or not to force the script to convert the file
	 */
	public static void writeFile(Deck curDeck, String impDir, String expDir, ScriptGUI sGUI, boolean force)
	{
		System.out.println("Writing deck: "+curDeck.getName());
		String fileName = curDeck.getName();
		
		String impFilePATH = impDir + File.separator + fileName;
		String expFilePATH = expDir + File.separator + fileName;
		
		try {
			
			if(force || !checkIfCompleat(curDeck, impDir, expDir, sGUI))
			{
				try {
					//File Path
					String PATH = impDir + File.separator + fileName;
					
					//IO
					BufferedReader br 					= new BufferedReader(new FileReader(PATH));
					BufferedWriter bw 					= null;
					FileWriter fw 						= null;
					StringBuilder sb 					= new StringBuilder();
					
					//Whether or not we are still Mainboard in the current file
					BoardType bt = BoardType.MAINBOARD; //We start at the mainboard so the default state is mainboard
					boolean firstEmptyLine = false; //Tracks whether or not we've hit the first empty line in the file (which denotes the sideboard)
					
					//Process each line of the file
					try {
					    
					    String line = ""; //String of the current line in the txt file
					    
					    while ((line = br.readLine()) != null) {
					    	updateDeckGUIElements(curDeck, sGUI, null, "Processing data");
					    	
					    	/*
					    	 * The sideboard is denoted by an empty line separating it from the other cards
					    	 * We check for that here
					    	 */
					    	if(line.length() == 0) {
					    		if(firstEmptyLine)
					    		{
					    			sb.append(System.lineSeparator());
					    		} else {
					    			firstEmptyLine = !firstEmptyLine; //This is just to get rid of the empty line at the start of the file
					    		}
					    		
					    		System.out.println("TURNING ON SIDEBOARD");
					    		bt = BoardType.SIDEBOARD;
					    		continue;
					    	}
					    	
					    	updateDeckGUIElements(curDeck, sGUI, curDeck.getFileProgressString(), "Sending Query");
					    	
					    	//Finds the name of the card and tries to match it to an existing MTG card
					    	String cardName = parseCardName(line, curDeck, bt); //This also adds our card to the DB
					    	
					    	sGUI.asyncUpdateCardImage(Manager.getCard(cardName));
					    	
					    	//Line progress counter for our GUI
					    	curDeck.updateProgress();
					    }
					    
					    
					    /*
					     * Setup export lines for the new file
					     */
					    
					    //Now that we've grabbed all Cards from the file, build the Strings for our Export file
					    curDeck.buildExportLines();
					    
					    //Date for when the script was ran
					    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date = new Date();
						
						//MD5 for the file converted
						
						for(Category cat : Category.values())
						{
							ArrayList<String> exportLines = curDeck.getExportLines(cat);
							
							if(exportLines.size() <= 1) {
								//This means only the default line is there (nothing else)
								//Skip it then
								
								continue;
							}
							
							for(String curLine : exportLines)
							{
								sb.append(curLine + System.lineSeparator());
							}
							
							sb.append(System.lineSeparator());
						}
					    
						
						//Making sure Gravez never forgets me Kappa Keepo
						sb.append("////////////////////////////////////////////////");
						sb.append(System.lineSeparator());
					    sb.append("Conversion Generated on : " + dateFormat.format(date));
					    sb.append(System.lineSeparator());
					    sb.append("Script by : nossr50 <3");
					    sb.append(System.lineSeparator());
					    sb.append("////////////////////////////////////////////////");
					    
					} finally {
					    br.close();
					}
					
					/*
					 * Write to the file
					 */
					
					System.out.println("Writing file");
					
					File curFile = new File(expDir + File.separator + fileName); //export destination
					
					fw = new FileWriter(curFile.getAbsoluteFile(), true);
					bw = new BufferedWriter(fw);

					if (!curFile.exists()) {
						curFile.createNewFile();
					} else {
						//Clear the contents of the file
						PrintWriter pw = new PrintWriter(expDir + File.separator + fileName);
						pw.close();
					}

					bw.write(sb.toString());
					
					bw.close();
					fw.close();
					
					/*
					 * md5 checksum to avoid needless reconversion
					 */
					Checksum.setAttributes(expFilePATH, Checksum.getMD5Checksum(impFilePATH));
					
					curDeck.setFileStatus("Done"); //haha get it?

				} catch (IOException e) {
					
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Something broke, tell nossr");

				} catch (Exception e) {
					System.out.println("Error generating md5 checksum!");
					e.printStackTrace();
				}
			} else {
				/*
				 * Export file has already been converted so update the GUI
				 */
				System.out.println("Checksum in export matches import file!");
				curDeck.setCompleat();
			}
		} catch (Exception e1) {
			System.out.println("Error comparing md5 checksums");
			e1.printStackTrace();
		}
		
		updateDeckGUIElements(curDeck, sGUI, null, null);
	}
	
	/**
	 * Synchronized Method
	 * <p> Updates GUI elements to reflect the state of the script
	 * @param deck the deck to update GUI elements for
	 * @param sGUI the reference to our GUI
	 * @param progress a new value to replace our current progress String (can be null)
	 * @param status a new value to replace our current status String (can be null)
	 */
	synchronized private static void updateDeckGUIElements(Deck deck, ScriptGUI sGUI, String progress, String status)
	{
		if(progress != null)
		{
			deck.setFileProgress(progress);
		}
		
		if(status != null)
		{
			deck.setFileStatus(status);
		}
		
		sGUI.asyncUpdateTextWidgets(deck);
	}
	
	/**
	 * Parses a line of the import file for a card name, and how many cards should be added
	 * @param line The line of the import file to be processed
	 * @param deck The associated Deck for this import file
	 * @param bt The board in which this card belongs
	 * @return The name of the card after its been parsed from the String
	 */
	public static String parseCardName(String line, Deck deck, BoardType bt)
	{
		String cardCount 				= "";
		int cardNameStartPos 			= 0;
    	char[] lineChars 				= line.toCharArray();
    	int cardCountInteger			= 1; //We'll default to one in case we don't hit a number
    	
    	//Get the cardCounts before parsing the rest of the string
    	for(int x = 0; x < lineChars.length; x++)
    	{
    		String curLetter = Character.toString(lineChars[x]); //current character
    		
    		try {
    			Integer.parseInt(curLetter);
    			
    			//If we get this far its a number
    			cardCount += curLetter;
    			
    		} catch ( NumberFormatException nme ) {
    			//Once we run out of numbers mark the first letter
    			cardNameStartPos = x+1; //Since the first character that isn't a number is always a space we will add 1
    			break;
    		}
    	}
    	
    	try {
    		cardCountInteger = Integer.parseInt(cardCount);
    	} catch ( NumberFormatException nme ) {
			System.out.print("NME! Tell nossr");
		}
    	
    	String cardName = "";
    	
    	//Go through each character and grab only what we need
    	for(int x = cardNameStartPos; x < lineChars.length; x++)
    	{
    		char curLetter = lineChars[x]; //current character
    		
    		//Example line
	    	//3 Angel of Invention (KLD) 4
    		
    		if(x >= (lineChars.length - 1))
    		{
    			cardName += curLetter;
    		} else {
    			if (lineChars[x+1] == '(') {
	    			//if the character after this one is ( we are done building the name
    				//System.out.println("( detected!");
    				break;
    			} else {
    				cardName += curLetter;
    			}
    		}
    	}
    	
    	//Add card to the deck
    	deck.addCards(cardName, cardCountInteger, bt);
    	return cardName;
	}
	
	/**
	 * This is a helper function to help write specific Strings into a file
	 * @param sb The StringBuilder of the file
	 * @param sList the strings to add to the file
	 */
	public static void WriteEntries(StringBuilder sb, List<String> sList)
	{
		for(String s : sList)
		{
			sb.append(s);
			sb.append(System.lineSeparator());
		}
		
		//Add a space between categories
		sb.append(System.lineSeparator());
	}
}