package util.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class CSVHelper {
	
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/** Read Csv*/
	public static void readCsvFile(){
		//File inputFile = new File("D:\\Work_ISEC\\FTP_SERVER\\WACOAL\\Salein20170906 - csv.csv");
		File inputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\temp_gen.csv");
		StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    Scanner scanner = null;
	    InputStream fis = null;
	    String lineStr = "";
		 try {
			  fis = new FileInputStream(inputFile);
			  scanner = new Scanner(fis, "TIS-620");
		      while (scanner.hasNextLine()){
		        //text.append(scanner.nextLine() + NL);
		    	  lineStr = scanner.nextLine();
		    	  System.out.println("BEFORE Line:["+lineStr+"]");
		    	  lineStr = lineStr.replace("	", "|");
		    	  System.out.println("AFTERS Line:["+lineStr+"]");
		    	  
		      }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		  scanner.close();
	    }
		
	}
}
