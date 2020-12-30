package com.pens.bme.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.NumberUtil;
import com.pens.util.Utils;

public class StockBMEUtils {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		Connection conn = null;
		try{
			conn =DBConnection.getInstance().getConnectionApps();
			String qtyCTN = StockBMEUtils.convertStockQty(conn, "781157",  "EA", "CTN", "2");
			
			System.out.println("qtyCTN:"+qtyCTN);
			
           String qtyEA = StockBMEUtils.convertStockQty(conn, "781157", "CTN", "EA", "1");
			
			System.out.println("qtyEA:"+qtyEA);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	 public static String convertStockQty(Connection conn,String productId,String uomFrom,String uomTo,String inputQty) throws Exception{
			PreparedStatement  ps = null;
			ResultSet rs = null;
			String subQtyOut = "";
			try{
				/**
				 *   inv_convert.inv_um_convert(item_id       => 3, --inventory_item_id
	                                  precision     => 5, --decimal
	                                  from_quantity => 15, --qty
	                                  from_unit     => 'CUP', -- uom from
	                                  to_unit       => 'CTN', -- uom to
	                                  from_name     => null,
	                                  to_name       => null)
				 */
				logger.debug("productId["+productId+"]inputQty["+inputQty+"]uomFrom["+uomFrom+"]uomTo["+uomTo+"]");
				StringBuffer sql = new StringBuffer("");
				sql.append("select inv_convert.inv_um_convert(?,?,?,?,?,?,?) as output_qty from dual");
			
				ps = conn.prepareStatement(sql.toString());
				ps.setString(1,productId);
				ps.setInt(2, 5);
				ps.setString(3,inputQty);
				ps.setString(4,uomFrom);
				ps.setString(5,uomTo);
				ps.setString(6,null);
				ps.setString(7,null);
				rs = ps.executeQuery();
				if(rs.next()){
					subQtyOut = rs.getString("output_qty");
				}
				return subQtyOut;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
}
