package compleat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.magicthegathering.javasdk.resource.Card;

import javax.swing.JOptionPane;

import compleat.datatypes.CardRarityType;

public class Converter {

	//honestly I don't remember shit about Java so ignore anything crazy here
	static void Convert(final String impDir, final String expDir)
	{
		//Establish directories
		makedir(impDir); 
		makedir(expDir);
		
		
		//Read through and convert
		readFiles(impDir, expDir);
		//String herp = GetCard("The Scarab God");
	}
	
	static void makedir (String impDir)
	{
		String PATH = impDir + File.separator; //System specific directory shit
	    //String directoryName = PATH.concat(this.getClassName());

	    File directory = new File(PATH);
	    if (! directory.exists()){
	    	//JOptionPane.showMessageDialog(null, "Directory made");
	        directory.mkdirs();
	    } else
	    {
	    	//JOptionPane.showMessageDialog(null, "dir exists");
	    }
	    System.out.println(impDir);
	}
	
	static String readFiles (String impDir, String expDir)
	{
		File dir = new File(impDir + File.separator);
		
		File[] files = dir.listFiles();
		
		//Convert each one
		for (File curFile : files )
		{
			writeFile(curFile.getName(), impDir, expDir);
		}
		
		if(files.length == 0) {
			JOptionPane.showMessageDialog(null, "Directories are made, add the files to the import directory and run the application again!");
		}
		
		return null;
	}
	
