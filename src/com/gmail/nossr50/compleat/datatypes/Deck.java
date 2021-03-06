package com.gmail.nossr50.compleat.datatypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.gmail.nossr50.compleat.Manager;
import com.gmail.nossr50.compleat.datatypes.enums.BoardType;
import com.gmail.nossr50.compleat.datatypes.enums.CardRarityType;
import io.magicthegathering.javasdk.resource.Card;

/**
 * This class is a representation of an MTGA (or MTG) deck. It is associated
 * with a local file found in the import directory, constructed later through
 * parsing each line in the associated file for Strings matching contents of an
 * MTGA Export dump or names of MTG cards.
 * 
 * <p>
 * Deck stores cards within it and sorts them based on which board they belong
 * in (CompleatTool or Sideboard), it tracks progress the script has made parsing the
 * associated file for Cards, and updates relevant variables used in conjunction
 * with our GUI elements as the script progresses.
 * <p>
 * Stores a reference to the File representation of the import file for
 * convenience
 * 
 * @see com.gmail.nossr50.compleat.scripts.DeckScript#writeFile The Deck object has its data
 *      slowly constructed from a file in the import folder during the
 *      DeckScript class's writeFile() function, before that function is called
 *      the Deck object is merely a shell representation of the import file with
 *      no substantial contents.
 * 
 * @author nossr50
 *
 */
public class Deck {

	private HashMap<BoardType, BoardProfile>		boards; //Containers for our decks contents and statistics (Mainboard/Sideboard)
	private HashMap<Category, ArrayList<String>>	exportLines; //Strings built for the export file dump sorted into categories

	private File									deckFile;							//File representation of the local file for this Deck

	/*
	 * vars used primarily to update GUI elements through asynchronous threads
	 */
	private int										gui_lineCount			= 0;		//total lines in files to process
	private int										gui_lineCountProgress	= 0;		//number of lines processed so far
	private String									gui_deckTableProgress	= "";		//represents file progress for our GUI
	private String									gui_deckTableStatus		= "Idle";	//represents current action of the script for our GUI
	private boolean									isCompleat				= false;	//whether or not the associated import file has been converted

	/**
	 * Constructor for Deck
	 * 
	 * @param deckFile
	 *            The File representation of the deck (typically an MTGA export dump
	 *            inside a txt file)
	 */
	public Deck(File deckFile) {
		this.deckFile = deckFile;

		boards = new HashMap<BoardType, BoardProfile>();
		exportLines = new HashMap<Category, ArrayList<String>>();

		BoardProfile mainboard = new BoardProfile();
		BoardProfile sideboard = new BoardProfile();

		boards.put(BoardType.MAINBOARD, mainboard);
		boards.put(BoardType.SIDEBOARD, sideboard);

		initCategoryLines();
		gui_lineCount = initFileLineCount();
	}
	
	/**
	 * This synchronized function sets the isCompleat boolean to true
	 */
	synchronized public void setCompleat() {
		gui_deckTableStatus = "Compleat";
		isCompleat = true;
	}

	/**
	 * Checks for whether or not the deck has been converted
	 * @return true if the deck has been converted, false if it hasn't been
	 */
	synchronized public boolean isCompleat() {
		return isCompleat;
	}

	/**
	 * Synchronized Method
	 * <p> Changes the status String which is used in conjunction with GUI elements
	 * @param status The String which will replace the contents of the current status String
	 */
	synchronized public void setFileStatus(String status) {
		gui_deckTableStatus = status;
	}

	/**
	 * Synchronized method
	 * @return The String representing the status of the File
	 */
	synchronized public String getFileStatus() {
		return gui_deckTableStatus;
	}

	/**
	 * Synchronized method
	 * <p> Changes the progress String which is used in conjunction with GUI elements
	 * @param progress new value for the String
	 */
	synchronized public void setFileProgress(String progress) {
		gui_deckTableProgress = progress;
	}

	/**
	 * Synchronized method
	 * @return Integer value representing how many lines of the file have been processed so far
	 */
	synchronized public int getFileProgressInt() {
		return gui_lineCountProgress;
	}

	/**
	 * Synchronized method
	 * <p> Incrementally increases the lineCountProgress Integer and updates the progress String
	 */
	synchronized public void updateProgress() {
		gui_lineCountProgress += 1;

		setFileProgress(gui_lineCountProgress + " / " + gui_lineCount);
	}
	
	/**
	 * Manually sets the progress of the script
	 * @param newValue the new value of the progress of the script
	 */
	synchronized public void setProgressInt(int newValue)
	{
	    gui_lineCountProgress = newValue;
	    
	    //Update com.gmail.nossr50.compleat status based on the new count
	    if(gui_lineCountProgress < gui_lineCount)
	        isCompleat = false;
	    else
	        isCompleat = true;
	}

	/**
	 * Synchronized method
	 * @return String representing how many lines of the file have been processed so far
	 */
	synchronized public String getFileProgressString() {
		return gui_deckTableProgress;
	}

