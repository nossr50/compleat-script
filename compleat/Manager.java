package compleat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compleat.datatypes.Deck;
import compleat.debug.Debugger;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;

public class Manager {
	
	//Cache for cards we've already requested to reduce https requests
	private static HashMap<String, Card> cardCache  = new HashMap<String, Card>();
	private static ArrayList<Deck> deckArray 		= new ArrayList<Deck>();
	
	public static Card getCard(String cardName)
	{
		//Special code to make looking up split cards work
		CharSequence splitSequence = "///";
						
		if(cardName.contains(splitSequence)) {
			//If the card is a split card we'll rebuild it with a new name and run that instead
			String correctedName = "";
							
			for(char c : cardName.toCharArray())
			{
				if(c != '/')
				{
					correctedName += c;
				} else
				{
					break;
				}
			}

				
			if(cardCache.get(correctedName) != null)
			{
				return cardCache.get(correctedName);
			} else
			{
				initLookup(correctedName);
				return cardCache.get(correctedName);
			}
		} else {
			if(cardCache.get(cardName) != null)
			{
				return cardCache.get(cardName);
			} else
			{
				initLookup(cardName);
				return cardCache.get(cardName);
			}
		}
				
		
	}
	
	public static ArrayList<Deck> getDecks()
	{
		return deckArray;
	}
	
	public static void addDeck(File deckFile)
	{
		System.out.println("Adding deck: "+deckFile.toString());
		Deck deck = new Deck(deckFile);
		deckArray.add(deck);
	}
	
	static void initLookup(String cardName)
	{
		/*
		 * Look up the card and then add it to local memory
		 */
		System.out.println("[QUERY] " + cardName);
				
		List<Card> queryResults = getCards("name=" + cardName);
					
		if(queryResults == null || queryResults.isEmpty())
		{
			System.out.println("[WARNING] No results found for query!");
		} else {
			addResults(cardName, queryResults);
		}
	}
	
	static void addResults(String cardName, List<Card> queryResults)
	{
		/*
		 * Multiple results for a query can include promotional cards, we do not want promotional cards.
		 */
		int index = 0;
		while(index < queryResults.size())
		{
			Card c = queryResults.get(index);
			//System.out.println("Result Rarity = "+c.getRarity());
			
			if(!c.getRarity().equals("Special"))
			{
				//System.out.println("[CACHE] Adding " + cardName + " to cache ("+queryResults.size()+" Results!) ");
				cardCache.put(cardName, c);
				break;
			}
			
			//System.out.println("Loop count: " +index);
			index++;
		}
	}
	
	public static ArrayList<Card> getCards(String... filters)
	{
		ArrayList<String> curFilters = new ArrayList<String>();
		
		for(String s : filters) {
			curFilters.add(s);
		}
		
		ArrayList<Card> cards = (ArrayList<Card>) CardAPI.getAllCards(curFilters);
		
		if(cards == null || cards.isEmpty()) {
			Debugger.Log("QUERY", "No results", curFilters);
			return null;
		} else {
			//NOTE: We should only be getting one result for most cards
			if(cards.size() > 1) {
				Debugger.Log("QUERY", "More than 1 result", curFilters);
			}
			
			return cards;
		}
	}
	
	public static boolean hasType(Card card, String type)
	{
		for(String s : card.getTypes())
		{
			if(s == type) {
				return true;
			}
		}
		return false;
	}
}
