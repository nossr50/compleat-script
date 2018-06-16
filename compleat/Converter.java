package compleat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class Converter {
	
	//honestly I don't remember shit about Java so ignore anything crazy here
	static void Convert(final String impDir, final String expDir)
	{
		//Establish directories
		makedir(impDir); 
		makedir(expDir);
		
		
		//Read through and convert
		readFiles(impDir, expDir);
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
			
			//Go through each line and convert it
			try {
			    
			    String line = "";

			    while ((line = br.readLine()) != null) {
			    	
			    	if(line.length() == 0) {
			    		//If we hit an empty line it probably means we are hitting the blank line before the sideboard
			    		sb.append(System.lineSeparator());
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
			    	sb.append(convertedStr);
			    	sb.append(System.lineSeparator());
			    	
			    	//System.out.println(cardName);
			    }
			    
			    //String everything = sb.toString();
			    
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
}
