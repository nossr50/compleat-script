package com.gmail.nossr50.compleat.datatypes;

/**
 * ENUM used for categorizing the export files contents
 * @see com.gmail.nossr50.compleat.datatypes.Deck#buildExportLines()
 * Mostly used as an array to iterate over when creating the export file during DeckScripts writeFile function
 * @author nossr50
 *
 */
public enum Category {

    /*
     * everything but Lands and Sideboard has another <br> infront
     */
	LANDS("Lands"),
	CREATURES("Creatures"),
	SPELLS("Spells"),
	PLANESWALKERS("Planeswalkers"),
	SIDEBOARD("Sideboard"),
	OTHER("Other"),
	RARITY_COUNT("Rares"),
	LAND_INFO("Land Info");

	String name; //String used in our @Override

	Category(String n) { name = n; } //Constructor for ENUMs

	/**
	 * Returns a string based on the ENUMs constructor
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
