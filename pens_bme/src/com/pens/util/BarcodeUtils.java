package com.pens.util;

import org.apache.log4j.Logger;

import com.isecinc.pens.process.BarcodeGenerate;

public class BarcodeUtils {
	
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			System.out.println("885987000110:"+genBarcode("885987000110"));
			//System.out.println("885987000108:"+genBarcode("885987000108"));
			//System.out.println("885987000109:"+genBarcode("885987000109"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
   /** 13 Digit EAN13**/
   /** 911+RunningNo+CheckDitgit(EAN13)->911000000001C**/
   public static String genBarcode(String barcodeInput12Digit) throws Exception{
	   String barcode = "";
	   try{
		   int chkDigitEAN13 = BarcodeGenerate.genBarcode(barcodeInput12Digit);
		   barcode = barcodeInput12Digit+chkDigitEAN13;
	   }catch(Exception e){
		   e.printStackTrace();
		   throw e;
	   }
	  return barcode;
   }
}
