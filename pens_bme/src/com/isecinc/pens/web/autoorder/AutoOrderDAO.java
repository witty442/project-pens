package com.isecinc.pens.web.autoorder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class AutoOrderDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	/**
	 *  -1 not found
	 *   0 is confirmed
	 *   1 is can confirm
	 * @param conn
	 * @param orderDate
	 * @return
	 * @throws Exception
	 */
	public static String canConfirmOrderRepToOder(Connection conn,String orderDate,String storeCode) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String r = "-1";
		StringBuffer sql = new StringBuffer("");
		try{
			//convert budishDate to christDate
			Date orderDateObj = DateUtil.parse(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String orderDateStr = DateUtil.stringValue(orderDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append(" select distinct store_code,status from PENSBI.BME_ORDER_REP where 1=1 \n" );
			sql.append(" and order_date = to_date('"+orderDateStr+"','dd/mm/yyyy')");
			sql.append(" and store_code ='"+storeCode+"'"); 
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(Utils.isNull(rs.getString("status")).equalsIgnoreCase("confirm")){
					r = "0";
				}else{
					r = "1";
				}
			}
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
	}
	public static boolean isCanGenStockOnhandTempRep(String storeCode) throws Exception{
	    Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean can = false;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append(" select count(*) as c from PENSBI.BME_CONFIG_REP where STORE_CODE ='"+storeCode+"' \n" );
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c")>0){
					can = true;
				}
			}
			return can;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
	}
	public static boolean isOrderIsGenerated(String storeCode,String orderDate) throws Exception{
	    Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean exist = false;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnection();
			//convert budishDate to christDate
			Date orderDateObj = DateUtil.parse(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String orderDateStr = DateUtil.stringValue(orderDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append(" select count(*) as c from PENSBI.BME_ORDER_REP where STORE_CODE ='"+storeCode+"' \n" );
			sql.append(" and order_date = to_date('"+orderDateStr+"','dd/mm/yyyy')");
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c")>0){
					exist = true;
				}
			}
			return exist;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
	}
	public static String storeOrdeRepNoConfirm(String orderDate) throws Exception{
	    Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String storeCode = "";
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnection();
			//convert budishDate to christDate
			Date orderDateObj = DateUtil.parse(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String orderDateStr = DateUtil.stringValue(orderDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append(" select store_code from PENSBI.BME_ORDER_REP where  \n" );
			sql.append(" order_date = to_date('"+orderDateStr+"','dd/mm/yyyy') \n");
			sql.append(" and status is null or status ='' \n");
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				storeCode = Utils.isNull(rs.getString("store_code"));
			}
			return storeCode;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
	}
	public static boolean isGeneratedStockOnhandTemp(String storeCode) throws Exception{
	    Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean exist = false;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnection();
			sql.append(" select count(*) as c from PENSBI.BME_TEMP_ONHAND_REP where STORE_CODE ='"+storeCode+"' \n" );
			//can stock onhand temp back 1 day
			sql.append(" and ( trunc(create_date) = trunc(sysdate) or trunc(create_date) = trunc(sysdate-1)) \n"); 
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c")>0){
					exist = true;
				}
			}
			return exist;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}

	public static List<AutoOrderBean> searchAutoOrderRep(AutoOrderBean bean) throws Exception {
		StringBuilder sql = new StringBuilder();
		List<AutoOrderBean> dataList = new ArrayList<AutoOrderBean>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Date tDate  = DateUtil.parse(bean.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tDateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select R.* ,W.retail_price_bf ");
			sql.append("\n ,(W.ONHAND_QTY - ");
			//subtract BME ORDER OrderDate =Sysdate
			sql.append("\n  (SELECT NVL(SUM(O.QTY),0) FROM PENSBI.PENSBME_ORDER O ");
			sql.append("\n    WHERE O.ORDER_DATE = to_date('"+tDateStr+"','dd/mm/yyyy') ");
			sql.append("\n    AND R.PENS_ITEM = O.ITEM ) ");
			sql.append("\n  )AS WACOAL_ONHAND_QTY ");
			
			sql.append("\n from PENSBI.BME_ORDER_REP R,PENSBI.PENSBME_ONHAND_BME W ");
			sql.append("\n where R.pens_item = W.pens_item ");
			sql.append("\n and R.store_code ='"+bean.getStoreCode()+"'");
			sql.append("\n and R.ORDER_DATE = to_date('"+tDateStr+"','dd/mm/yyyy')");
			sql.append("\n and W.onhand_qty <> 0");
			sql.append("\n order by R.STORE_CODE,R.GROUP_CODE ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				AutoOrderBean item = new AutoOrderBean();
				item.setStoreCode(rs.getString("store_code"));
				item.setOrderDate(bean.getOrderDate());
				item.setStoreType(rs.getString("store_type"));
				item.setStatus(Utils.isNull(rs.getString("status")));
				item.setGroupCode(rs.getString("group_code"));
				item.setSizeColor(item.getGroupCode().substring(item.getGroupCode().length()-4,item.getGroupCode().length()));
				item.setPensItem(rs.getString("pens_item"));
		        item.setWacoalOnhandQty(Utils.decimalFormat(rs.getDouble("wacoal_onhand_qty"),Utils.format_current_no_disgit));
		        item.setRetailPriceBF(Utils.decimalFormat(rs.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
		        item.setShopOnhandQty(Utils.decimalFormat(rs.getDouble("shop_onhand_qty"),Utils.format_current_no_disgit));
		        item.setSalesQty(Utils.decimalFormat(rs.getDouble("sales_qty"),Utils.format_current_no_disgit));
		        item.setBackSalesDay(Utils.decimalFormat(rs.getDouble("backsales_day"),Utils.format_current_no_disgit));
		        item.setDayCover(Utils.decimalFormat(rs.getDouble("day_cover"),Utils.format_current_no_disgit));
		        item.setSalesQtyPerDay(Utils.decimalFormat(rs.getDouble("sales_qty_per_day"),Utils.format_current_no_disgit));
		        item.setStockByCoverDay(Utils.decimalFormat(rs.getDouble("STOCK_BY_COVER_DAY"),Utils.format_current_no_disgit));
		        item.setRecommendCalcQty(Utils.decimalFormat(rs.getDouble("RECOMMAND_CALC_QTY"),Utils.format_current_no_disgit));
		        item.setRecommendQty(Utils.decimalFormat(rs.getDouble("RECOMMAND_QTY"),Utils.format_current_no_disgit));
		        item.setOrderQty(Utils.decimalFormat(rs.getDouble("ORDER_QTY"),Utils.format_current_no_disgit));
		        item.setMinQty(Utils.decimalFormat(rs.getDouble("MIN_QTY"),Utils.format_current_no_disgit));
		        item.setMaxQty(Utils.decimalFormat(rs.getDouble("MAX_QTY"),Utils.format_current_no_disgit));
		        dataList.add(item);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
				ps.close();
				rs.close();
			} catch (Exception e) {}
		}
		return dataList;
	}
	
	public static void updateOrderRep(Connection conn,AutoOrderBean h, List<AutoOrderBean> dataList) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
    	logger.debug("updateOrderRep");
		try{
			sql.append(" UPDATE PENSBI.BME_ORDER_REP \n");
			sql.append(" SET ORDER_QTY =? ,UPDATE_USER =? ,UPDATE_DATE =?  \n" );
			sql.append(" WHERE ORDER_DATE =? AND STORE_CODE =? AND GROUP_CODE =? AND PENS_ITEM =? \n" );

			ps = conn.prepareStatement(sql.toString());
			Date orderDate = DateUtil.parse( h.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			if(dataList != null && dataList.size() >0){
				for(int i=0;i<dataList.size();i++){
					AutoOrderBean o = dataList.get(i);
					
					ps.setBigDecimal(1, new BigDecimal(Utils.convertToInt(o.getOrderQty())));
					ps.setString(2, o.getUserName()+"");
					ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
					ps.setDate(4, new java.sql.Date(orderDate.getTime()));
					ps.setString(5, h.getStoreCode());
					ps.setString(6, o.getGroupCode());
					ps.setString(7, o.getPensItem());
					
					ps.addBatch();
				}//for
				ps.executeBatch();
			}//if
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static void confirmOrderRep(Connection conn,AutoOrderBean h) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
    	logger.debug("confirmOrderRep");
		try{
			sql.append(" UPDATE PENSBI.BME_ORDER_REP \n");
			sql.append(" SET STATUS =? ,UPDATE_USER =? ,UPDATE_DATE =?  \n" );
			sql.append(" WHERE ORDER_DATE =? AND STORE_CODE =? \n" );

			ps = conn.prepareStatement(sql.toString());
			Date orderDate = DateUtil.parse( h.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	
			ps.setString(1, "CONFIRM");
			ps.setString(2, h.getUserName()+"");
			ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setDate(4, new java.sql.Date(orderDate.getTime()));
			ps.setString(5, h.getStoreCode());
			
			ps.executeUpdate();
				
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	/**
	 * Gen OrderRep to PENSBME_ORDER 
	 * @param conn
	 * @param h
	 * @throws Exception
	 */
	public static void genOrderRepToBMEOrder(Connection conn,AutoOrderBean h) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		Order o = null;
		StringBuffer sql = new StringBuffer();
		String orderNo = "";
		Map<String, String> orderNoMap = new HashMap<String, String>();
		try{
			Date orderDate  = DateUtil.parse(h.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String orderDateStr = DateUtil.stringValue(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select O.* ");
			sql.append("\n ,OH.retail_price_bf ,OH.whole_price_bf,OH.barcode");
			sql.append("\n from PENSBI.BME_ORDER_REP O,PENSBI.PENSBME_ONHAND_BME OH ");
			sql.append("\n where O.pens_item = OH.pens_item ");
			sql.append("\n and O.store_code ='"+h.getStoreCode()+"'");
			sql.append("\n and O.ORDER_DATE = to_date('"+orderDateStr+"','dd/mm/yyyy')");
			sql.append("\n and O.order_qty <> 0 ");
			sql.append("\n order by O.pens_item ");
			logger.debug("sql:"+sql);
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				o = new Order();
				o.setOrderDate(h.getOrderDate());
				o.setGroupCode(Utils.isNull(rs.getString("group_code")));
				o.setItem(Utils.isNull(rs.getString("pens_item")));
				o.setQty(Utils.isNull(rs.getString("order_qty")));
				o.setBarcode(Utils.isNull(rs.getString("barcode")));
				o.setRetailPriceBF(Utils.isNull(rs.getString("retail_price_bf")));
				o.setWholePriceBF(Utils.isNull(rs.getString("whole_price_bf")));
				
				o.setExported("N");
				o.setStoreType(Utils.isNull(h.getStoreCode().substring(0,6)));
				o.setStoreCode(Utils.isNull(h.getStoreCode()));
				
				o.setBillType("N");	
				o.setValidFrom("00000000");
	    		o.setValidTo("00000000");
	    		if(orderNoMap.get(o.getStoreCode()) ==null){
            		//Generate new orderNo
	    			orderNo = OrderNoGenerate.genOrderNoKEY(orderDate, Utils.isNull(h.getStoreCode()));
            	    
            	    orderNoMap.put(Utils.isNull(o.getStoreCode()), orderNo); 
            	}else{
            		// Get old orderNo
            		orderNo = orderNoMap.get(o.getStoreCode());
            	}
	    		o.setOrderNo(orderNo);
	    		o.setCreateUser(h.getUserName());
	    		//Insert Or Update
	    		OrderDAO.saveOrderFromGenAuto(conn, o);
			}//for
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void deleteStockOnhandTemp(Connection conn,String storeCode) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" delete from PENSBI.BME_TEMP_ONHAND_REP where 1=1");
			//sql.append(" STORE_CODE ='"+storeCode+"' \n" );
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateMasterConfig(String refCode,String custGroup,String inputQty) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append(" update  PENSBI.PENSBME_MST_REFERENCE ");
			sql.append(" set interface_value ='"+inputQty+"'");
			sql.append(" where pens_value ='"+custGroup+"' \n" );
			sql.append(" and reference_code ='"+refCode+"' \n" );
	        logger.debug("sql:\n"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	public static String getMasterConfig(String refCode,String custGroup) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String value = "";
		Connection conn =null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append(" select interface_value from PENSBI.PENSBME_MST_REFERENCE \n");
			sql.append(" WHERE reference_code = '"+refCode+"' \n");
			sql.append(" AND pens_value = '"+Utils.isNull(custGroup)+"' \n");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				value = rst.getString("interface_value");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e) {}
		}
		return value;
	}
}
