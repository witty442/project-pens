package com.pens.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class Test {
	public static void main(String[] args) {
		 String temp ="xx.jpg";
        System.out.println(temp.substring(temp.indexOf("."),temp.length()));
	}
	
	public static void main_2(String[] args) {
		 String ddmmyyyy ="PD : PD ∫√‘…—∑ ‡Õ .Õ“√Ï. ´ÿª‡ªÕ√Ï¡“√Ï∑ ®”°—¥(‡æ™√∫Ÿ√≥Ï)";
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
