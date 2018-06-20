package compleat.datatypes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import compleat.Manager;
import io.magicthegathering.javasdk.resource.Card;

public class Deck {
	
	//Container for deck info
	
	private String name; //name of the file
	
	private HashMap<BoardType, BoardProfile> boards;
	private HashMap<Category, ArrayList<String>> exportLines;
	
	private File deckFile;
	
	public Deck(File deckFile)
	{
		this.deckFile 	= deckFile;
		this.name 		= deckFile.getName();
		
		boards = new HashMap<BoardType, BoardProfile>();
		exportLines = new HashMap<Category, ArrayList<String>>();
		
		BoardProfile mainboard = new BoardProfile();
		BoardProfile sideboard = new BoardProfile();
		
		boards.put(BoardType.MAINBOARD, mainboard);
		boards.put(BoardType.SIDEBOARD, sideboard);
		
		initCategoryLines();
	}
	
	public void addCards(String cardName, int numCards, BoardType bt)
	{
		Card card = Manager.getCard(cardName);
		boards.get(bt).addCard(card, numCards);
	}
	
	public String getName()
	{
		return deckFile.getName();
	}
	
	/***
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
}
