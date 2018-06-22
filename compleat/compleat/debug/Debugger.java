package compleat.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compleat.datatypes.CardRarityType;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;

public class Debugger {
	
	
	public static void DebugCard(Card card, String source)
	{
		
		//Since the query DB isn't handled by us we need some debug code to see if everything is fine on their end
		System.out.println("## [DEBUG] START - "+source +" (LOCAL CACHE) ## ");
		
		if(card != null)
		{
			if(card.getName() != null)
			{
				System.out.println("Name: "+card.getName());
			} else {
				System.out.println("Name: [[NULL!!!!]]");
			}
			
			if(card.getRarity() != null) {
				System.out.println("Rarity: "+card.getRarity());
			} else {
				System.out.println("Rarity: [[NULL!!!!]]");
			}
			
			if(card.getType() != null) {
				System.out.println("Type: "+card.getType());
			} else {
				System.out.println("Type: [[NULL!!!!]]");
			}
			
			//Subtypes and Supertypes
			if(card.getSubtypes() != null && card.getSubtypes().length > 0) {
				System.out.println("-- SUBTYPES --");
				for(String s : card.getSubtypes() ) {
							System.out.println(s);
				}
			} else {
				System.out.println("-- NO SUBTYPES --");
			}
			
			if(card.getSupertypes() != null && card.getSupertypes().length > 0) {
				System.out.println("-- SUPERTYPES --");
				for(String s : card.getSupertypes() ) {
							System.out.println(s);
				}
			} else {
				System.out.println("-- NO SUPERTYPES --");
			}
			
			//Types
			if(card.getTypes() != null && card.getTypes().length > 0) {
				System.out.println("-- TYPES --");
				for(String s : card.getTypes() ) {
							System.out.println(s);
				}
			} else {
				System.out.println("-- NO TYPES --");
			}
			
			if(card.getText() != null && card.getText().length() > 0)
			{
				System.out.println("-- GET TEXT --");
				System.out.println(card.getText());
				
			}
			
			if(card.getOriginalText() != null && card.getOriginalText().length() > 0) {
				System.out.println("-- ORIGINAL TEXT --");
				System.out.println(card.getOriginalText());
			}
			
			//Lets run a fresh https request
			List<String> filters = Arrays.asList("name=" + card.getName()); //Grab cards by name in English
			List<Card> queryResults = CardAPI.getAllCards(filters); //Get all cards matching a filter
			
			System.out.println("## [DEBUG] START - "+source + " (FRESH HTTPS QUERY) ## ");
			
			if(queryResults.get(0).getRarity() != null) {
				System.out.println("Rarity: "+queryResults.get(0).getRarity());
			} else {
				System.out.println("Rarity: [[NULL!!!!]]");
			}
			
			System.out.println("## [DEBUG] END ## ");
		} else {
			System.out.println("Card is null!");
			System.out.println("## [DEBUG] END ## ");
		}
		
		DebugRarity(card); //Finally try matching the card by rarity
	}
	
	public static void DebugRarity(Card card)
	{
		System.out.println("## [RARITY QUERY (" + card.getName() + ") ] ##");
		boolean foundMatch = false;
		
		for(CardRarityType crt : CardRarityType.values())
		{
			//Reset filters query results every loop
			List<String> 	filters 		= Arrays.asList("name=" + card.getName()); //Grab cards by name in English
			List<Card> 		queryResults 	= CardAPI.getAllCards(filters); //Get all cards matching a filter
			
			//Try finding the rarity
			filters = Arrays.asList("name=" + card.getName(), "rarity=" + crt.toString()); //Grab cards by name in English
			queryResults = CardAPI.getAllCards(filters); //Get all cards matching a filter
			
			if(queryResults != null && queryResults.size() > 0) {
				System.out.println("[OK] - " + crt.toString());
				foundMatch = true;
				System.out.println("Found Match for " + crt.toString());
				break;
			} else {
				System.out.println("[FAIL] - " + crt.toString());
			}
		}
		
		if(!foundMatch)
		{
			System.out.println("Found no matches!");
		}
	}
	
	public static void Log(String source, String reason, ArrayList<String> curFilters)
	{
		boolean argsPassed = false;
		System.out.println("## ["+source+" DEBUG ] - "+reason+" ##");
		
		for(String s : curFilters)
		{
			if(s != null && s.length() > 0)
			{
				argsPassed = true;
				System.out.println(s);
			}
		}
		
		if(argsPassed)
		{
			System.out.println("## ["+source+" DEBUG ] - END ##");
		}
		
	}
}
