package com.pens.util;

import java.sql.Connection;

import com.isecinc.pens.model.MOrder;

public class CalcUpdateAmountOrderUtils {

	public static void main(String[] s){
		calcAmountOrder();
	}
	
	public static void calcAmountOrder(){
		Connection conn = null;
		try{
			new MOrder().reCalculateHeadAmountCaseImport(conn, ""+23355);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				
			}catch(Exception ee){}
		}
	}
}
