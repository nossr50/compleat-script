package compleat.datatypes;

import compleat.Manager;
import io.magicthegathering.javasdk.resource.Card;

public enum CardRarityType {
	BASIC_LAND("Basic Land"),
	COMMON("Common"),
	UNCOMMON("Uncommon"),
	RARE("Rare"),
	MYTHIC_RARE("Mythic Rare"),
	SPECIAL("Special"); //Special == Promotional cards
	
	
	private String name; //Giving our enums names
	
	CardRarityType(String s){ name = s; } //constructor
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public static CardRarityType GetRarity(String cardName)
	{
		//Grab card from cache
		Card c = Manager.getCard(cardName);
		
		switch(c.getRarity())
		{
		case "Basic Land":
			return CardRarityType.BASIC_LAND;
		case "Uncommon":
			return CardRarityType.UNCOMMON;
		case "Common":
			return CardRarityType.COMMON;
		case "Rare":
			return CardRarityType.RARE;
		case "Mythic Rare":
			return CardRarityType.MYTHIC_RARE;
		default:
			System.out.println("[WARNING] Unexpected rarirty for card named "+cardName);
			return CardRarityType.SPECIAL;
		}
	}
	
	public static CardRarityType GetRarity(Card card)
	{
		switch(card.getRarity())
		{
		case "Basic Land":
			return CardRarityType.BASIC_LAND;
		case "Uncommon":
			return CardRarityType.UNCOMMON;
		case "Common":
			return CardRarityType.COMMON;
		case "Rare":
			return CardRarityType.RARE;
		case "Mythic Rare":
			return CardRarityType.MYTHIC_RARE;
		default:
			System.out.println("[WARNING] Unexpected rarirty for card named "+card.getName());
			return CardRarityType.SPECIAL;
		}
	}
}
