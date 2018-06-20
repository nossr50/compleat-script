package compleat.datatypes;

public enum Category {
	
	LANDS("Lands"),
	CREATURES("Creatures"),
	SPELLS("Spells"),
	PLANESWALKERS("Planeswalkers"),
	SIDEBOARD("Sideboard"),
	OTHER("Other"),
	RARITY_COUNT("Rares"),
	LAND_INFO("Land Info");
	
	String name;
	
	Category(String n) { name = n; }
	
	@Override
	public String toString()
	{
		return name;
	}
}
