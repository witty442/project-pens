package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestDateTime {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));

	}

}
