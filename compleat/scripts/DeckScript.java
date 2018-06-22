package compleat.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import compleat.datatypes.BoardType;
import compleat.datatypes.CardRarityType;
import compleat.datatypes.Category;
import compleat.datatypes.Deck;
import compleat.gui.ScriptGUI;
import compleat.tools.Checksum;

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
		makedir(Main.expDir);
		
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
	    //String directoryName = PATH.concat(this.getClassName());

	    File directory = new File(PATH);
	    if (! directory.exists()){
	    	//JOptionPane.showMessageDialog(null, "Directory made");
	        directory.mkdirs();
	    } else
	    {
	    	//JOptionPane.showMessageDialog(null, "dir exists");
	    }
	    System.out.println(newDirectory);
	}
	
	/**
	 * This is the main workhorse in our script, it converts MTGA deck exports into something useable for our website
	 * @param impDir import directory
	 * @param expDir export directory
	 * @param sGUI our main GUI object
	 * @return
	 */
	public static void processDeckFiles (String impDir, String expDir, ScriptGUI sGUI)
	{
		//Start the conversion script on each deck file
		for(Deck curDeck : Manager.getDecks())
		{
			writeFile(curDeck, impDir, expDir, sGUI);
		}
	}
	
	/**
	 * 
	 * @param impDir import directory
	 * @param expDir export directory
	 */
	private static void addDecks (String impDir, String expDir)
	{
		File dir = new File(impDir + File.separator);
		
		File[] files = dir.listFiles();
		
		//Convert each one
		for (File curFile : files )
		{
			Manager.addDeck(curFile);
		}
		
		System.out.println("[DEBUG] Found "+files.length+" decks!");
		
		if(files.length == 0) {
			JOptionPane.showMessageDialog(null, "Directories are made, add the files to the import directory and run the application again!");
		}
	}
	
	/**
	 * The name is an MTGA reference
	 * If we've already converted a file, we mark it "Compleat"
	 */
	synchronized public static boolean checkIfCompleat(Deck curDeck, String impDir, String expDir, ScriptGUI sGUI)
	{
		System.out.println("Checking to see if deck is compleat...");
		String fileName = curDeck.getName();
		String impFilePATH = impDir + File.separator + fileName;
		String expFilePATH = expDir + File.separator + fileName;
		
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
			
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param curDeck The deck we are currently processing and writing an export file for
	 * @param impDir import directory
	 * @param expDir export directory
	 * @param sGUI instance of our GUI
	 */
	static void writeFile(Deck curDeck, String impDir, String expDir, ScriptGUI sGUI)
	{
		System.out.println("Writing deck: "+curDeck.getName());
		String fileName = curDeck.getName();
		
		String impFilePATH = impDir + File.separator + fileName;
		String expFilePATH = expDir + File.separator + fileName;
		
		try {
			
			if(checkIfCompleat(curDeck, impDir, expDir, sGUI))
			{
				System.out.println("Checksum in export matches import file!");
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
					    	
					    	updateDeckGUIElements(curDeck, sGUI, curDeck.getFileProgess(), "Sending Query");
					    	
					    	//Finds the name of the card and tries to match it to an existing MTG card
					    	parseCardName(line, curDeck, bt);
					    	
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
				
				curDeck.setCompleat();
			}
		} catch (Exception e1) {
			System.out.println("Error comparing md5 checksums");
			e1.printStackTrace();
		}
		
		updateDeckGUIElements(curDeck, sGUI, null, null);
	}
	
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
	
	public static void parseCardName(String line, Deck deck, BoardType bt)
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
	}
	
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
	
	public static void AddRarity(int cardCount, CardRarityType crt, int basic_land, int uncommon, int common, int rare, int mythic_rare)
	{
		switch(crt)
		{
		case BASIC_LAND:
			basic_land+=cardCount;
			break;
		case UNCOMMON:
			uncommon+=cardCount;
			break;
		case COMMON:
			common+=cardCount;
			break;
		case RARE:
			rare+=cardCount;
		case MYTHIC_RARE:
			mythic_rare+=cardCount;
		case SPECIAL:
			break;
		}
	}

	
}