	static void writeFile(String fileName, String impDir, String expDir)
	{
		try {
			
			String PATH = impDir + File.separator + fileName;
			
			BufferedReader br = new BufferedReader(new FileReader(PATH));
			BufferedWriter bw = null;
			FileWriter fw = null;
			StringBuilder sb = new StringBuilder();
			
			//We're going to organize this for Gravez
			ArrayList<String> creatureList 		= new ArrayList<String>();
			ArrayList<String> spellList 		= new ArrayList<String>();
			ArrayList<String> planeswalkerList 	= new ArrayList<String>();
			ArrayList<String> landsList 		= new ArrayList<String>();
			ArrayList<String> sideboardList 	= new ArrayList<String>();
			ArrayList<String> otherList 		= new ArrayList<String>();
			
			boolean areWeSideboard = false;
			boolean firstEmptyLine = false;
			
			int rareLandCounter = 0;
			
			
			
			//Go through each line and convert it
			try {
			    
			    String line = "";
			    
			    //Rarity tracker
			    HashMap<CardRarityType, Integer> rarityMap = new HashMap<CardRarityType, Integer>();
			    

			    while ((line = br.readLine()) != null) {
			    	
			    	if(line.length() == 0) {
			    		//If we hit an empty line it probably means we are hitting the blank line before the sideboard
			    		
			    		if(firstEmptyLine)
			    		{
			    			sb.append(System.lineSeparator());
			    		} else {
			    			firstEmptyLine = !firstEmptyLine; //This is just to get rid of the empty line at the start of the file
			    		}
			    		
			    		areWeSideboard = true;
			    		continue;
			    	}
			    	
			    	//Afterwards
			    	//3 <a class="simple" href="https://deckbox.org/mtg/Angel of Invention">Angel of Invention</a><br>
			    	
			    	String number = "";
			    	String convertedStr = "";
			    	String cardName = "";
			    	
			    	int cardNameStartPos = 0;
			    	
			    	char[] lineChars = line.toCharArray();
			    	
			    	//Get the numbers before parsing the rest of the string
			    	for(int x = 0; x < lineChars.length; x++)
			    	{
			    		String curLetter = Character.toString(lineChars[x]); //current character
			    		
			    		try {
			    			Integer.parseInt(curLetter);
			    			
			    			//If we get this far its a number
			    			number += curLetter;
			    			
			    		} catch ( NumberFormatException nme ) {
			    			//Once we run out of numbers mark the first letter
			    			cardNameStartPos = x+1; //Since the first character that isn't a number is always a space we will add 1
			    			break;
			    		}
			    	}
			    	
			    	
			    	
			    	
			    	//Go through each character and grab only what we need
			    	for(int x = cardNameStartPos; x < lineChars.length; x++)
			    	{
			    		char curLetter = lineChars[x]; //current character
			    		
			    		//Example line
				    	//3 Angel of Invention (KLD) 4
			    		
			    		if(x >= (lineChars.length - 1))
			    		{
			    			cardName += curLetter;
			    		} else {
			    			if (lineChars[x+1] == '(') {
				    			//if the character after this one is ( we are done building the name
			    				//System.out.println("( detected!");
			    				break;
			    			} else {
			    				cardName += curLetter;
			    			}
			    		}
			    		
			    		
			    	}

			    	//Converted line looks like this
			    	//3 <a class="simple" href="https://deckbox.org/mtg/Angel of Invention">Angel of Invention</a><br>
			    	
			    	convertedStr = number + " <a class=\"simple\" href=\"https://deckbox.org/mtg/" + cardName + "\">" + cardName + "</a><br>";
			    	
			    	Card card = Manager.getCard(cardName);
			    	CardType ct = getSimpleCardType(card);
			    	
			    	//Check rarity from the hashmap
			    	CardRarityType crt = GetRarity(cardName);

			    	
			    	try {
			    		int cardCount = Integer.parseInt(number);
			    		
			    		if(rarityMap.get(crt) == null)
			    		{
			    			rarityMap.put(crt, cardCount);
			    		} else {
			    			rarityMap.put(crt, rarityMap.get(crt)+cardCount);
			    		}
			    		
			    		if(ct == CardType.LAND && crt == CardRarityType.RARE)
				    	{
				    		rareLandCounter+=cardCount;
				    	}
			    	} catch (NumberFormatException nfe)
			    	{
			    		System.out.println("Failed to convert String to number...");
			    	}
			    	
			    	
			    	
			    	//Determine what type of card it is and put it in the appropriate list
			    	if(areWeSideboard)
			    	{
			    		sideboardList.add(convertedStr);
			    	} else
			    	{
			    		switch (ct) {
				    	case CREATURE:
				    		creatureList.add(convertedStr);
				    		break;
				    	case SPELL:
				    		spellList.add(convertedStr);
				    		break;
				    	case PLANESWALKER:
				    		planeswalkerList.add(convertedStr);
				    		break;
				    	case LAND:
				    		landsList.add(convertedStr);
				    		break;
				    	case OTHER:
				    		otherList.add(convertedStr);
				    		break;
				    	default:
				    		otherList.add(convertedStr);
				    		break;
				    	}
			    	}
			    	
			    }
			    
			    if(!landsList.isEmpty()) { //Like this would ever be empty...
				    sb.append("Lands" + System.lineSeparator());
				    WriteEntries(sb, landsList);
			    }
			    
			    //Now write everything into the file in categories
			    if(!creatureList.isEmpty()) {
				    sb.append("Creatures" + System.lineSeparator());
				    WriteEntries(sb, creatureList);
			    }
			    
			    if(!spellList.isEmpty()) {
				    sb.append("Spells" + System.lineSeparator());
				    WriteEntries(sb, spellList);
			    }
			    
			    if(!planeswalkerList.isEmpty()) {
				    sb.append("Planeswalkers" + System.lineSeparator());
				    WriteEntries(sb, planeswalkerList);
			    }
			    
			    if(!sideboardList.isEmpty()) {
			    	sb.append("Sideboard" + System.lineSeparator());
			    	WriteEntries(sb, sideboardList);
			    }
			    
			    if(!otherList.isEmpty()) {
			    	sb.append("Other (Contact me if you see this Gravez, this category only pops up if a card isn't categorized properly)" + System.lineSeparator());
				    WriteEntries(sb, otherList);
			    }
			    
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
			    
				
				//Making sure Gravez never forgets me Kappa Keepo
				sb.append("////////////////////////////////////////////////");
				sb.append(System.lineSeparator());
			    sb.append("Conversion Generated on : " + dateFormat.format(date));
			    sb.append(System.lineSeparator());
			    sb.append("Script by : nossr50 <3");
			    sb.append(System.lineSeparator());
			    
			    sb.append("--DECK INFO--");
			    
			    //sb.append(System.lineSeparator());
			    
			    //sb.append("Rarity Count:"+System.lineSeparator());
			    
			    sb.append(System.lineSeparator());
			    sb.append(System.lineSeparator());
			    sb.append("[LANDS]"+System.lineSeparator());
			    sb.append("Basic Lands: "+rarityMap.get(CardRarityType.BASIC_LAND)+System.lineSeparator());
			    sb.append("Rare Lands: "+rareLandCounter+System.lineSeparator());
			    sb.append(System.lineSeparator());
			    
			    sb.append(System.lineSeparator());
			    sb.append("[TOTAL RARITY COUNT]"+System.lineSeparator());
			    sb.append("Common: "+rarityMap.get(CardRarityType.COMMON)+System.lineSeparator());
			    sb.append("Uncommon: "+rarityMap.get(CardRarityType.UNCOMMON)+System.lineSeparator());
			    sb.append("Rare: "+rarityMap.get(CardRarityType.RARE)+System.lineSeparator());
			    sb.append("Mythic Rare: "+rarityMap.get(CardRarityType.MYTHIC_RARE)+System.lineSeparator());
			    
			    sb.append(System.lineSeparator());
			    
			    sb.append("////////////////////////////////////////////////");
			    
			} finally {
			    br.close();
			}
			
			System.out.println("Writing file");
			
			File curFile = new File(expDir + File.separator + fileName); //export destination
			
			fw = new FileWriter(curFile.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			if (!curFile.exists()) {
				curFile.createNewFile();
			} else {
				//Clear the contents of the file
				PrintWriter pw = new PrintWriter(expDir + File.separator + fileName);
				pw.close();
			}

			bw.write(sb.toString());
			
			bw.close();
			fw.close();

			

		} catch (IOException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Something broke, tell nossr");

		}
	}
	
	public static CardType getSimpleCardType(Card card)
	{
		
		/*
		 * In MTG cards have multiple types, we choose to ignore that and classify them more simply
		 */
		
		
		if(card == null)
		{
			System.out.println("Results are null!!");
			return CardType.OTHER;
		}
		
		//First check if the card is a creature, because technically we have artifact creatures
		for(String type : card.getTypes())
		{
			System.out.println("Type: "+type);
			if(getCardType(type) == CardType.CREATURE) {
				return CardType.CREATURE;
			}
		}
		
		//If it's not a creature, we can check the other types
		for(String type : card.getTypes())
		{
			System.out.println("Type: "+type);
			//We're only going to return the first type that doesn't fall into our other category
			if(getCardType(type) != CardType.OTHER) {
				return getCardType(type);
			}
		}
		
		return CardType.OTHER; //if we make it this far then classify it as other
	}
	
	public static CardType getCardType(String s)
	{
		switch(s.toUpperCase()) {
		case "LAND":
			return CardType.LAND;
		case "CREATURE":
			return CardType.CREATURE;
		case "PLANESWALKER":
			return CardType.PLANESWALKER;
		case "SPELL":
		case "ENCHANTMENT":
		case "ARTIFACT":
		case "INSTANT":
		case "SORCERY":
			return CardType.SPELL;
		default:
			return CardType.OTHER;
		}
	}
	
	public static void WriteEntries(StringBuilder sb, List<String> sList)
	{
		for(String s : sList)
		{
			sb.append(s);
			sb.append(System.lineSeparator());
		}
		
		//Add a space between categories
		sb.append(System.lineSeparator());
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
			System.out.println("[ Rarity Bugged for (" + c.getName() + ") -- Executing workaround hotfix ]"); //Remove this when the bug gets fixed
			return CardRarityType.GetRarity(c);
		}
	}
	
	public static void AddRarity(int cardCount, CardRarityType crt, int basic_land, int uncommon, int common, int rare, int mythic_rare)
	{
		switch(crt)
		{
		case BASIC_LAND:
			basic_land+=cardCount;
			break;
		case UNCOMMON:
			uncommon+=cardCount;
			break;
		case COMMON:
			common+=cardCount;
			break;
		case RARE:
			rare+=cardCount;
		case MYTHIC_RARE:
			mythic_rare+=cardCount;
		}
	}
	
	
}


