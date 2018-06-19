package compleat.datatypes;

import java.util.Arrays;
import java.util.List;

import compleat.Converter;
import compleat.Manager;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;

public enum CardRarityType {
	BASIC_LAND("Basic Land"),
	COMMON("Common"),
	UNCOMMON("Uncommon"),
	RARE("Rare"),
	MYTHIC_RARE("Mythic Rare");
	
	
	private String name; //Giving our enums names
	
	CardRarityType(String s){ name = s; } //constructor
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public static CardRarityType GetRarity(Card card)
	{
		System.out.println("## [RARITY QUERY (" + card.getName() + ") ] ##");
		
		for(CardRarityType crt : CardRarityType.values())
		{
			//Reset filters query results every loop
			List<String> 	filters 		= Arrays.asList("name=" + card.getName(), "rarity=" + crt.toString()); //Grab cards by name in English
			List<Card> 		queryResults 	= CardAPI.getAllCards(filters); //Get all cards matching a filter
			
			if(queryResults != null && queryResults.size() > 0) {
				System.out.println("Found Match for " + crt.toString());
				Manager.getCard(card.getName()).setRarity(crt.toString()); //Fix the rarity in our local cache
				return crt;
			}
		}
		
		return null;
	}
}
