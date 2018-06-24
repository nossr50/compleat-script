package compleat.datatypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import compleat.Manager;
import io.magicthegathering.javasdk.resource.Card;

public class Deck {
	
	//Container for deck info
	
	private HashMap<BoardType, BoardProfile> boards;
	private HashMap<Category, ArrayList<String>> exportLines;
	
	private File deckFile;
	
	//GUI vars
	private int lineCount 		= 0; //total lines in files to process
	private int lineProgress 	= 0; //count of lines processed so far
	private String fileProgress = "N/A";
	private String fileStatus   = "Idle";
	private boolean isCompleat  = false;
	
	public Deck(File deckFile)
	{
		this.deckFile 	= deckFile;
		
		boards = new HashMap<BoardType, BoardProfile>();
		exportLines = new HashMap<Category, ArrayList<String>>();
		
		BoardProfile mainboard = new BoardProfile();
		BoardProfile sideboard = new BoardProfile();
		
		boards.put(BoardType.MAINBOARD, mainboard);
		boards.put(BoardType.SIDEBOARD, sideboard);
		
		initCategoryLines();
		lineCount = initFileLineCount();
	}
	
	synchronized public void setCompleat()
	{
		fileStatus = "Compleat";
		isCompleat = true;
	}
	
	synchronized public boolean isCompleat()
	{
		return isCompleat;
	}
	
	synchronized public void setFileStatus(String status)
	{
		fileStatus = status;
	}
	
	synchronized public String getFileStatus()
	{
		return fileStatus;
	}
	
	synchronized public void setFileProgress(String progress)
	{
		fileProgress = progress;
	}
	
	synchronized public int getFileProgressInt()
	{
		return lineProgress;
	}
	
	synchronized public void updateProgress()
	{
		lineProgress+=1;
		
		setFileProgress(lineProgress + " / " + lineCount);
	}
	
	synchronized public String getFileProgess()
	{
		return fileProgress;
	}
	
	public int getFileLineCount()
	{
		return lineCount;
	}
	
	public void addCards(String cardName, int numCards, BoardType bt)
	{
		Card card = Manager.getCard(cardName);
		boards.get(bt).addCard(card, numCards);
	}
	
	synchronized public String getName()
	{
		return deckFile.getName();
	}
	
	/**
	 * Add the first lines for each category
	 */
	private void initCategoryLines()
	{
		for(Category cat : Category.values())
		{
			ArrayList<String> newExportLines = new ArrayList<String>();
			String firstLine = cat.toString();
			exportLines.put(cat, newExportLines);
			
			if(cat == Category.RARITY_COUNT)
			{
				continue; //We don't need this
			}
			
			exportLines.get(cat).add(firstLine);
		}
	}
	
	public String getFormattedLine(Card card, BoardType bt)
	{
		int count = boards.get(bt).getCount(card);
		return count + " <a class=\"simple\" href=\"https://deckbox.org/mtg/" + card.getName() + "\">" + card.getName() + "</a><br>";
	}
	
	public void buildExportLines()
	{
		//All cards in the mainboard get sorted
		for(Card card : boards.get(BoardType.MAINBOARD).getCards())
		{
			Category cardCategory = getCategoryType(card);
			//System.out.println("Card ("+card.getName()+") -- Category: "+cardCategory.toString());
			exportLines.get(cardCategory).add(getFormattedLine(card, BoardType.MAINBOARD));
		}
		
		//Cards in the sideboard do not
		for(Card card : boards.get(BoardType.SIDEBOARD).getCards())
		{
			System.out.println("Adding "+card.getName()+" to sideboard category!");
			exportLines.get(Category.SIDEBOARD).add(getFormattedLine(card, BoardType.SIDEBOARD));
		}
		
		
		//Rarity Stats
		exportLines.get(Category.RARITY_COUNT).add("Rares (Mainboard)");
		for(CardRarityType crt : CardRarityType.values()) {
			exportLines.get(Category.RARITY_COUNT).add(crt.toString() + ": " + boards.get(BoardType.MAINBOARD).getRareCount(crt));
		}
		
		exportLines.get(Category.RARITY_COUNT).add(System.lineSeparator());
		
		exportLines.get(Category.RARITY_COUNT).add("Rares (Sideboard)");
		for(CardRarityType crt : CardRarityType.values()) {
			exportLines.get(Category.RARITY_COUNT).add(crt.toString() + ": " + boards.get(BoardType.SIDEBOARD).getRareCount(crt));
		}
		
		exportLines.get(Category.RARITY_COUNT).add(System.lineSeparator());
		
		exportLines.get(Category.RARITY_COUNT).add("Rares (Total)");
		for(CardRarityType crt : CardRarityType.values()) {
			
			int main = boards.get(BoardType.MAINBOARD).getRareCount(crt);
			int side = boards.get(BoardType.SIDEBOARD).getRareCount(crt);
			int sum = main + side;
			
			exportLines.get(Category.RARITY_COUNT).add(crt.toString() + ": " + sum);
		}
		
		//Land info
	}
	
	public Category getCategoryType(Card card)
	{
		//We must check the card for being a creature before assigning it another category
		//This is because artifact creatures are going to be seperated from artifacts
		for(String s : card.getTypes())
		{
			//NOTE: Case sensitive
			if(s.toUpperCase().equals("CREATURE"))
			{
				//System.out.println("CREATURE FOUND");
				return Category.CREATURES;
			}
		}
		
		for(String s : card.getTypes())
		{
			switch(s.toUpperCase()) {
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
	
	public ArrayList<String> getExportLines(Category cat)
	{
		return exportLines.get(cat);
	}
	
	/*
	 * Line count info for our GUI
	 */
	public int initFileLineCount()
	{
		String PATH = deckFile.getPath();
		
		int count = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(PATH));
			String line = "";
			
			try {
				while((line = br.readLine()) != null)
				{
					if(line.length() > 0)
					{
						count+=1;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException occured trying to open missing file named: "+getName());
			e.printStackTrace();
		}
		
		return count;
	}
}
