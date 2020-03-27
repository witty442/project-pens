package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;

import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class Test {
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		try {
			String strExcel = FileUtil.readFile("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\TRANS_LOTUS(SaleOut)\\New Format Sales-Out LOTUS_test.xlsb");
		    logger.debug("strExcel \n"+strExcel);
		} catch (Exception ex) {
		   ex.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * Output 
	    *
	   ***
	  *****
	 *******
	  *****
	   ***
	    *
	 */
	public static void main_xx(String[] args) {
		
		try{
		   for(int i=0;i<=7;i++){
			  
			   if(i<=3){
				   System.out.println("*");
				   for(int j=0;j<=i+1;j++){
					   //System.out.println("m["+(i%j)+"]");
				        System.out.print("*");
				   }		  
			   }else{
				   System.out.println("*");
				   for(int j=8;j>=i+1;j--){
					   //System.out.println("m["+(i%j)+"]");
				        System.out.print("*");
				   }
			   }
		   }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
