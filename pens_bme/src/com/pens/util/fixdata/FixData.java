package com.pens.util.fixdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.NumberToolsUtil;
import com.pens.util.Utils;

public class FixData {
  
	public static void main(String[] a){
		try{
		fixDataTableSales_FROM_KING();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void fixDataTableSales_FROM_KING() throws Exception{
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement psUpdate = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			String sql = "select * from pensbme_sales_from_king "+
			"where 1=1 ";
			//"and cust_no = '020056-2' "+
			//"and reference ='EK2H10' "+
			//"and sale_date = to_date('31/07/2017','dd/mm/yyyy') "+
			//"and file_name ='ams ศรีวารี  Jul 2017.xls' ";
					
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String sqlUpdate = "update pensbme_sales_from_king set code =? "+
			"where  cust_no = ?"+
			"and reference =?"+
			"and sale_date = ?"+
			"and file_name =?"+
			"and code =?	";
			
			psUpdate = conn.prepareStatement(sqlUpdate);
			
			while(rs.next()){
				
				psUpdate.setString(1, NumberToolsUtil.convertSciToDecimal(rs.getString("code")));
				psUpdate.setString(2, Utils.isNull(rs.getString("cust_no")));
				psUpdate.setString(3, Utils.isNull(rs.getString("reference")));
				psUpdate.setDate(4, rs.getDate("sale_date"));
				psUpdate.setString(5, Utils.isNull(rs.getString("file_name")));
				psUpdate.setString(6, Utils.isNull(rs.getString("code")));
				
				psUpdate.executeUpdate();
			}
			System.out.println("Success ");
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
			ps.close();
			psUpdate.close();
			rs.close();
		}
	}
}
