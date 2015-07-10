package com.isecinc.pens.web.nissin;

public class NSConstant {
	//O   Open  ,   AB   Cancel   ,  C  Complete  ,  R = Received
	public static String STATUS_OPEN ="O";
	public static String STATUS_CANCEL ="AB";
	public static String STATUS_COMPLETE ="C";

	public static String getDesc(String s){
		if(s.equals(STATUS_OPEN)){
			return "OPEN";
		}else if(s.equals(STATUS_CANCEL)){
			return "CANCEL";
		}else if(s.equals(STATUS_COMPLETE)){
			return "COMPLETE";
		}
		return "";
	}
}
