package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM", Locale.US);
		String today = df.format(new Date());
	    System.out.println("today:"+today);
	}

}
