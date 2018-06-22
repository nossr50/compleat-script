package compleat;

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

import compleat.datatypes.BoardType;
import compleat.datatypes.CardRarityType;
import compleat.datatypes.Category;
import compleat.datatypes.Deck;
import compleat.gui.ScriptGUI;

public class ScriptIO {

	//honestly I don't remember shit about Java so ignore anything crazy here
	public static void Init(final String impDir, final String expDir)
	{
		//Create file directories if they don't exist
		makedir(impDir); 
		makedir(expDir);
		
		//Make Deck objects out of our files to prep for IO
		addDecks(impDir, expDir);
	}
	
	static void makedir (String impDir)
	{
		String PATH = impDir + File.separator; //System specific directory shit
	    //String directoryName = PATH.concat(this.getClassName());

	    File directory = new File(PATH);
	    if (! directory.exists()){
	    	//JOptionPane.showMessageDialog(null, "Directory made");
	        directory.mkdirs();
	    } else
	    {
	    	//JOptionPane.showMessageDialog(null, "dir exists");
	    }
	    System.out.println(impDir);
	}
	
	public static String processDeckFiles (String impDir, String expDir, ScriptGUI sGUI)
	{
		//Start the conversion script on each deck file
		for(Deck curDeck : Manager.getDecks())
		{
			writeFile(curDeck, impDir, expDir, sGUI);
		}
		
		return null;
	}
	
	static String addDecks (String impDir, String expDir)
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
		
		return null;
	}
	
	static void writeFile(Deck curDeck, String impDir, String expDir, ScriptGUI sGUI)
	{
		System.out.println("Writing deck: "+curDeck.getName());
		String fileName = curDeck.getName();
		
		try {
			
			String PATH = impDir + File.separator + fileName;
			
			BufferedReader br 					= new BufferedReader(new FileReader(PATH));
			BufferedWriter bw 					= null;
			FileWriter fw 						= null;
			StringBuilder sb 					= new StringBuilder();
			
			BoardType bt = BoardType.MAINBOARD;
			boolean firstEmptyLine = false;
			
			//Go through each line and convert it
			try {
			    
			    String line = "";

			    while ((line = br.readLine()) != null) {
			    	
			    	if(line.length() == 0) {
			    		//If we hit an empty line it probably means we are hitting the blank line before the sideboard
			    		
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
			    	
			    	//Add the card to our deck
			    	parseCardName(line, curDeck, bt);
			    	
			    	sGUI.updateTextWidgets(curDeck);
			    }
			    
			    //Now that we are done adding all the cards, build the export lines
			    curDeck.buildExportLines();
			    
			    //Write to the file
			    
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				
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

			

		} catch (IOException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Something broke, tell nossr");

		}
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


