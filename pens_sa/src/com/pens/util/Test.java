package com.pens.util;


public class Test {

	public static void main(String[] args) {
		 String ddmmyyyy ="PD : PD บริษัท เอส.อาร์. ซุปเปอร์มาร์ท จำกัด(เพชรบูรณ์)";
         System.out.println("result:"+ddmmyyyy.replaceAll("\\/", ""));
	}

	/**
	 * @param args
	 */
	public static void main_1(String[] args) {
		// TODO Auto-generated method stub
		 //convert yyyy thai to end date
		String mmyyyy ="20190601";
		 String mm = mmyyyy.substring(4,6);
		 String yyyy = mmyyyy.substring(0,4);
		 String dd = mmyyyy.substring(6,8);
		 System.out.println(dd+":"+mm+":"+yyyy);
		// mmyyyy = mm+yyyy;
		 
	}

}
