package com.pens.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.isecinc.pens.inf.helper.Utils;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    try{
	       System.out.println("V38163120028,"+Utils.stringValueSpecial(new Double(1608280020000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120037,"+Utils.stringValueSpecial(new Double(1607045880000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120083,"+Utils.stringValueSpecial(new Double(1607305140000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120208,"+Utils.stringValueSpecial(new Double(1608006900000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120222,"+Utils.stringValueSpecial(new Double(1608110040000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120239,"+Utils.stringValueSpecial(new Double(1608267120000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120243,"+Utils.stringValueSpecial(new Double(1612166100000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38163120275,"+Utils.stringValueSpecial(new Double(1608460320000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));
	       System.out.println("V38164010270,"+Utils.stringValueSpecial(new Double(1611564360000.000000).longValue(),Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th));

	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}

}
