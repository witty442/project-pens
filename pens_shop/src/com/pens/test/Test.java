package com.pens.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;

public class Test {
	public static Logger logger = Logger.getLogger("PENS");
	public static void main(String[] args) {
		try{
		logger.debug("order.getCreated():"+"17/10/2561 14:22");
		Date createDateTemp = Utils.parse("17/10/2561 14:22", Utils.DD_MM_YYYY_HH_mm_WITH_SLASH,Utils.local_th);
		logger.debug("createDateTemp:"+createDateTemp);
		String createDateChrist = Utils.stringValue(createDateTemp, Utils.DD_MM_YYYY_HH_mm_WITH_SLASH);
		logger.debug("createDateChrist:"+createDateChrist);
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
