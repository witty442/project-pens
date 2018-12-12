package com.pens.test;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class Test {
	public static Logger logger = Logger.getLogger("PENS");
	public static void main(String[] args) {
		try{
		  BigDecimal priceIncludeVat = new BigDecimal(690.86);
		 // priceIncludeVat = priceIncludeVat.setScale(2,BigDecimal.ROUND_HALF_UP);
		  System.out.println("before priceIncludeVat:"+priceIncludeVat);
		  
		  BigDecimal priceExcludeVat = new BigDecimal("0");
		  priceExcludeVat = priceIncludeVat.divide(new BigDecimal(1.07),BigDecimal.ROUND_HALF_UP);
		  priceExcludeVat = priceExcludeVat.setScale(2,BigDecimal.ROUND_HALF_UP);
		  System.out.println("before priceExcludeVat:"+priceExcludeVat);
		  
		  //vat = vat.setScale(0, BigDecimal.ROUND_UP);
		 // System.out.println("after vat:"+vat);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main_1(String[] args) {
		// TODO Auto-generated method stub
		/*SimpleDateFormat df = new SimpleDateFormat("yyyyMM", Locale.US);
		String today = df.format(new Date());
	    System.out.println("today:"+today);*/
		
		String addressDesc ="กรุงเทพมหานคร 173 ถนนดินสอ แขวงเสาชิงช้า เขตพระนคร กรุงเทพฯ 10200";
		//System.out.println("ss:"+str.indexOf("ขตพระนคร"));
		//System.out.println("ss:"+str.substring(0,50)+"]");
		String custAddressArr1 = "";
		String custAddressArr2 = "";
		int fixNewLineLen = 40;
		int findNewLineIndex = 0;
				
		String addressTemp = addressDesc.substring(0,fixNewLineLen);
		System.out.println("addressTemp:"+addressTemp);
		System.out.println("addressTempLen:"+addressTemp.length());
		for(int i=addressTemp.length();i<=addressTemp.length();i--){
            if(i>=1){
				String t = addressTemp.substring(i-1,i);
				System.out.println("i["+i+"]t["+t+"]");
				if(" ".equals(t)){
					findNewLineIndex = i;
					break;
				}
            }
		}//for
		System.out.println("findNewLineIndex:"+findNewLineIndex);
		custAddressArr1 = addressDesc.substring(0,findNewLineIndex);
		custAddressArr2 = addressDesc.substring(findNewLineIndex,addressDesc.length());
		
		System.out.println("custAddressArr1:"+custAddressArr1);
		System.out.println("custAddressArr2:"+custAddressArr2);
	}

}
