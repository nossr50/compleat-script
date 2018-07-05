package compleat.datatypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.magicthegathering.javasdk.resource.Card;

public class Guide {
    
    private HashMap<String, List<Card>> parsedStrings;
    private File file;
    
    private ArrayList<String> rawFileContents = new ArrayList<String>();

    public Guide(File guideFile) {
        file = guideFile;
        Init();
    }
    
    private void Init()
    {
        String PATH = file.getPath();

        try {
            BufferedReader br = new BufferedReader(new FileReader(PATH));
            String line = "";

            try {
                while ((line = br.readLine()) != null) {
                    rawFileContents.add(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException occured trying to open missing file named: " + file.getName());
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> getRawContents()
    {
        return rawFileContents;
    }
}
