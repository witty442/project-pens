package com.isecinc.pens.web.export;

public class Excel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static String appendBlank(int n){
		String r = "";
		String blank =" ";
		for(int i=0;i<n;i++){
			r +=blank;
		}
		return r;
	}

}
