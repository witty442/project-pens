package com.pens.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestEncDec {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new SimpleDateFormat("yyyy", new Locale("th", "TH")).format(new Date()));
	}

}