	/**
	 * Synchronized method
	 * @return Integer value representing how many lines in the file can be processed by our script for use with GUI elements
	 */
	synchronized public int getFileLineCount() {
		return gui_lineCount;
	}

	/**
	 * Adds cards to our deck
	 * @param cardName The name of the card to add
	 * @param numCards The number of the given card to add
	 * @param bt The board the card belongs in
	 */
	public void addCards(String cardName, int numCards, BoardType bt) {
		Card card = Manager.getCard(cardName);
		boards.get(bt).addCard(card, numCards);
	}

	/**
	 * @return The filename of our associated deckFile
	 */
	synchronized public String getName() {
		return deckFile.getName();
	}

	/**
	 * This method prepares Strings for each Category type for use later with our DeckScript
	 * @see com.gmail.nossr50.compleat.scripts.DeckScript#writeFile(Deck, String, String, com.gmail.nossr50.compleat.gui.ScriptGUI, boolean)
	 */
	private void initCategoryLines() {
		for (Category cat : Category.values()) {
			ArrayList<String> newExportLines = new ArrayList<String>();
			String firstLine = cat.toString();
			exportLines.put(cat, newExportLines);

			if (cat == Category.RARITY_COUNT) {
				continue; //We don't need this
			}

			exportLines.get(cat).add(firstLine);
		}
	}

	/**
	 * Returns a formatted line for the export file
	 * @param card The card
	 * @param bt The board the card belongs in
	 * @return The formatted line for our export file
	 */
	public String getFormattedLine(Card card, BoardType bt) {
		int count = boards.get(bt).getCount(card);
		return count + " <a class=\"simple\" href=\"https://deckbox.org/mtg/" + card.getName() + "\">" + card.getName()
		        + "</a><br>";
	}

	/**
	 * Builds Strings for the export file for each category
	 * <p> This is the main meat of what gets put into the export file at the end of the script
	 */
	public void buildExportLines() {
		//All cards in the mainboard get sorted
	    
	    int landCount          = 0;
	    int spellCount         = 0;
	    int creatureCount      = 0;
	    int planeswalkerCount  = 0;
	    int sideboardCount     = 0;
	    
		for (Card card : boards.get(BoardType.MAINBOARD).getCards()) {
			Category cardCategory = getCategoryType(card);
			
			exportLines.get(cardCategory).add(getFormattedLine(card, BoardType.MAINBOARD));
			
			switch(cardCategory)
			{
            case CREATURES:
                creatureCount+=boards.get(BoardType.MAINBOARD).getCount(card);
                break;
            case LANDS:
                landCount+=boards.get(BoardType.MAINBOARD).getCount(card);
                break;
            case LAND_INFO:
                break;
            case OTHER:
                break;
            case PLANESWALKERS:
                planeswalkerCount+=boards.get(BoardType.MAINBOARD).getCount(card);
                break;
            case RARITY_COUNT:
                break;
            case SPELLS:
                spellCount+=boards.get(BoardType.MAINBOARD).getCount(card);
                break;
            default:
                break;
			
			}
		}

		//Cards in the sideboard do not
		for (Card card : boards.get(BoardType.SIDEBOARD).getCards()) {
			System.out.println("Adding " + card.getName() + " to sideboard category!");
			exportLines.get(Category.SIDEBOARD).add(getFormattedLine(card, BoardType.SIDEBOARD));
			
			sideboardCount+=boards.get(BoardType.SIDEBOARD).getCount(card);
		}

		//Rarity Stats
		exportLines.get(Category.RARITY_COUNT).add("Rares (Mainboard)");
		for (CardRarityType crt : CardRarityType.values()) {
			exportLines.get(Category.RARITY_COUNT)
			        .add(crt.toString() + ": " + boards.get(BoardType.MAINBOARD).getRareCount(crt));
		}

		exportLines.get(Category.RARITY_COUNT).add(System.lineSeparator());

		exportLines.get(Category.RARITY_COUNT).add("Rares (Sideboard)");
		for (CardRarityType crt : CardRarityType.values()) {
			exportLines.get(Category.RARITY_COUNT)
			        .add(crt.toString() + ": " + boards.get(BoardType.SIDEBOARD).getRareCount(crt));
		}

		exportLines.get(Category.RARITY_COUNT).add(System.lineSeparator());

		exportLines.get(Category.RARITY_COUNT).add("Rares (Total)");
		
		String commonExpStr       = "";
		String uncommonExpStr     = "";
		String rareExpStr         = "";
		String mythicExpStr       = "";
		
		exportLines.get(Category.RARITY_COUNT).add("<hr>");
		exportLines.get(Category.RARITY_COUNT).add("<div class=\"text-center\">");
		exportLines.get(Category.RARITY_COUNT).add("<strong>");
		
		
		for (CardRarityType crt : CardRarityType.values()) {

		    int sum = 0;
			int main = boards.get(BoardType.MAINBOARD).getRareCount(crt);
			int side = boards.get(BoardType.SIDEBOARD).getRareCount(crt);
			sum = main + side;
			
			switch(crt) {
            case COMMON:
                commonExpStr = "    <img src=\"images/cmn.png\" height=\"25\" class=\"d-inline-block align-top boop\"> : "+sum+" &nbsp;&nbsp;&nbsp;";
                break;
            case MYTHIC_RARE:
                mythicExpStr = "    <img src=\"images/mythic.png\" height=\"25\" class=\"d-inline-block align-top boop\"> : "+sum+" &nbsp;&nbsp;&nbsp;";
                break;
            case RARE:
                rareExpStr = "    <img src=\"images/rare.png\" height=\"25\" class=\"d-inline-block align-top boop\"> : "+sum+" &nbsp;&nbsp;&nbsp;";
                break;
            case UNCOMMON:
                uncommonExpStr = "    <img src=\"images/unc.png\" height=\"25\" class=\"d-inline-block align-top boop\"> : "+sum+" &nbsp;&nbsp;&nbsp;";
                break;
            default:
                break;
			
			}
		}
		
		exportLines.get(Category.RARITY_COUNT).add(mythicExpStr);
		exportLines.get(Category.RARITY_COUNT).add(rareExpStr);
		exportLines.get(Category.RARITY_COUNT).add(uncommonExpStr);
		exportLines.get(Category.RARITY_COUNT).add(commonExpStr);
		
		exportLines.get(Category.RARITY_COUNT).add("</strong>");
		exportLines.get(Category.RARITY_COUNT).add("</div>");
		exportLines.get(Category.RARITY_COUNT).add("<hr>");
        
        

		//Land info
		exportLines.get(Category.LANDS).set(0, "<u><strong>Lands ("+landCount+")</u></strong><br>");
		exportLines.get(Category.CREATURES).set(0, "<br><u><strong>Creatures ("+creatureCount+")</u></strong><br>");
		exportLines.get(Category.SPELLS).set(0, "<br><u><strong>Spells ("+spellCount+")</u></strong><br>");
		exportLines.get(Category.PLANESWALKERS).set(0, "<br><u><strong>Planeswalkers ("+planeswalkerCount+")</u></strong><br>");
		exportLines.get(Category.SIDEBOARD).set(0, "<br><u><strong>Sideboard ("+sideboardCount+")</u></strong><br>");
	}

