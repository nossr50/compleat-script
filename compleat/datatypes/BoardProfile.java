package compleat.datatypes;

import java.util.HashMap;
import java.util.Set;

import io.magicthegathering.javasdk.resource.Card;

public class BoardProfile {
	
	int rare_lands		= 0;
	
	private HashMap<Card, Integer> cards;
	private HashMap<CardRarityType, Integer> rarityMap;
	
	BoardProfile()
	{
		cards 				= 	new HashMap<Card, Integer>();
		rarityMap			= 	new HashMap<CardRarityType, Integer>();
	}
	
	public void addCard(Card card, int numCards)
	{
		if(cards.get(card) != null)
		{
			cards.put(card, numCards+cards.get(card));
		} else {
			cards.put(card, numCards);
		}
		
		parseCard(card, numCards);
	}
	
	public void addRares(CardRarityType crt, int count)
	{
		if(rarityMap.get(crt) != null)
		{
			rarityMap.put(crt, count+rarityMap.get(crt));
		} else {
			rarityMap.put(crt, count);
		}
	}
	
	public int getRareCount(CardRarityType crt)
	{
		if(rarityMap.get(crt) != null)
		{
			return rarityMap.get(crt);
		} else
		{
			return 0;
		}
	}
	
	private void parseCard(Card card, int numCards)
	{
		CardRarityType crt 	= CardRarityType.GetRarity(card);
    	
		//Check if its a rare and a land for our Rare Land count
    	if(card.getType().equals("LAND") && crt == CardRarityType.RARE)
	    {
	    	rare_lands+=numCards;
	    }
    	
    	addRares(crt, numCards);
	}
	
	public int getCount(Card card)
	{
		int count = cards.get(card);
		return count;
	}
	
	public Set<Card> getCards()
	{
		return cards.keySet();
	}
}