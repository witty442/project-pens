package com.pens.util;

import antlr.StringUtils;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
