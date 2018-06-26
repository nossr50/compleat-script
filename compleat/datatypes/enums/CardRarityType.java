package compleat.datatypes.enums;

import compleat.Manager;
import io.magicthegathering.javasdk.resource.Card;

/**
 * ENUM for the rarity of a Card
 * Special is for promotional cards, and we should never be storing promotional cards in our local cache of Cards
 * @author nossr50
 *
 */
public enum CardRarityType {
	BASIC_LAND("Basic Land"),
	COMMON("Common"),
	UNCOMMON("Uncommon"),
	RARE("Rare"),
	MYTHIC_RARE("Mythic Rare"),
	SPECIAL("Special"); //Special == Promotional cards

	private String name; //String used for our override

	CardRarityType(String s){ name = s; } //Constructor for the ENUM

	/**
	 * Returns the name of the card as specified in the ENUMs constructor
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Returns a rarity for a given card based on its getRarity() string
	 * @param cardName The name of the card (Used to pull the card from our local cache)
	 * @return The CardRarityType ENUM for the given card
	 */
	public static CardRarityType GetRarity(String cardName)
	{
		//Grab card from cache
		Card c = Manager.getCard(cardName);

		return GetRarity(c);
	}

	/**
	 * Returns a rarity for a given card based on its getRarity() string
	 * @param card The Card we wish to check rarity for
	 * @return The CardRarityType ENUM for the given card
	 */
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
