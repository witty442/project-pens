package com.isecinc.pens.web.order;

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
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class OrderValidate {
	protected static Logger logger = Logger.getLogger("PENS");
	public static void main(String[] a){
		Order o = new Order();
		o.setOrderDate("20/08/2561");
		
		validateOrder(o);
	}
	public static OrderErrorBean validateOrder(Order o){
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		String key = "";
		OrderErrorBean orderErrorBean = null;
		String storeCodeAllIn = "";
		String storeCode ="";
		List<String> storeList = null;
		try{
			// Get StoreCode Error
			conn = DBConnection.getInstance().getConnectionApps();
			
			// Get StoreCode Error
			List<String> storeErrorList = getStoreErrorList(conn,o);
			if(storeErrorList != null && storeErrorList.size() >0){
				storeList = new ArrayList<String>();
				for(int k=0;k<storeErrorList.size();k++){
					storeCodeAllIn = "'"+storeErrorList.get(k)+"'";
					storeCode = storeErrorList.get(k);
					storeList.add(storeCode);//add all store
					List<OrderErrorBean> storeBarcodeList = getStoreCodeBarcodeList(conn, o.getOrderDate(), storeCodeAllIn);
					List<OrderErrorBean> orderNoList = getOrderNoList(conn, o.getOrderDate(), storeCodeAllIn);
					Map<String ,OrderErrorBean> dataErrorMap = getOrderDetailMap(conn, o.getOrderDate(), storeCodeAllIn);
			
					if(storeBarcodeList != null && storeBarcodeList.size() >0){
						h.append("<table border='1'> \n");
						
						h.append("<tr> \n");
						h.append("<th rowspan='2'>Store Code</th> \n");
						h.append("<th rowspan='2'>Store Name</th> \n");
						h.append("<th rowspan='2'>GroupCode Size/Color</th> \n");
						h.append("<th rowspan='2'>Pens Item</th> \n");
						h.append("<th colspan='"+orderNoList.size()+"'>OrderNo</th> \n");
						h.append("</tr> \n");
						h.append("<tr> \n");
						/** Loop OrderNo List**/
						for(int c=0;c<orderNoList.size();c++){
							h.append("<th>"+orderNoList.get(c).getOrderNo()+"\n");
							 h.append("<input type='radio' name='radio_"+storeCode+"' value='"+orderNoList.get(c).getOrderNo()+"')/></th> \n");
							h.append("</th> \n");
						}
						h.append("</tr> \n");
						
						/** Loop StoreCode ,barcode **/
						
						for(int i=0;i<storeBarcodeList.size();i++){
							OrderErrorBean store = storeBarcodeList.get(i);
							//logger.debug("** StoreCode error:"+store.getStoreCode()+" **");
							h.append("<tr> \n");
							h.append("<td>"+store.getStoreCode()+"</td> \n");
							h.append("<td>"+store.getStoreName()+"</td> \n");
							h.append("<td>"+store.getGroupCodeDesc()+"</td> \n");
							h.append("<td>"+store.getPensItem()+"</td> \n");
							
							///Loop orderNo List for get Qty by Order No
							for(int c=0;c<orderNoList.size();c++){
								OrderErrorBean orderNo = orderNoList.get(c);
								key = store.getStoreCode()+store.getBarcode()+orderNo.getOrderNo();
								if(dataErrorMap.get(key) != null){
								   h.append("<td>"+dataErrorMap.get(key).getQty()+"</td> \n");
								}else{
								   h.append("<td></td> \n");
								}
							}
							h.append("</tr> \n");
						}
						h.append("</table>");
					}//if
				}//for loop Store
				
				//logger.debug("Result \n"+h.toString());
				//FileUtil.writeFile("d:\\temp\\orderError.html", h.toString());
				
				orderErrorBean = new OrderErrorBean();
				orderErrorBean.setResultError(h);
				orderErrorBean.setStoreErrorList(storeList);
			}//if storeList != null
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  if(conn != null){
				conn.close();conn=null;
			  }
			}catch(Exception ee){}
		}
		return orderErrorBean;
	}
	
	private static List<String>  getStoreErrorList(Connection conn,Order o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<String> storeList = new ArrayList<String>();
		Map<String, String> storeErrorMap = new HashMap<String, String>();
		try{
			Date orderDate = DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String orderDateStr = DateUtil.stringValue(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select order_date ,store_type,store_code,group_code,barcode,count(*) ");
			sql.append("from pensbi.pensbme_order \n");
			sql.append("where order_date =to_date('"+orderDateStr+"','dd/mm/yyyy') \n");
			sql.append("group by order_date ,store_type,store_code,group_code,barcode \n");
			sql.append("having count(*)> 1 \n");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while (rst.next()) {
				OrderErrorBean item = new OrderErrorBean();
				item.setStoreCode(rst.getString("store_code"));
				item.setOrderDate(DateUtil.stringValue(rst.getDate("order_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				if(storeErrorMap.get(item.getStoreCode()) == null){
				   //logger.debug("add storeCode:"+item.getStoreCode());
					storeList.add(item.getStoreCode());
				   storeErrorMap.put(item.getStoreCode(), item.getStoreCode());
				}else{
				   storeErrorMap.put(item.getStoreCode(), item.getStoreCode());
				}
			}//while

		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			rst.close(); rst=null;
            ps.close(); ps=null;
		}
		return storeList;
	}
	private static List<OrderErrorBean> getStoreCodeBarcodeList(Connection conn,String orderDateS,String storeCodeAllIn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<OrderErrorBean> barcodeList = new ArrayList<OrderErrorBean>();
		try{
			Date orderDate = DateUtil.parse(orderDateS, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String orderDateStr = DateUtil.stringValue(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select store_code,group_code,barcode,qty ");
			sql.append("\n ,(select max(pens_desc) from pensbi.pensbme_mst_reference m where m.reference_code ='Store'"
					+ "     and m.pens_value = o.store_code) as store_name ");
			sql.append("\n ,(select max(m.interface_value) from pensbi.pensbme_mst_reference m where m.reference_code ='LotusItem'"
					+ "     and m.interface_desc = o.barcode) as item_desc ");
			sql.append("\n ,(SELECT max(m.pens_value) from PENSBME_MST_REFERENCE m   ");
			sql.append("\n   where m.reference_code ='LotusItem' and m.interface_desc = o.barcode) as pens_item ");
			sql.append("from pensbi.pensbme_order o \n");
			sql.append("where order_date =to_date('"+orderDateStr+"','dd/mm/yyyy') \n");
			sql.append("and store_code in("+storeCodeAllIn+")");
			sql.append("order by store_code,barcode ");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while (rst.next()) {
				OrderErrorBean item = new OrderErrorBean();
				item.setStoreCode(rst.getString("store_code"));
				item.setStoreName(rst.getString("store_name"));
				item.setGroupCode(rst.getString("group_code"));
				item.setBarcode(rst.getString("barcode"));
				item.setGroupCodeDesc(rst.getString("item_desc"));
				item.setPensItem(rst.getString("pens_item"));
				item.setQty(String.valueOf(rst.getInt("qty")));
				
				barcodeList.add(item);
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			rst.close(); rst=null;
            ps.close(); ps=null;
		}
		return barcodeList;
	}
	
	private static List<OrderErrorBean> getOrderNoList(Connection conn,String orderDateS,String storeCodeAllIn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<OrderErrorBean> barcodeList = new ArrayList<OrderErrorBean>();
		try{
			Date orderDate = DateUtil.parse(orderDateS, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String orderDateStr = DateUtil.stringValue(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select distinct order_no ");
			sql.append("from pensbi.pensbme_order \n");
			sql.append("where order_date =to_date('"+orderDateStr+"','dd/mm/yyyy') \n");
			sql.append("and store_code in("+storeCodeAllIn+")");
			sql.append("order by order_no ");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while (rst.next()) {
				OrderErrorBean item = new OrderErrorBean();
				item.setOrderNo(rst.getString("order_no"));
				barcodeList.add(item);
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			rst.close(); rst=null;
            ps.close(); ps=null;
		}
		return barcodeList;
	}
	
	private static Map<String, OrderErrorBean> getOrderDetailMap(Connection conn,String orderDateS,String storeCodeAllIn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String key = "";//storeCode+orderNo+barcode
		Map<String, OrderErrorBean> dataErrorMap = new HashMap<String, OrderErrorBean>();
		try{
			Date orderDate = DateUtil.parse(orderDateS, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String orderDateStr = DateUtil.stringValue(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select order_date ,store_type,store_code,order_no,group_code,barcode,qty ");
			sql.append("from pensbi.pensbme_order \n");
			sql.append("where order_date =to_date('"+orderDateStr+"','dd/mm/yyyy') \n");
			sql.append("and store_code in("+storeCodeAllIn+")");
			sql.append("order by order_no,group_code ");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while (rst.next()) {
				OrderErrorBean item = new OrderErrorBean();
				item.setOrderNo(rst.getString("order_no"));
				item.setStoreCode(rst.getString("store_code"));
				item.setOrderDate(DateUtil.stringValue(rst.getDate("order_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				item.setStoreType(rst.getString("store_type"));
				item.setGroupCode(rst.getString("group_code"));
				item.setBarcode(rst.getString("barcode"));
				item.setQty(String.valueOf(rst.getInt("qty")));
				
				//key = storeCode+barcode+orderNo
				key = item.getStoreCode()+item.getBarcode()+item.getOrderNo();
				dataErrorMap.put(key, item);
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			rst.close(); rst=null;
            ps.close(); ps=null;
		}
		return dataErrorMap;
	}
}
