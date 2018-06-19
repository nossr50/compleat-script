package compleat.datatypes;

import java.util.ArrayList;

public class Deck {
	
	//Container for deck info
	
	String name 					= 	""; //name of the file
	ArrayList<String> CardTypes 	= 	new ArrayList<String>();
	
	
	//Statistics
	int creatures					= 	0;
	int spells						= 	0;
	int artifacts					=	0;
	int lands						=	0;
	int avgCMC						=	0;
	int planeswalkers				=	0;
	int legendaries					=	0;
	
	
	
	int rareCount 					= 	0;
	
	public Deck(String deckName)
	{
		setName(deckName);
	}
	
	private void setName(String deckName)
	{
		name = deckName;
	}
}