	/**
	 * Determines what category a card belongs to, which is important for sorting in our export file
	 * @param card The card to categorize
	 * @return The category of that card
	 */
	public Category getCategoryType(Card card) {
		//We must check the card for being a creature before assigning it another category
		//This is because artifact creatures are going to be separated from artifacts
		for (String s : card.getTypes()) {
			//NOTE: Case sensitive
			if (s.toUpperCase().equals("CREATURE")) {
				//System.out.println("CREATURE FOUND");
				return Category.CREATURES;
			}
		}

		for (String s : card.getTypes()) {
			switch (s.toUpperCase()) {
			case "LAND":
				return Category.LANDS;
			case "CREATURE":
				return Category.CREATURES;
			case "PLANESWALKER":
				return Category.PLANESWALKERS;
			case "SPELL":
			case "ENCHANTMENT":
			case "ARTIFACT":
			case "INSTANT":
			case "SORCERY":
				return Category.SPELLS;
			default:
				break;
			}
		}

		//If we reach this far classify card as other
		return Category.OTHER;

	}

	/**
	 * Returns the export lines for a given category
	 * @param cat The category related to the export lines
	 * @return The export lines for the given category
	 */
	public ArrayList<String> getExportLines(Category cat) {
		return exportLines.get(cat);
	}

	/**
	 * Counts the number of lines that our script can process for this Deck's associated import File, used in conjunction with GUI elements
	 * @return the number of lines our script can process for this Deck File
	 */
	public int initFileLineCount() {
		String PATH = deckFile.getPath();

		int count = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(PATH));
			String line = "";

			try {
				while ((line = br.readLine()) != null) {
					if (line.length() > 0) {
						count += 1;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException occured trying to open missing file named: " + getName());
			e.printStackTrace();
		}

		return count;
	}
	
	/**
	 * Grab the associated File for this deck
	 * @return the associated File for this Deck
	 */
	public File getFile()
	{
	    return deckFile;
	}
	
	/**
	 * Clears all significant information in the Deck, as if it had just been created
	 */
	public void clear()
	{
	    //gui_lineCount           = 0;
        gui_lineCountProgress   = 0;
        
        setFileProgress(gui_lineCountProgress + " / " + gui_lineCount);
        
        gui_deckTableProgress   = getFileProgressString();
        gui_deckTableStatus     = "Queued";
        isCompleat              = false;
        
	    exportLines = new HashMap<Category, ArrayList<String>>();

        BoardProfile mainboard = new BoardProfile();
        BoardProfile sideboard = new BoardProfile();

        boards.put(BoardType.MAINBOARD, mainboard);
        boards.put(BoardType.SIDEBOARD, sideboard);

        initCategoryLines();
        gui_lineCount = initFileLineCount();
	}
}
