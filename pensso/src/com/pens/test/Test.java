package com.pens.test;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.NumberToolsUtil;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
		   //testCallProc();
			
			System.out.println(NumberToolsUtil.round(new Double(".99"), 2, BigDecimal.ROUND_HALF_UP));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void testCallProc() throws Exception{
		CallableStatement  ps = null;
		int  c = 1;
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
		
			StringBuffer sql = new StringBuffer("");
			//sql.append("{ call xxpens_om_trip_pkg.update_trip(?,?,?,?,?) } \n");
			
			sql.append("{ call xxpens_om_sales_online_pkg.initial_stock(3) }");
			 
			/*xxpens_om_trip_pkg.update_trip(p_cust_id  in number,
	                p_party_site_id in number,
	                p_trip1         in varchar2,
	                p_trip2         in varchar2,
	                p_trip3         in varchar2)*/
	                
			ps = conn.prepareCall(sql.toString());
			//ps.registerOutParameter(1, Types.VARCHAR);
			//ps.registerOutParameter(2, Types.VARCHAR);
			ps.execute();
			
			//String p1 = ps.getString(1);
			//String p2 = ps.getString(2);
			
			//System.out.println("p1:"+p1);
			//System.out.println("p2:"+p2);
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			conn.close();
		}
	}
}
