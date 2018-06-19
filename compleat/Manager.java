package compleat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compleat.debug.Debugger;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;

public class Manager {
	
	//Cache for cards we've already requested to reduce https requests
	private static HashMap<String, Card> cardCache = new HashMap<String, Card>();
	
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
	
	static void initLookup(String cardName)
	{
		/*
		 * Look up the card and then add it to local memory
		 */
		System.out.println("[QUERY] " + cardName);
				
		List<Card> queryResults = getCards("name=" + cardName);
					
		if(queryResults == null || queryResults.isEmpty())
		{
			//Its possible the card isn't in standard so reduce our filters
			if(queryResults != null) {
				queryResults.clear(); //Just in case
			}
						
			System.out.println("[QUERY - ALL RESULTS] " + cardName);
			queryResults = getCards("name=" + cardName);
						
			/*
			 * If there are no results this time with a filter only asking for a name something went wrong
			 */
			if(queryResults == null || queryResults.isEmpty()) {
				System.out.println("Failed to find card named "+cardName+" with only the most basic filters!");
					
			} else {
				System.out.println("Card [" + cardName + "] was found but not recognized to be in Standard");
						
			}
		}
			
		/*
		 * finally add all cards to cache
		 */
			
		System.out.println("[CACHE] Adding " + cardName + " to cache");
		cardCache.put(cardName, queryResults.get(0));
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
