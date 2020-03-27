package com.pens.util;

import org.apache.log4j.Logger;

import com.isecinc.pens.process.BarcodeGenerate;

public class BarcodeUtils {
	
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			logger.debug("885987000101:"+genBarcode("885987000101"));
			logger.debug("885987000102:"+genBarcode("885987000102"));
			logger.debug("885987000103:"+genBarcode("885987000103"));
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
