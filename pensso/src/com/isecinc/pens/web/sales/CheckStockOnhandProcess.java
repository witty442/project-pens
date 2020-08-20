package com.isecinc.pens.web.sales;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.Utils;

public class CheckStockOnhandProcess {
	public static Logger logger = Logger.getLogger("PENS");

	public static void main(String[] args) {
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			//381
			//754189	381092
			checkStockOnhandItemProc(conn,"381038","754185","CTN","CUP");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception ee){}
		}
	}
	

	public static String[] checkStockOnhandItemProc(String productCode,String productId,String uomFrom,String uomTo) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return checkStockOnhandItemProc(conn,productCode, productId, uomFrom, uomTo);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public static String[] checkStockOnhandItemProc(Connection conn,String productCode,String productId,String uomFrom,String uomTo) throws Exception{
		CallableStatement  cs = null;
		String qty ="";
		String ctnQtyIn = "";
		String subQtyIn = "";
		String[] qtyArr = new String[3];
		StringBuffer sql = new StringBuffer("");
		try{
		
			sql = new StringBuffer("");
			/**
			 *   xxpens_om_sales_online_pkg.check_onhand(p_organization_id => 226,
                                          p_subinventory    => 'Z001',
                                          p_item_id         => 815171,
                                          x_qoh             => x_qoh, --quantity on-hand
                                          x_qr              => x_qr, --quantity reserved
                                          x_atr             => x_atr); --available to reserve
			 */
			sql.append("{ call  xxpens_om_sales_online_pkg.check_onhand(?,?,?,?,?,?) }");
			cs = conn.prepareCall(sql.toString());
			cs.setInt(1,226);
			cs.setString(2, "Z001");
			cs.setInt(3,Utils.convertStrToInt(productId));
			cs.registerOutParameter(4, java.sql.Types.NUMERIC);
            cs.registerOutParameter(5, java.sql.Types.NUMERIC);
            cs.registerOutParameter(6, java.sql.Types.NUMERIC);
            
			cs.executeUpdate();
			qty = String.valueOf(cs.getDouble(6));
			qtyArr[0] = qty;//1.8 input
			if(qty.indexOf(".") != -1){
			   ctnQtyIn = qty.substring(0,qty.indexOf("."));
			   subQtyIn = qty.substring(qty.indexOf("."),qty.length());
			}else{
			   ctnQtyIn = qty;
			}
			qtyArr[1] = ctnQtyIn;
			qtyArr[2] = "";
			if( !Utils.isNull(uomTo).equals("")){
			  //convert to Uom2
			   subQtyIn = convertToSubQty(conn,productId,uomFrom,uomTo,subQtyIn);
			   qtyArr[2] = subQtyIn;
			}
			logger.debug("productCode["+productCode+"]productId["+productId+"]qty:"+qty+",ctnQtyIn:"+ctnQtyIn+",subQtyIn:"+subQtyIn);

			return qtyArr;
		}catch(Exception e){
			throw e;
		}finally{
			if(cs != null){
				cs.close();cs=null;
			}
			
		}
	}
	public static String[] checkStockItemProc_NEW(Connection conn,String productCode,String productId,String uomFrom,String uomTo) throws Exception{
		CallableStatement  ps = null;
		ResultSet rs = null;
		String qty ="";
		String ctnQtyIn = "";
		String subQtyIn = "";
		String[] qtyArr = new String[3];
		StringBuffer sql = new StringBuffer("");
		try{
			clear_quantity_cache_proc(conn);
			
			sql = new StringBuffer("");
			sql.append("{ call  inv_quantity_tree_pub.query_quantities(?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?,? ,?,?,?,? ,?) }");
			ps = conn.prepareCall(sql.toString());
			ps.setDouble(1, 1.0);
			ps.registerOutParameter(2, java.sql.Types.VARCHAR);
            ps.registerOutParameter(3, java.sql.Types.NUMERIC);
            ps.registerOutParameter(4, java.sql.Types.VARCHAR);
            
            ps.setInt(5,226);
            ps.setInt(6,Utils.convertStrToInt(productId));
            ps.setInt(7, 2);//p_tree_mode
            ps.setBoolean(8, false);
            ps.setBoolean(9, false);
            ps.setBoolean(10, false);
            
            ps.setString(11, null);//p_revision
            ps.setString(12, null);//p_lot_number
            ps.setTimestamp(13, null);//p_lot_expiration_date
            ps.setString(14, "Z001");//p_subinventory_code
            ps.setInt(15, java.sql.Types.NULL);//p_locator_id
            
        	ps.registerOutParameter(16, java.sql.Types.NUMERIC);
            ps.registerOutParameter(17, java.sql.Types.NUMERIC);
            ps.registerOutParameter(18, java.sql.Types.NUMERIC);
        	ps.registerOutParameter(19, java.sql.Types.NUMERIC);
            ps.registerOutParameter(20, java.sql.Types.NUMERIC);
            ps.registerOutParameter(21, java.sql.Types.NUMERIC);
            
			ps.executeUpdate();
			
			qty = String.valueOf(ps.getDouble(21));
		
			qtyArr[0] = qty;//1.8 input
			if(qty.indexOf(".") != -1){
			   ctnQtyIn = qty.substring(0,qty.indexOf("."));
			   subQtyIn = qty.substring(qty.indexOf("."),qty.length());
			}else{
			   ctnQtyIn = qty;
			}
			qtyArr[1] = ctnQtyIn;
			qtyArr[2] = "";
			if( !Utils.isNull(uomTo).equals("")){
			  //convert to Uom2
			   subQtyIn = convertToSubQty(conn,productId,uomFrom,uomTo,subQtyIn);
			   qtyArr[2] = subQtyIn;
			}
			logger.debug("productCode["+productCode+"]productId["+productId+"]qty:"+qty+",ctnQtyIn:"+ctnQtyIn+",subQtyIn:"+subQtyIn);
			
			//conn.commit();
			return qtyArr;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static boolean clear_quantity_cache_proc(Connection conn) throws Exception{
		boolean r = true;
		CallableStatement  cs = null;
		StringBuffer sql = new StringBuffer("");
		logger.debug("clear_quantity_cache_proc");
		try{
			sql.append("{ call inv_quantity_tree_pub.clear_quantity_cache() }");
			cs = conn.prepareCall(sql.toString());
			cs.execute();
			conn.commit();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			cs.close();
		}
	}
	
	public static String convertToSubQty(Connection conn,String productId,String uomFrom,String uomTo,String subQtyIn) throws Exception{
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
			StringBuffer sql = new StringBuffer("");
			sql.append("select inv_convert.inv_um_convert(?,?,?,?,?,?,?) as sub_qty from dual");
		
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,productId);
			ps.setInt(2, 5);
			ps.setString(3,subQtyIn);
			ps.setString(4,uomFrom);
			ps.setString(5,uomTo);
			ps.setString(6,null);
			ps.setString(7,null);
			rs = ps.executeQuery();
			if(rs.next()){
				subQtyOut = rs.getString("sub_qty");
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
