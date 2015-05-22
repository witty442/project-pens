package com.isecinc.pens.web.rt;

public class RTConstant {
	//O   Open  ,   AB   Cancel   ,  C  Complete  ,  R = Received
	public static String STATUS_OPEN ="O";
	public static String STATUS_CANCEL ="AB";
	public static String STATUS_COOMFIRM ="C";
	public static String STATUS_RECEIVED ="R";

	public static String getDesc(String s){
		if(s.equals(STATUS_OPEN)){
			return "OPEN";
		}else if(s.equals(STATUS_CANCEL)){
			return "CANCEL";
		}else if(s.equals(STATUS_COOMFIRM)){
			return "CONFIRM";
		}else if(s.equals(STATUS_RECEIVED)){
			return "RECEIVCED";
		}
		return "";
	}
}
