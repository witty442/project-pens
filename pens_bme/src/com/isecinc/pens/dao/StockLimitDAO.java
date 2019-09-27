package com.isecinc.pens.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.StoreBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class StockLimitDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	public static void deleteOrderInPage(String orderDateStr,String barcodeInPage,List<StoreBean> storeList) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnection();
			java.util.Date orderDate = DateUtil.parse(orderDateStr, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql = new StringBuffer("");
			sql.append("delete from PENSBME_ORDER  \n");	
			sql.append("where  store_code = ? \n");
			sql.append("and barcode in ("+barcodeInPage+")\n");
			sql.append("and order_date = ?");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			
			if(storeList != null && storeList.size()>0){ 
	        	for(int col=0;col<storeList.size();col++){
	               StoreBean store = (StoreBean)storeList.get(col);
	               
	               if(Utils.isNull(store.getStoreStyle()).equals("storeError")){
						ps.setString(1, store.getStoreCode());
						ps.setDate(2,new java.sql.Date(orderDate.getTime()));
						ps.addBatch();
	               }
	        	}
	        	ps.executeBatch();
			}
	
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
			   conn.close();conn=null;
			}
			
		}
	} 
	
	public static boolean isCustGroupChkCreditLimit(Connection conn,String storeType) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean chk = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select pens_desc2 from pensbi.PENSBME_MST_REFERENCE where reference_code = 'Customer' and pens_value='"+storeType+"'\n");	

			//logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
	
			if(rs.next()){
			  if(Utils.isNull(rs.getString("pens_desc2")).equalsIgnoreCase("CREDIT CHECKED")){
				  chk = true;
			  }
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return chk;
	} 
	
	public static boolean isStoreChkCreditLimit(Connection conn,String storeCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean chk = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select interface_desc from pensbi.PENSBME_MST_REFERENCE where reference_code = 'LimitAmt' and pens_value='"+storeCode+"'\n");	

			//logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
	
			if(rs.next()){
			  if(Utils.isNull(rs.getString("interface_desc")).equalsIgnoreCase("Y")){
				  chk = true;
			  }
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return chk;
	} 
	
	public static void runProcedureStoreLimit(Connection conn,String custGroup,String region) throws Exception{
		CallableStatement cs = null;
		ResultSet rs = null;
		try{
			String sql = "{call PENSBME_STOCK_STORE_LIMIT_PROC(?,?)}";
	
			//logger.debug("sql:"+sql);
			cs = conn.prepareCall(sql);
			cs.setString(1,custGroup);
			if( !Utils.isNull(region).equals("")){
			   cs.setString(2, region);
			}else{
			   cs.setString(2, null);
			}
			
			rs = cs.executeQuery();

		}catch(Exception e){
	     logger.error(e.getMessage(),e);
		}finally{
			if(cs != null){
			   cs.close();cs = null;
			}
		}
	} 
	
	public static boolean isStoreOverCreditLimit(Connection conn,String storeCode,java.util.Date orderDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean isOver = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("\n select store_code ,limit_amt ");
			sql.append("\n ,(select nvl(sum(o.qty*o.whole_price_bf),0) from pensbme_order o");
			sql.append("\n  where o.store_code = SL.store_code ");
			sql.append("\n  and o.order_date = ? ");
			sql.append("\n  group by store_code ) as order_amt");
            sql.append("\n from PENSBI.PENSBME_STOCK_LIMIT SL");	
			sql.append("\n where store_code ='"+storeCode+"'");
	
			//logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			rs = ps.executeQuery();
	
			if(rs.next()){
				double limit_amt = rs.getDouble("limit_amt");
				double order_amt = rs.getDouble("order_amt");
				if(order_amt > limit_amt){
					isOver = true;
				}
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return isOver;
	} 
	
	
	public static Map<String,String> getLimitDAOMap(Connection conn,String storeType,String storeCodeArray) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,String> mapLimitByStoreMap = new HashMap<String,String>();
		String keymap = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select store_code ,limit_amt from PENSBI.PENSBME_STOCK_LIMIT \n");	
			sql.append("where  \n");
			sql.append(" store_code LIKE '"+storeType+"%'");
			sql.append(" and store_code in("+storeCodeArray+")");
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
	
			while(rs.next()){
				keymap = Utils.isNull(rs.getString("store_code"));
				mapLimitByStoreMap.put(keymap,rs.getString("limit_amt"));
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return mapLimitByStoreMap;
	} 
	
	@Deprecated
	public static double getLimitDAO(Connection conn,String storeCode,String barcode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double limitAmt= 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select limit_amt from PENSBI.PENSBME_STOCK_LIMIT \n");	
			sql.append("where 1=1  \n");
			sql.append(" and store_code ='"+storeCode+"' \n");
			sql.append(" and barcode ='"+barcode+"'");
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				limitAmt = rs.getDouble("limit_amt");
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return limitAmt;
	} 
	
}
