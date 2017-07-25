package com.isecinc.pens.process.modifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ModifierControl {

	public static String ModifierControl_ID = "2401";
	public static String CALC_C4_ID = "2402";
	public static String METHOD_PROMOTION_GOODS_1 ="1";//default
	public static String METHOD_PROMOTION_GOODS_2 ="2";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static String getCalcC4Control(){
		String method = "1";//default
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			ps =conn.prepareStatement("select value from c_reference where ISACTIVE ='Y' and REFERENCE_ID="+CALC_C4_ID);
			rs = ps.executeQuery();
			if(rs.next()){
				method = Utils.isNull(rs.getString("value")).equals("")?"1":Utils.isNull(rs.getString("value"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn !=null){
					conn.close();
					conn = null;
				}
				if(ps != null){
				   ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return method;
	}
	
	public static String getMethodPromotiomGoodsControl(){
		String method = "1";//default
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			ps =conn.prepareStatement("select value from c_reference where ISACTIVE ='Y' and REFERENCE_ID="+ModifierControl_ID);
			rs = ps.executeQuery();
			if(rs.next()){
				method = Utils.isNull(rs.getString("value")).equals("")?METHOD_PROMOTION_GOODS_1:Utils.isNull(rs.getString("value"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn !=null){
					conn.close();
					conn = null;
				}
				if(ps != null){
				   ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return method;
	}
	

}
