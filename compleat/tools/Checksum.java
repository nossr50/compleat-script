package compleat.tools;

import java.io.File;
import java.io.FileInputStream;

import java.nio.file.*;
import java.nio.file.attribute.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;

public class Checksum {

	/*
	 * Credit to Bill the Lizard from StackOverflow
	 */
	public static byte[] createChecksum(String fileName) throws Exception {
		InputStream fis =  new FileInputStream(fileName);

	    byte[] buffer = new byte[1024];
	    MessageDigest complete = MessageDigest.getInstance("MD5");
	    int numRead;

	    do {
	        numRead = fis.read(buffer);
	        if (numRead > 0) {
	            complete.update(buffer, 0, numRead);
	        }
	    } while (numRead != -1);

	    fis.close();
	    return complete.digest();
	}

	public static String getMD5Checksum(String fileName) throws Exception {
	    byte[] b = createChecksum(fileName);
	    String result = "";

	    for (int i=0; i < b.length; i++) {
	        result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	    }
	    return result;
	}
	
	public static void setAttributes(String PATH, String att) throws Exception
	{
		String md5 = getMD5Checksum(PATH);
		
		System.out.println("Adding md5 checksum to file...");
		System.out.println("Checksum value: "+md5);
		
		Path filePath = Paths.get(PATH);
		
		UserDefinedFileAttributeView view = Files.getFileAttributeView(filePath, UserDefinedFileAttributeView.class);
		try {
			view.write("user.mimetype",Charset.defaultCharset().encode(att));
			System.out.println("md5 checksum added!");
		} catch (IOException e) {
			System.out.println("Error adding md5 to file attributes");
			e.printStackTrace();
		}
	}
	
	public static String readAttributes(String PATH)
	{
		File f = new File(PATH);
		if(f.exists() && !f.isDirectory()) { 
			Path path = Paths.get(PATH);
			
			UserDefinedFileAttributeView view =
			        Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
			    String name = "user.mimetype";
			    ByteBuffer buf;
				try {
					buf = ByteBuffer.allocate(view.size(name));
					try {
						view.read(name, buf);
						buf.flip();
					    String value = Charset.defaultCharset().decode(buf).toString();
					    return value;
					} catch (IOException e) {
						System.out.println("[1] ERROR READING MD5 CHECKSUM IN FILE ATTRIBUTES!");
						//e.printStackTrace();
					}
				} catch (IOException e1) {
					System.out.println("[2] ERROR READING MD5 CHECKSUM IN FILE ATTRIBUTES!");
					//e1.printStackTrace();
				}
			
		    // do something
		} else {
			System.out.println("Didn't find an export file named "+PATH);
		}

		return null;
	}
	
	public static boolean compareChecksum(File fileA, File fileB) throws Exception
	{
		if(fileA != null || fileB != null)
		{
			String md5_a = getMD5Checksum(fileA.getPath());
			String md5_b = getMD5Checksum(fileB.getPath());
			
			return md5_a.equals(md5_b);
		} else {
			return false;
		}
	}
}