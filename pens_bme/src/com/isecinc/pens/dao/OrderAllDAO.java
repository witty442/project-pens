package com.isecinc.pens.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Constants;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.exception.LogisticException;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.order.OrderAction;

public class OrderAllDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	public int MAX_ORDER_SPLIT_ORDER_NO = 450;//default 450
	
	
	public int getTotalRowBMEItemByPageName(Connection conn,Order o,String pageName ) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
        int totalRow = 0;
		StringBuilder sql = new StringBuilder();
		String tableName = "";
		try {
			if("OSHOPPING".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_OSHOPPING";
			}
			sql.append("\n SELECT count(*) as total_row  from "+tableName+" ");
			sql.append("\n where 1=1 AND onhand_qty <> 0 and status <> 'ERROR' ");
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and group_item = '"+Utils.isNull(o.getGroupCode())+"'  ");
			}
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
		
			if (rst.next()) {
				totalRow = rst.getInt("total_row");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				
			} catch (Exception e) {}
		}
		return totalRow;
	}
	
	
	public List<Order> prepareNewOrderByPageName(String pageName,Connection conn,Order o,List<StoreBean> storeList,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<Order> pos = new ArrayList<Order>();
		StringBuilder sql = new StringBuilder();
		String tableName = "";
		try {
			if("OSHOPPING".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_OSHOPPING";
			}
			Date orderDate = Utils.parse(o.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql.delete(0, sql.length());
			sql.append("\n  SELECT *  from "+tableName+"");
			sql.append("\n  where 1=1 AND onhand_qty <> 0 and status <> 'ERROR' ORDER BY ITEM ASC ");
	
			logger.debug("sql:"+sql);
			
			//Get StoreBeanMap 
			Map<String, StoreBean> storeBeanOrderMap = getStoreBeanOrderMap(conn, o.getStoreType(), orderDate);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int n=0;
			List<StoreBean> storeItemList = null;
			while (rst.next()) {
				Order item = new Order();
				item.setBarcode(rst.getString("BARCODE"));
				item.setOnhandQty(rst.getString("Onhand_QTY"));
				
				item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("Whole_Price_BF"),Utils.format_current_2_disgit)+" ");
				item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("Retail_Price_BF"),Utils.format_current_2_disgit)+" ");
				
				item.setItem(rst.getString("item"));
				item.setItemDesc(rst.getString("item_desc"));
				if( !Utils.isNull(item.getItemDesc()).equals("")){
					item.setItemDisp(item.getItemDesc().substring(item.getItemDesc().indexOf("-")+1,item.getItemDesc().length()));
				}
				item.setGroupCode(rst.getString("group_item"));
				
				//** add Store 
				//** OrderNo ,qty by StoreCode
				if(storeList != null && storeList.size() >0){
					storeItemList = new ArrayList<StoreBean>();
					for(int c =0;c<storeList.size();c++){
						StoreBean s = (StoreBean)storeList.get(c);
						
						//Find Order 
						//StoreBean order = getOrderByStoreNo(conn,o.getStoreType(),orderDate, s.getStoreCode(), item.getItem());
						
						String keyMap = item.getItem()+"_"+s.getStoreCode();
						StoreBean order = storeBeanOrderMap.get(keyMap)!=null?(StoreBean)storeBeanOrderMap.get(keyMap):null;
						if(order!= null){
							order.setStoreDisp(s.getStoreDisp());
							order.setStoreName(s.getStoreName());
							//System.out.println("StoreCode["+order.getStoreCode()+"]OrderNo["+order.getOrderNo()+"]item["+order.getItem()+"]qty["+order.getQty()+"]");

						}else{
							order = s;
							order.setQty("");
						}
						storeItemList.add(order);

					}//for
				}//if
				
				//logger.debug("storeItemList Size:"+storeItemList.size());
				item.setStoreItemList(storeItemList);
				
				pos.add(item);

				n++;
				//if(n==3){break;}
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				
			} catch (Exception e) {}
		}
		return pos;
	}
	
	
	public List<Order> prepareNewOrderByPageName(String pageName,Connection conn,Order o,List<StoreBean> storeList,User user,int pageNumber,int pageSize) throws Exception {
		logger.debug("prepareNewOrder");
		PreparedStatement ps = null;
		ResultSet rst = null;
		List<Order> pos = new ArrayList<Order>();
		StringBuilder sql = new StringBuilder();
		String tableName = "";
		String storeType = "";
		String storeTypeItemCode = "";
		try {
			if("OSHOPPING".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_OSHOPPING";
				storeType = Constants.STORE_TYPE_OSHOPPING_CODE;
				storeTypeItemCode = Constants.STORE_TYPE_OSHOPPING_ITEM;
			}
			Date orderDate = Utils.parse(o.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql.append("\n SELECT * FROM( \n");
			sql.append("\n SELECT a.*, rownum r__ \n");
			sql.append("\n FROM ( \n");
			sql.append("\n  SELECT s.* FROM(  \n");
				sql.append("\n SELECT h.* ");
				sql.append("\n ,h.item as item_wacoal ");
				sql.append("\n ,( h.onhand_qty - (select nvl(sum(o.qty),0)  from pensbme_order o  where 1=1  "); 
			    sql.append("\n    and o.order_date = ?  ");
			    sql.append("\n    and o.store_type ='"+storeType+"'");
				sql.append("\n    and o.barcode = h.barcode ) ");
				sql.append("\n  ) as remain_onhand_qty ");
						
				sql.append("\n ,(SELECT max(m.pens_value) from PENSBME_MST_REFERENCE m   ");
				sql.append("\n   where m.reference_code ='"+storeTypeItemCode+"' and m.interface_desc = h.barcode) as item_oracle ");
				sql.append("\n ,(SELECT max(m.interface_value) from PENSBME_MST_REFERENCE m   ");
				sql.append("\n   where m.reference_code ='"+storeTypeItemCode+"' and m.interface_desc = h.barcode) as item_style ");
				sql.append("\n from "+tableName+" h   \n");
				sql.append("\n where 1=1 AND onhand_qty <> 0 and status <> 'ERROR'  ");
				
				if( !Utils.isNull(o.getGroupCode()).equals("")){
					sql.append("\n and group_item = '"+Utils.isNull(o.getGroupCode())+"'  ");
				}
				
				sql.append("\n  ) s  ");
				sql.append("\n order by s.group_item,s.item_style asc  ");
				sql.append("\n ) a  ");
			sql.append("\n  WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
			sql.append("\n )  ");
			sql.append("\n WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
					
			logger.debug("sql:"+sql);
			
			//Get StoreBeanMap 
			Map<String, StoreBean> storeBeanOrderMap = getStoreBeanOrderMap(conn, o.getStoreType(), orderDate);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			rst = ps.executeQuery();
			int n=0;
			List<StoreBean> storeItemList = null;
			while (rst.next()) {
				Order item = new Order();
				item.setBarcode(rst.getString("BARCODE"));
				item.setBillType(o.getBillType());
				item.setOnhandQty(rst.getString("remain_onhand_QTY"));
				
				item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("Whole_Price_BF"),Utils.format_current_2_disgit)+" ");
				item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("Retail_Price_BF"),Utils.format_current_2_disgit)+" ");
				
				item.setItem(rst.getString("item_oracle"));
				
				String itemStyle = Utils.isNull(rst.getString("item_style"));
				if(!"".equals(itemStyle)){
					itemStyle = itemStyle.substring(itemStyle.length()-4,itemStyle.length());
				}
				item.setItemDisp(itemStyle);
				item.setItemDesc(rst.getString("item_style"));
				item.setGroupCode(rst.getString("group_item"));
				
				int lineQty = 0;
				//** add Store 
				//** OrderNo ,qty by StoreCode
				if(storeList != null && storeList.size() >0){
					storeItemList = new ArrayList<StoreBean>();
					for(int c =0;c<storeList.size();c++){
						StoreBean s = (StoreBean)storeList.get(c);
						
						//Find Order 
						//StoreBean order = getOrderByStoreNo(conn,o.getStoreType(),orderDate, s.getStoreCode(), item.getItem());
						
						String keyMap = item.getBarcode()+"_"+s.getStoreCode()+"_"+item.getBillType();
						//logger.debug("KeyMap["+keyMap+"]");
						
						
						StoreBean order = storeBeanOrderMap.get(keyMap)!=null?(StoreBean)storeBeanOrderMap.get(keyMap):null;
						if(order!= null){
							order.setStoreDisp(s.getStoreDisp());
							order.setStoreName(s.getStoreName());
							//System.out.println("StoreCode["+order.getStoreCode()+"]OrderNo["+order.getOrderNo()+"]item["+order.getItem()+"]qty["+order.getQty()+"]");

							lineQty += Utils.convertStrToInt(order.getQty());
						}else{
							order = s;
							order.setQty("");
						}
						storeItemList.add(order);

					}//for
					
					//RemainOmhandQty + lineQty display
					item.setOnhandQty((Utils.convertStrToInt(item.getOnhandQty())+lineQty)+"");
					
				}//if
				
				//logger.debug("storeItemList Size:"+storeItemList.size());
				item.setStoreItemList(storeItemList);
				
				pos.add(item);

				n++;
				//if(n==3){break;}
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rst !=null){
				  rst.close();rst=null;
				}
				if(ps != null){
				  ps.close();ps=null;
				}
			} catch (Exception e) {}
		}
		return pos;
	}
	
	public void genOrderLotNoProcess(Connection conn,User user,String orderDateStr) throws Exception {
		try{
			Date orderDate = Utils.parse(orderDateStr, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//logger.debug("sql:"+sql);
			List<Order> storeOrderList = findStoreOrderList(conn, user, orderDate);
			if(storeOrderList != null && storeOrderList.size() >0){
				for(int i=0;i<storeOrderList.size();i++){
					Order o = (Order)storeOrderList.get(i);
					if(o.getCountOrderNoByStoreCode() > MAX_ORDER_SPLIT_ORDER_NO){
						logger.debug("Case Order Item over maxSplitOrderNo ->Split order_no");
						//update order by barcode
						updateOrderLotNoByStoreBarcode(conn,user,orderDate,o.getStoreType(),o.getStoreCode(),o.getOrderNo());
					}else{
						logger.debug("Case StoreCode not over maxSplitOrderNo ->update order_lot_no = order_no;");
						//update all storeNo,orderNo
						String orderLotNo = OrderNoGenerate.genOrderNoSplit(conn, orderDate, "ALLBR");
						String barOnBox = OrderNoGenerate.genBarOnBox(conn, orderDate);
						
						o.setOrderLotNo(orderLotNo);
						o.setBarOnBox(barOnBox);
						
						updateOrderLotNoByStoreCode(conn, o);
					}
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		} finally {
		}
	}

	private void updateOrderLotNoByStoreBarcode(Connection conn,User user,Date orderDate,String storeType,String storeCode,String orderNo) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
		    logger.debug("updateOrderLotNoByStoreBarcode ");
		    logger.debug("****Param:********************");
		    logger.debug("orderDate["+orderDate+"]");
		    logger.debug("storeType["+storeType+"]");
		    logger.debug("storeCode["+storeCode+"]");
		    logger.debug("orderNo["+orderNo+"]");
		    logger.debug("****Param:********************");
		    
			sql.append(" select o.store_type, o.store_code,o.order_no , \n ");
			sql.append("  o.bill_type, o.barcode \n ");
			sql.append("  from PENSBME_ORDER o \n ");
			sql.append("  WHERE 1=1  \n ");
			sql.append("  and trunc(order_date) = ?  \n ");
			sql.append("  and store_type = ?  \n ");
			sql.append("  and store_code = ?  \n ");
			sql.append("  and order_no = ?  \n ");
			sql.append(" ORDER BY o.store_type,o.order_no,o.store_code,o.bill_type,o.barcode asc \n ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2,storeType);
			ps.setString(3,storeCode);
			ps.setString(4,orderNo);
			
			rst = ps.executeQuery();
			int no = 1;
			String orderLotNo = "";
			String barOnBox = "";
			while (rst.next()) {
				Order o = new Order();
				o.setStoreType(rst.getString("store_type"));//key
				o.setStoreCode(rst.getString("store_code"));//key
				o.setOrderNo(rst.getString("order_no"));//key
				o.setBillType(rst.getString("bill_type"));//key
				o.setBarcode(rst.getString("barcode"));//key
				o.setUpdateUser(user.getUserName());
				
				logger.debug("no["+no+"]MAX["+MAX_ORDER_SPLIT_ORDER_NO+"]");
				if(no==1){
				   orderLotNo = OrderNoGenerate.genOrderNoSplit(conn, orderDate, "ALLBR");
				   barOnBox = OrderNoGenerate.genBarOnBox(conn, orderDate);

				   logger.debug("gen orderLotNo["+orderLotNo+"]barOnBox["+barOnBox+"]");
				}
				
				if(no == MAX_ORDER_SPLIT_ORDER_NO){
				   no =0;//reset no;
				}
				
				 o.setOrderLotNo(orderLotNo);
				 o.setBarOnBox(barOnBox);
				
				 logger.debug("Fetch**********************");
				 logger.debug("orderDate["+o.getStoreType()+"]");
				 logger.debug("storeType["+o.getStoreCode()+"]");
				 logger.debug("storeCode["+o.getOrderNo()+"]");
				 logger.debug("bill_type["+o.getBillType()+"]");
				 logger.debug("barcode["+o.getBarcode()+"]");
				 logger.debug("orderLotNo["+o.getOrderLotNo()+"]");
				 logger.debug("barOnBox["+o.getBarOnBox()+"]");
				 logger.debug("***************************");
				 
			    //update OrderLotNo by orderNo ,barcode
				updateOrderLotNoBySplitOrderNo(conn,o);
				
				no++;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		} finally {
			rst.close(); rst=null;
            ps.close(); ps=null;
		}
	}
	

	private List<Order> findStoreOrderList(Connection conn,User user,Date orderDate) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<Order> storeOrderList = new ArrayList<Order>();
		try{
			
			sql.append(" select A.* from ( \n ");
			sql.append("  select distinct o.store_type, o.store_code,o.order_no ,count(*) as count_order_no \n ");
			sql.append("  from PENSBME_ORDER o \n ");
			sql.append("  WHERE 1=1  \n ");
			sql.append("  and trunc(order_date) = ?  \n ");
			sql.append("  group by o.store_type,o.store_code,o.order_no  \n ");
			sql.append(" )A order by A.store_type,A.order_no,A.store_code asc \n ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			
			rst = ps.executeQuery();
			while (rst.next()) {
				Order o = new Order();
				o.setStoreType(rst.getString("store_type"));
				o.setStoreCode(rst.getString("store_code"));
				o.setOrderNo(rst.getString("order_no"));
				o.setCountOrderNoByStoreCode(rst.getInt("count_order_no"));
				
				storeOrderList.add(o);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			rst.close(); rst=null;
            ps.close(); ps=null;
		}
		return storeOrderList;
	}
	
	public StringBuffer genOrderToTextAll(Connection conn,User user,String orderDateStr) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer data = new StringBuffer("");
		StringBuilder sql = new StringBuilder();
		String BLANK = " ";
		ImportDAO importDAO = new ImportDAO();
		try {

			Date orderDate = Utils.parse(orderDateStr, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			sql.append(" select A.* from ( \n ");
			sql.append("  select o.order_lot_no,o.bar_on_box, TO_CHAR(o.ORDER_DATE, 'ddmmyyyy') as order_date, \n ");
			sql.append("  o.barcode, o.qty, 'PC' as uom ,o.store_code ,o.bill_type ,o.valid_from,o.valid_to, \n ");
			sql.append("  ( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='Store' and m.pens_value = o.store_code and m.status='Active') as  store_name_real, \n ");
			sql.append("  ( SELECT m.pens_desc2 FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='Store' and m.pens_value = o.store_code and m.status='Active') as  store_name, \n ");
			sql.append("  ( SELECT m.pens_desc3 FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='Store' and m.pens_value = o.store_code and m.status='Active') as  store_address, \n ");
			sql.append("  ( SELECT max(m.interface_value) from PENSBME_MST_REFERENCE m  where m.reference_code ='LotusItem' and m.interface_desc = o.barcode) as material_master, ");
			sql.append("  o.whole_price_bf,o.retail_price_bf,o.create_date \n ");
			sql.append("  from PENSBME_ORDER o \n ");
			sql.append("  WHERE 1=1  \n ");
			sql.append("  and trunc(order_date) = ?  \n ");
			
			sql.append(" )A order by A.order_lot_no,A.bar_on_box,A.order_date, A.material_master asc \n ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			
			rst = ps.executeQuery();
			while (rst.next()) {

				String orderNo = Utils.isNull(rst.getString("order_lot_no"));
				String barOnBox = Utils.isNull(rst.getString("bar_on_box"));
				String orderDateSS = Utils.isNull(rst.getString("order_date"));
				String materialMaster = Utils.isNull(rst.getString("material_master"));
				String barcode = Utils.isNull(rst.getString("barcode"));
				String qty = Utils.isNull(rst.getString("qty"));
				String uom = Utils.isNull(rst.getString("uom"));
				String store_code = Utils.isNull(rst.getString("store_code"));
				String store_name = Utils.isNull(rst.getString("store_name"));
				String store_name_real = Utils.isNull(rst.getString("store_name_real"));
				String store_address = Utils.isNull(rst.getString("store_address"));
				String billType = Utils.isNull(rst.getString("bill_type"));
				String validFrom = Utils.isNull(rst.getString("valid_from"));
				String validTo = Utils.isNull(rst.getString("valid_to"));
				
				if("N".equalsIgnoreCase(billType)){
					billType = "";
				}
				
				Master mLogistic = importDAO.getLogisticsName(conn, store_code);
				String TSC_ID = "";
				String TSC_NAME = "";
				
				if(mLogistic != null){
				   TSC_ID = Utils.isNull(mLogistic.getInterfaceValue());
				   TSC_NAME = Utils.isNull(mLogistic.getInterfaceDesc());
				}
				//Validate Logistic Name is null throw Exception
				if("".equalsIgnoreCase(TSC_ID) || "".equalsIgnoreCase(TSC_NAME)){
					String error = "ร้าน ["+store_code+"-"+store_name_real+"] ไม่พบข้อมูล Logistic กรุณาแก้ไขข้อมูลให้ถูกต้อง";
					throw new LogisticException(error);
				}
				
				String whole_price_bf = Utils.isNull(rst.getString("whole_price_bf"));
				String whole_price_bf_1 = whole_price_bf.substring(0,whole_price_bf.indexOf("."));
		    	String whole_price_bf_2 = whole_price_bf.substring(whole_price_bf.indexOf(".")+1,whole_price_bf.length());
		    	
				String retail_price_bf = Utils.isNull(rst.getString("retail_price_bf"));
				String retail_price_bf_1 = retail_price_bf.substring(0,retail_price_bf.indexOf("."));
		    	String retail_price_bf_2 = retail_price_bf.substring(retail_price_bf.indexOf(".")+1,retail_price_bf.length());
		    	
		    	String storeGroup = "";
		    	//020047-19
		    	String storeType = store_code.substring(0,6);
		    	storeGroup = importDAO.getStoreTypeName(conn, storeType).getInterfaceDesc();
		    	
		    	logger.debug("storeType:"+storeType+",store_code:"+store_code+",storeGroup:"+storeGroup);
				logger.debug("billType["+billType+"]");
				
		    	String custMat = "";

		    	data.append(Utils.appendRight(orderNo, BLANK, 15));//1	15
		    	data.append(Utils.appendRight(orderDateSS, BLANK, 8));//16	23
		    	data.append(Utils.appendRight(materialMaster, BLANK, 15));//24	38
		    	data.append(Utils.appendRight(barcode, BLANK, 13));//39	51
		    	data.append(Utils.appendLeft(qty, "0", 9));//52	60
		    	data.append(Utils.appendRight(uom, BLANK, 5));//61	65
		    	data.append(Utils.appendRight(store_code, BLANK, 10));//66	75
		    	data.append(Utils.appendRight(store_name, BLANK, 50));//76	125
		    	data.append(Utils.appendRight(store_address,BLANK, 100));//126	225
		    	data.append(Utils.appendRight(TSC_ID, BLANK, 3));//226	228
		    	data.append(Utils.appendRight(TSC_NAME, BLANK, 40));//229	268
		    	
		    	data.append(Utils.appendLeft(retail_price_bf_1, "0", 9));//280	290
		    	data.append(Utils.appendRight(retail_price_bf_2, "0", 2));
		    	
		    	data.append(Utils.appendLeft(whole_price_bf_1, "0", 9));//269	279
		    	data.append(Utils.appendRight(whole_price_bf_2, "0", 2));

		    	data.append(Utils.appendRight(storeGroup, BLANK, 10));//291	301
		    	data.append(Utils.appendRight(custMat, BLANK, 15));  //302  317
		    	data.append(Utils.appendRight(billType, BLANK, 10));  //
		    	data.append(Utils.appendRight(validFrom, BLANK, 8));
		    	data.append(Utils.appendRight(validTo, BLANK, 8));
		    	data.append(Utils.appendRight(barOnBox, BLANK, 13));
		    	
		    	data.append("\n");
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
                ps.close();
				
			} catch (Exception e) {}
		}
		return data;
	}
	
	public StringBuffer genOrderToExcelByPageName(String pageName,Connection conn,User user,String storeType,String orderDateStr) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer h = new StringBuffer("");
		StringBuilder sql = new StringBuilder();
		String tableName = "";
		try {
			if("OSHOPPING".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_OSHOPPING";
			}
			
			 h.append("<table align='center' border='1' cellpadding='3' cellspacing='0' class='result'> \n");
		     h.append("<tbody> \n");
			   h.append("<td>no \n </td>");
			   h.append("<td>orderNo \n </td>");
			   h.append("<td>orderDateSS \n </td>");
			   h.append("<td>materialMaster \n </td>");
			   h.append("<td>barcode \n </td>");
			   h.append("<td>qty \n </td>");
			   h.append("<td>uom \n </td>");
			   h.append("<td>store_code \n </td>");
			   h.append("<td>store_name \n </td>");
			   h.append("<td>store_address \n </td>");
			   h.append("<td>TSC_ID \n </td>");
			   h.append("<td>TSC_NAME \n </td>");
			   h.append("<td>whole_price_bf \n </td>");
			   h.append("<td>retail_price_bf \n </td>");
            h.append("</tbody> \n");    
	            
			Date orderDate = Utils.parse(orderDateStr, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			sql.append("  select o.order_no,TO_CHAR(o.ORDER_DATE, 'ddmmyyyy') as order_date, b.material_master, \n ");
			sql.append("  o.barcode, o.qty, 'PC' as uom ,o.store_code ,\n ");
			sql.append("  ( SELECT s.pens_desc2 FROM PENSBME_MST_REFERENCE s WHERE s.reference_code ='Store' and s.pens_value = o.store_code ) as  store_name, \n ");
			sql.append("  ( SELECT s.pens_desc3 FROM PENSBME_MST_REFERENCE s WHERE s.reference_code ='Store' and s.pens_value = o.store_code ) as  store_address, \n ");
			sql.append("  'T1' as TSC_ID , \n ");
			sql.append("  ( SELECT s.interface_desc FROM PENSBME_MST_REFERENCE s WHERE s.reference_code ='Logistic' and s.interface_value ='T1' ) as TSC_NAME, \n ");
			sql.append("  b.whole_price_bf,b.retail_price_bf \n ");
			sql.append("  from PENSBME_ORDER o,"+tableName+" b \n ");
			sql.append("  WHERE 1=1 and o.barcode = b.barcode \n ");
			sql.append("  and trunc(order_date) = ?  \n ");
			sql.append("  and store_type= ? \n ");
			sql.append("  order by o.order_no,o.order_date, b.material_master asc \n ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			rst = ps.executeQuery();
	        int no = 0;
			while (rst.next()) {
				no++;
				String orderNo = Utils.isNull(rst.getString("order_no"));
				String orderDateSS = Utils.isNull(rst.getString("order_date"));
				String materialMaster = Utils.isNull(rst.getString("material_master"));
				String barcode = Utils.isNull(rst.getString("barcode"));
				String qty = Utils.isNull(rst.getString("qty"));
				String uom = Utils.isNull(rst.getString("uom"));
				String store_code = Utils.isNull(rst.getString("store_code"));
				String store_name = Utils.isNull(rst.getString("store_name"));
				String store_address = Utils.isNull(rst.getString("store_address"));
				String TSC_ID = Utils.isNull(rst.getString("TSC_ID"));
				String TSC_NAME = Utils.isNull(rst.getString("TSC_NAME"));
				
				String whole_price_bf = Utils.isNull(rst.getString("whole_price_bf"));
				//String whole_price_bf_1 = whole_price_bf.substring(0,whole_price_bf.indexOf("."));
		    	//String whole_price_bf_2 = whole_price_bf.substring(whole_price_bf.indexOf(".")+1,whole_price_bf.length());
		    	
				String retail_price_bf = Utils.isNull(rst.getString("retail_price_bf"));
				//String retail_price_bf_1 = retail_price_bf.substring(0,retail_price_bf.indexOf("."));
		    	//String retail_price_bf_2 = retail_price_bf.substring(retail_price_bf.indexOf(".")+1,retail_price_bf.length());
		    	
		    	 h.append("<tr> \n");
				   h.append("<td>"+no+" \n </td>");
				   h.append("<td>"+orderNo+" \n </td>");
				   h.append("<td>"+orderDateSS+" \n </td>");
				   h.append("<td>"+materialMaster+" \n </td>");
				   h.append("<td>&nbsp;"+barcode+" \n </td>");
				   h.append("<td>"+qty+" \n </td>");
				   h.append("<td>"+uom+" \n </td>");
				   h.append("<td>"+store_code+" \n </td>");
				   h.append("<td>"+store_name+" \n </td>");
				   h.append("<td>"+store_address+" \n </td>");
				   h.append("<td>"+TSC_ID+" \n </td>");
				   h.append("<td>"+TSC_NAME+" \n </td>");
				   h.append("<td>&nbsp;"+whole_price_bf+" \n </td>");
				   h.append("<td>&nbsp;"+retail_price_bf+" \n </td>");
				 h.append("</tr> \n"); 
			
				//i++;
				//if(i==1){break;}
			}//while
			
			h.append("</table> \n");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
                ps.close();
				
			} catch (Exception e) {}
		}
		return h;
	}
	
	public StringBuffer genSummaryOrderToExcel(Connection conn,User user,String storeType,Date orderDate) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer h = new StringBuffer("");
		StringBuilder sql = new StringBuilder();
		try {
			
			//Customer code	Cust name	order date to wacoal	Group	Pens item	Pens item desc	sum qty by pens item
			String refCode = "LotusItem";// Lotus Item include all product (BigC)
			 h.append("<table align='center' border='1' cellpadding='3' cellspacing='0' class='result'> \n");
		     h.append("<tbody> \n");
			   h.append("<td>no </td> \n");
			   h.append("<td>Order No </td> \n");
			   h.append("<td>Customer Code  </td> \n");
			   h.append("<td>Customer Name  </td> \n");
			   h.append("<td>Order Date To Wacoal  </td> \n");
			   h.append("<td>Group Code  </td> \n");
			   h.append("<td>Pens Item  </td> \n");
			   h.append("<td>Pens Item Desc  </td> \n");
			   h.append("<td>sum qty by pens item  </td> \n");
            h.append("</tbody> \n");    
	            
			sql.append("select \n");
			sql.append(" A.order_no, A.store_code \n");
			sql.append(",A.store_name \n");
			sql.append(",TO_CHAR(A.ORDER_DATE, 'ddmmyyyy') as order_date \n");
			sql.append(",A.group_code \n");
			sql.append(",A.item \n");
			sql.append(",A.item_desc \n");
			sql.append(",SUM(A.qty) as qty \n");
			sql.append("FROM( \n");
			sql.append("  select  \n");
			sql.append("   o.store_code ,o.order_no \n");
			sql.append("  ,( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='Store' and m.pens_value = o.store_code ) as  store_name  \n");
			sql.append("  ,o.order_date \n");
			sql.append("  ,o.group_code \n");
			sql.append("  ,o.item \n");
			sql.append("  ,( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='"+refCode+"' and m.pens_value = o.item ) as  item_desc \n");
			sql.append("  ,o.qty \n");
			sql.append("  from PENSBME_ORDER o \n");
			sql.append("  WHERE 1=1  \n");
			sql.append("  and o.qty <> 0 \n");
			sql.append("  and trunc(order_date) = ? \n");
			sql.append("  and store_type= ? \n");
			sql.append(" )A \n");
			sql.append("GROUP BY A.order_no,A.store_code,A.store_name \n");
			sql.append(",A.order_date ,A.group_code, A.item ,A.item_desc \n");
			sql.append("ORDER BY a.order_no,A.store_code,A.store_name ,A.order_date,A.group_code, A.item ,A.item_desc asc \n");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			rst = ps.executeQuery();
	        int no = 0;
			while (rst.next()) {
				no++;
				String orderNo = Utils.isNull(rst.getString("order_no"));
				String storeCode = Utils.isNull(rst.getString("store_code"));
				String orderDateSS = Utils.isNull(rst.getString("order_date"));
				String storeName = Utils.isNull(rst.getString("store_name"));
				String groupCode = Utils.isNull(rst.getString("group_code"));
				String qty = Utils.isNull(rst.getString("qty"));
				String item = Utils.isNull(rst.getString("item"));
				String itemDesc = Utils.isNull(rst.getString("item_desc"));

		    	 h.append("<tr> \n");
				   h.append("<td>"+no+" \n </td>");
				   h.append("<td>"+orderNo+" \n </td>");
				   h.append("<td>"+storeCode+" \n </td>");
				   h.append("<td>"+storeName+" \n </td>");
				   h.append("<td>&nbsp;"+orderDateSS+" \n </td>");
				   h.append("<td>"+groupCode+" \n </td>");
				   h.append("<td>"+item+" \n </td>");
				   h.append("<td>&nbsp;"+itemDesc+" \n </td>");
				   h.append("<td>"+qty+" \n </td>");
				  
				 h.append("</tr> \n"); 
			
			}//while
			
			h.append("</table> \n");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
                ps.close();
				
			} catch (Exception e) {}
		}
		return h;
	}
	
	public XSSFWorkbook genSummaryOrderToCsv(Connection conn,User user,String storeType,Date orderDate,String pageName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer h = new StringBuffer("");
		StringBuilder sql = new StringBuilder();
		XSSFWorkbook xssfWorkbook = null;
		 //Color 
        XSSFColor myColor = new XSSFColor(new java.awt.Color(141,180,226));
		try {
		   //Customer code	Cust name	order date to wacoal	Group	Pens item	Pens item desc	sum qty by pens item
		   String refCode = "LotusItem";// Lotus Item include all product (BigC)
		 
           // Create Sheet.
           xssfWorkbook = new XSSFWorkbook();
           XSSFSheet sheet = xssfWorkbook.createSheet("Summary");

           // Font setting for sheet.
           XSSFFont font = xssfWorkbook.createFont();
           font.setBoldweight((short) 700);

           // Create Styles for sheet.
           XSSFCellStyle headerStyle = xssfWorkbook.createCellStyle();
           headerStyle.setFillForegroundColor(myColor);
           headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
           headerStyle.setFont(font);
           headerStyle.setWrapText(true);
           
           XSSFCellStyle dataStyle = xssfWorkbook.createCellStyle();
           dataStyle.setWrapText(true);

           // Create Header Row
           XSSFRow headerRow = sheet.createRow(0);
  
           // Write row for header
           XSSFCell headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(headerStyle);
           headerCell1.setCellValue("no");
           
           XSSFCell headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(headerStyle);
           headerCell2.setCellValue("Order No");
           
           XSSFCell headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(headerStyle);
           headerCell3.setCellValue("Customer Code");
           
           XSSFCell headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(headerStyle);
           headerCell4.setCellValue("Customer Name");
           
           XSSFCell headerCell5 = headerRow.createCell(4);
           headerCell5.setCellStyle(headerStyle);
           headerCell5.setCellValue("Order Date To Wacoal");
           
           XSSFCell headerCell6 = headerRow.createCell(5);
           headerCell6.setCellStyle(headerStyle);
           headerCell6.setCellValue("Group Code");
           
           XSSFCell headerCell7 = headerRow.createCell(6);
           headerCell7.setCellStyle(headerStyle);
           headerCell7.setCellValue("Pens Item");
           
           XSSFCell headerCell8 = headerRow.createCell(7);
           headerCell8.setCellStyle(headerStyle);
           headerCell8.setCellValue("Pens Item Desc");
           
           XSSFCell headerCell9 = headerRow.createCell(8);
           headerCell9.setCellStyle(headerStyle);
           headerCell9.setCellValue("sum qty by pens item");

			sql.append("select \n");
			sql.append(" A.order_no, A.store_code \n");
			sql.append(",A.store_name \n");
			sql.append(",TO_CHAR(A.ORDER_DATE, 'ddmmyyyy') as order_date \n");
			sql.append(",A.group_code \n");
			sql.append(",A.item \n");
			sql.append(",A.item_desc \n");
			sql.append(",SUM(A.qty) as qty \n");
			sql.append("FROM( \n");
			sql.append("  select  \n");
			sql.append("   o.store_code ,o.order_no \n");
			sql.append("  ,( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='Store' and m.pens_value = o.store_code ) as  store_name  \n");
			sql.append("  ,o.order_date \n");
			sql.append("  ,o.group_code \n");
			sql.append("  ,o.item \n");
			sql.append("  ,( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='"+refCode+"' and m.pens_value = o.item ) as  item_desc \n");
			sql.append("  ,o.qty \n");
			sql.append("  from PENSBME_ORDER o \n");
			sql.append("  WHERE 1=1  \n");
			sql.append("  and o.qty <> 0 \n");
			sql.append("  and trunc(order_date) = ? \n");
			sql.append("  and store_type= ? \n");
			sql.append(" )A \n");
			sql.append("GROUP BY A.order_no,A.store_code,A.store_name \n");
			sql.append(",A.order_date ,A.group_code, A.item ,A.item_desc \n");
			sql.append("ORDER BY a.order_no,A.store_code,A.store_name ,A.order_date,A.group_code, A.item ,A.item_desc asc \n");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			rst = ps.executeQuery();
	        int no = 0;
	        int row = 1;
			while (rst.next()) {
				no++;
				String orderNo = Utils.isNull(rst.getString("order_no"));
				String storeCode = Utils.isNull(rst.getString("store_code"));
				String orderDateSS = Utils.isNull(rst.getString("order_date"));
				String storeName = Utils.isNull(rst.getString("store_name"));
				String groupCode = Utils.isNull(rst.getString("group_code"));
				String qty = Utils.isNull(rst.getString("qty"));
				String item = Utils.isNull(rst.getString("item"));
				String itemDesc = Utils.isNull(rst.getString("item_desc"));

			   // Create First Data Row
	           XSSFRow dataRow = sheet.createRow(row); row++;
	          
	           // Write data in data row
	           XSSFCell cell1 = dataRow.createCell(0);
	           cell1.setCellStyle(dataStyle);
	           cell1.setCellValue(new XSSFRichTextString(no+""));
	          
	           XSSFCell cell2 = dataRow.createCell(1);
	           cell2.setCellStyle(dataStyle);
	           cell2.setCellValue(new XSSFRichTextString(orderNo));
	           
	           XSSFCell cell3 = dataRow.createCell(2);
	           cell3.setCellStyle(dataStyle);
	           cell3.setCellValue(new XSSFRichTextString(storeCode));

	           XSSFCell cell4 = dataRow.createCell(3);
	           cell4.setCellStyle(dataStyle);
	           cell4.setCellValue(new XSSFRichTextString(storeName));
	           
	           XSSFCell cell5 = dataRow.createCell(4);
	           cell5.setCellStyle(dataStyle);
	           cell5.setCellValue(new XSSFRichTextString(orderDateSS));
	          
	           XSSFCell cell6 = dataRow.createCell(5);
	           cell6.setCellStyle(dataStyle);
	           cell6.setCellValue(new XSSFRichTextString(groupCode));
	           
	           XSSFCell cell7 = dataRow.createCell(6);
	           cell7.setCellStyle(dataStyle);
	           cell7.setCellValue(new XSSFRichTextString(item));
	           
	           XSSFCell cell8 = dataRow.createCell(7);
	           cell8.setCellStyle(dataStyle);
	           cell8.setCellValue(new XSSFRichTextString(itemDesc));
	           
	           
	           XSSFCell cell9 = dataRow.createCell(8);
	           cell9.setCellStyle(dataStyle);
	           cell9.setCellValue(new XSSFRichTextString(qty));
	          
			}//while
			    
		   //adjust width of the  column
		   Sheet sheet2 = xssfWorkbook.getSheetAt(0);
		   
		   sheet2.setColumnWidth(0, 2500); 
	       sheet2.setColumnWidth(1, 4500);
	       sheet2.setColumnWidth(2, 3500);
	       sheet2.setColumnWidth(3, 5500);
	       sheet2.setColumnWidth(4, 3500);
	       sheet2.setColumnWidth(5, 3500);
	       sheet2.setColumnWidth(6, 3500);
	       sheet2.setColumnWidth(7, 3500);
	       sheet2.setColumnWidth(8, 3500);
	       
			// write in excel

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
                ps.close();
				
			} catch (Exception e) {}
		}
		return xssfWorkbook;
	}
	
	public StringBuffer genDetailOrderToExcel(Connection conn,User user,String storeType,Date orderDate) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer h = new StringBuffer("");
		StringBuilder sql = new StringBuilder();
		try {	
			String refCode = "LotusItem";

			 h.append("<table align='center' border='1' cellpadding='3' cellspacing='0' class='result'> \n");
		     h.append("<tbody> \n");
			   h.append("<td>no </td> \n");
			   h.append("<td>Order No </td> \n");
			   h.append("<td>Customer Code  </td> \n");
			   h.append("<td>Customer Name  </td> \n");
			   h.append("<td>Order Date To Wacoal  </td> \n");
			   h.append("<td>Group Code  </td> \n");
			   h.append("<td>Pens Item  </td> \n");
			   h.append("<td>Pens Item Desc  </td> \n");
			   h.append("<td>Size/Color  </td> \n");
			   h.append("<td>whole price bf  </td> \n");
			   h.append("<td>retail_price_bf  </td> \n");
			   h.append("<td>sum qty by pens item  </td> \n");
            h.append("</tbody> \n");    
	            
			sql.append("select \n");
			sql.append(" A.order_no,A.store_code \n");
			sql.append(",A.store_name \n");
			sql.append(",TO_CHAR(A.ORDER_DATE, 'ddmmyyyy') as order_date \n");
			sql.append(",A.group_code \n");
			sql.append(",A.item \n");
			sql.append(",A.item_desc \n");
			sql.append(",A.item_style  \n");
			sql.append(",A.whole_price_bf ,A.retail_price_bf\n");
			sql.append(",SUM(A.qty) as qty \n");
			sql.append("FROM( \n");
			sql.append("  select  \n");
			sql.append("  o.order_no, o.store_code \n");
			sql.append("  ,( SELECT max(s.pens_desc) FROM PENSBME_MST_REFERENCE s WHERE s.reference_code ='Store' and s.pens_value = o.store_code ) as  store_name  \n");
			sql.append("  ,o.order_date \n");
			sql.append("  ,o.group_code \n");
			sql.append("  ,o.item \n");
			sql.append("  ,( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='"+refCode+"' and m.pens_value = o.item ) as  item_desc \n");
			sql.append("  ,(SELECT max(m.interface_value) from PENSBME_MST_REFERENCE m  where m.reference_code ='"+refCode+"' and m.interface_desc = o.barcode) as item_style ");
			sql.append("  ,o.whole_price_bf,o.retail_price_bf \n");
			sql.append("  ,o.qty \n");
			sql.append("  from PENSBME_ORDER o  \n");
			sql.append("  WHERE 1=1  \n");
			sql.append("  and o.qty <> 0 \n");
			sql.append("  and trunc(order_date) = ?   \n");
			sql.append("  and store_type= ? \n");
			sql.append(" )A \n");
			sql.append("GROUP BY   A.order_no,A.store_code,A.store_name \n");
			sql.append(",A.order_date, A.group_code , A.item ,A.item_desc \n");
			sql.append(",A.item_style,A.whole_price_bf,A.retail_price_bf  \n");
			sql.append("ORDER BY  A.order_no,A.store_code,A.store_name \n");
			sql.append(",A.order_date,A.group_code , A.item ,A.item_desc \n");
			sql.append(",A.item_style asc \n");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			rst = ps.executeQuery();
	        int no = 0;
			while (rst.next()) {
				no++;
				String orderNo = Utils.isNull(rst.getString("order_no"));
				String storeCode = Utils.isNull(rst.getString("store_code"));
				String orderDateSS = Utils.isNull(rst.getString("order_date"));
				String storeName = Utils.isNull(rst.getString("store_name"));
				String groupCode = Utils.isNull(rst.getString("group_code"));
				String qty = Utils.isNull(rst.getString("qty"));
				String item = Utils.isNull(rst.getString("item"));
				String itemDesc = Utils.isNull(rst.getString("item_desc"));
				String itemStyle = Utils.isNull(rst.getString("item_style"));
				
				if(!"".equals(itemStyle)){
					itemStyle = itemStyle.substring(itemStyle.length()-4,itemStyle.length());
				}
				
				String wholePriceBf = Utils.isNull(rst.getString("whole_price_bf"));
				String retailPriceBf = Utils.isNull(rst.getString("retail_price_bf"));
				
		    	 h.append("<tr> \n");
				   h.append("<td>"+no+" \n </td>");
				   h.append("<td>"+orderNo+" \n </td>");
				   h.append("<td>"+storeCode+" \n </td>");
				   h.append("<td>"+storeName+" \n </td>");
				   h.append("<td>&nbsp;"+orderDateSS+" \n </td>");
				   h.append("<td>"+groupCode+" \n </td>");
				   h.append("<td>"+item+" \n </td>");
				   h.append("<td>&nbsp;"+itemDesc+" \n </td>");
				   h.append("<td>"+itemStyle+" \n </td>");
				   h.append("<td>"+wholePriceBf+" \n </td>");
				   h.append("<td>"+retailPriceBf+" \n </td>");
				   h.append("<td>"+qty+" \n </td>");
				  
				 h.append("</tr> \n"); 
			
			}//while
			
			h.append("</table> \n");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rst !=null){rst.close();rst=null;}
                if(ps != null){ps.close();ps=null;}
				
			} catch (Exception e) {}
		}
		return h;
	}
	
	public XSSFWorkbook genDetailOrderToCsv(Connection conn,User user,String storeType,Date orderDate,String pageName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer h = new StringBuffer("");
		StringBuilder sql = new StringBuilder();
		XSSFWorkbook xssfWorkbook = null;
		 //Color 
        XSSFColor myColor = new XSSFColor(new java.awt.Color(141,180,226));
        String tableName = "";
		String storeCodeType = "";
		String storeTypeItemCode = "";
		try {
			if("OSHOPPING".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_OSHOPPING";
				storeCodeType = Constants.STORE_TYPE_OSHOPPING_CODE;
				storeTypeItemCode = Constants.STORE_TYPE_OSHOPPING_ITEM;
			}
			
			// Create Sheet.
           xssfWorkbook = new XSSFWorkbook();
           XSSFSheet sheet = xssfWorkbook.createSheet("SampleExcelSheet");
           
           // Font setting for sheet.
           XSSFFont font = xssfWorkbook.createFont();
           font.setBoldweight((short) 700);
          
           // Create Styles for sheet.
           XSSFCellStyle headerStyle = xssfWorkbook.createCellStyle();
           headerStyle.setFillForegroundColor(myColor);
           headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
           headerStyle.setFont(font);
           XSSFCellStyle dataStyle = xssfWorkbook.createCellStyle();
           dataStyle.setWrapText(true);
           
           // Create Header Row
           XSSFRow headerRow = sheet.createRow(0);
           // Write row for header
           XSSFCell headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(headerStyle);
           headerCell1.setCellValue("no");
           
           XSSFCell headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(headerStyle);
           headerCell2.setCellValue("Order No");
           
           XSSFCell headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(headerStyle);
           headerCell3.setCellValue("Customer Code");
           
           XSSFCell headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(headerStyle);
           headerCell4.setCellValue("Customer Name");
           
           XSSFCell headerCell4_1 = headerRow.createCell(4);
           headerCell4_1.setCellStyle(headerStyle);
           headerCell4_1.setCellValue("Order Lot No");
           
           XSSFCell headerCell4_2 = headerRow.createCell(5);
           headerCell4_2.setCellStyle(headerStyle);
           headerCell4_2.setCellValue("BarOnbox");
           
           XSSFCell headerCell5 = headerRow.createCell(6);
           headerCell5.setCellStyle(headerStyle);
           headerCell5.setCellValue("Order Date To Wacoal");
           
           XSSFCell headerCell6 = headerRow.createCell(7);
           headerCell6.setCellStyle(headerStyle);
           headerCell6.setCellValue("Group Code");
           
           XSSFCell headerCell7 = headerRow.createCell(8);
           headerCell7.setCellStyle(headerStyle);
           headerCell7.setCellValue("Pens Item");
           
           XSSFCell headerCell8 = headerRow.createCell(9);
           headerCell8.setCellStyle(headerStyle);
           headerCell8.setCellValue("Pens Item Desc");
           
           XSSFCell headerCell9 = headerRow.createCell(10);
           headerCell9.setCellStyle(headerStyle);
           headerCell9.setCellValue("Size/Color");
           
           XSSFCell headerCell10 = headerRow.createCell(11);
           headerCell10.setCellStyle(headerStyle);
           headerCell10.setCellValue("whole price bf");
           
           XSSFCell headerCell11 = headerRow.createCell(12);
           headerCell11.setCellStyle(headerStyle);
           headerCell11.setCellValue("retail_price_bf");
           
           XSSFCell headerCell12 = headerRow.createCell(13);
           headerCell12.setCellStyle(headerStyle);
           headerCell12.setCellValue("sum qty by pens item");

			sql.append("select \n");
			sql.append(" A.order_no,A.store_code \n");
			sql.append(",A.order_lot_no,A.bar_on_box \n");
			sql.append(",A.store_name \n");
			sql.append(",TO_CHAR(A.ORDER_DATE, 'ddmmyyyy') as order_date \n");
			sql.append(",A.group_code \n");
			sql.append(",A.item \n");
			sql.append(",A.item_desc \n");
			sql.append(",A.item_style  \n");
			sql.append(",A.whole_price_bf ,A.retail_price_bf \n");
			sql.append(",SUM(A.qty) as qty \n");
			sql.append("FROM( \n");
			sql.append("  select  \n");
			sql.append("  o.order_no, o.store_code,o.order_lot_no,o.bar_on_box \n");
			sql.append("  ,( SELECT max(s.pens_desc) FROM PENSBME_MST_REFERENCE s WHERE s.reference_code ='Store' and s.pens_value = o.store_code ) as  store_name  \n");
			sql.append("  ,o.order_date \n");
			sql.append("  ,o.group_code \n");
			sql.append("  ,o.item \n");
			
			//sql.append("  ,( SELECT max(m.pens_desc) FROM PENSBME_MST_REFERENCE m WHERE m.reference_code ='"+refCode+"' and m.pens_value = o.item ) as  item_desc \n");
			//sql.append("  ,(SELECT max(m.interface_value) from PENSBME_MST_REFERENCE m  where m.reference_code ='"+refCode+"' and m.interface_desc = o.barcode) as item_style ");
			
			sql.append("  ,( SELECT max(m.item_desc) FROM "+tableName+" m WHERE  m.pens_item = o.item ) as  item_desc \n");
			sql.append("  ,(SELECT max(m.material_master) from "+tableName+" m  where  m.barcode = o.barcode) as item_style ");
			
			sql.append("  ,o.whole_price_bf,o.retail_price_bf \n");
			sql.append("  ,o.qty \n");
			sql.append("  from PENSBME_ORDER o  \n");
			sql.append("  WHERE 1=1  \n");
			sql.append("  and o.qty <> 0 \n");
			sql.append("  and trunc(order_date) = ?   \n");
			sql.append("  and store_type= ? \n");
			sql.append(" )A \n");
			sql.append("GROUP BY   A.order_no,A.store_code,A.store_name,A.order_lot_no,A.bar_on_box \n");
			sql.append(",A.order_date, A.group_code , A.item ,A.item_desc \n");
			sql.append(",A.item_style,A.whole_price_bf,A.retail_price_bf  \n");
			sql.append("ORDER BY  A.order_no,A.store_code,A.store_name,A.order_lot_no,A.bar_on_box \n");
			sql.append(",A.order_date,A.group_code , A.item ,A.item_desc \n");
			sql.append(",A.item_style asc \n");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			rst = ps.executeQuery();
	        int no = 0;
	        int row = 1;
			while (rst.next()) {
				no++;
				String orderNo = Utils.isNull(rst.getString("order_no"));
				String orderLotNo = Utils.isNull(rst.getString("order_lot_no"));
				String barOnBox = Utils.isNull(rst.getString("bar_on_box"));
				String storeCode = Utils.isNull(rst.getString("store_code"));
				String orderDateSS = Utils.isNull(rst.getString("order_date"));
				String storeName = Utils.isNull(rst.getString("store_name"));
				String groupCode = Utils.isNull(rst.getString("group_code"));
				String qty = Utils.isNull(rst.getString("qty"));
				String item = Utils.isNull(rst.getString("item"));
				String itemDesc = Utils.isNull(rst.getString("item_desc"));
				String itemStyle = Utils.isNull(rst.getString("item_style"));
				
				if(!"".equals(itemStyle)){
					itemStyle = itemStyle.substring(itemStyle.length()-4,itemStyle.length());
				}
				
				String wholePriceBf = Utils.isNull(rst.getString("whole_price_bf"));
				String retailPriceBf = Utils.isNull(rst.getString("retail_price_bf"));
				
			   // Create First Data Row
	           XSSFRow dataRow = sheet.createRow(row); row++;
	           
	           // Write data in data row
	           XSSFCell cell1 = dataRow.createCell(0);
	           cell1.setCellStyle(dataStyle);
	           cell1.setCellValue(new XSSFRichTextString(no+""));
	           
	           XSSFCell cell2 = dataRow.createCell(1);
	           cell2.setCellStyle(dataStyle);
	           cell2.setCellValue(new XSSFRichTextString(orderNo));
	          
	           XSSFCell cell3 = dataRow.createCell(2);
	           cell3.setCellStyle(dataStyle);
	           cell3.setCellValue(new XSSFRichTextString(storeCode));
	           
	           XSSFCell cell4 = dataRow.createCell(3);
	           cell4.setCellStyle(dataStyle);
	           cell4.setCellValue(new XSSFRichTextString(storeName));
	              
	           XSSFCell cell4_1 = dataRow.createCell(4);
	           cell4_1.setCellStyle(dataStyle);
	           cell4_1.setCellValue(new XSSFRichTextString(orderLotNo));
	           
	           XSSFCell cell4_2 = dataRow.createCell(5);
	           cell4_2.setCellStyle(dataStyle);
	           cell4_2.setCellValue(new XSSFRichTextString(barOnBox));
	           
	           XSSFCell cell5 = dataRow.createCell(6);
	           cell5.setCellStyle(dataStyle);
	           cell5.setCellValue(new XSSFRichTextString(orderDateSS));
	           
	           XSSFCell cell6 = dataRow.createCell(7);
	           cell6.setCellStyle(dataStyle);
	           cell6.setCellValue(new XSSFRichTextString(groupCode));
	           
	           XSSFCell cell7 = dataRow.createCell(8);
	           cell7.setCellStyle(dataStyle);
	           cell7.setCellValue(new XSSFRichTextString(item));
	           
	           XSSFCell cell8 = dataRow.createCell(9);
	           cell8.setCellStyle(dataStyle);
	           cell8.setCellValue(new XSSFRichTextString(itemDesc));
	           
	           XSSFCell cell9 = dataRow.createCell(10);
	           cell9.setCellStyle(dataStyle);
	           cell9.setCellValue(new XSSFRichTextString(itemStyle));
	           
	           XSSFCell cell10 = dataRow.createCell(11);
	           cell10.setCellStyle(dataStyle);
	           cell10.setCellValue(new XSSFRichTextString(wholePriceBf));
	           
	           XSSFCell cell11 = dataRow.createCell(12);
	           cell11.setCellStyle(dataStyle);
	           cell11.setCellValue(new XSSFRichTextString(retailPriceBf));
	           
	           XSSFCell cell12 = dataRow.createCell(13);
	           cell12.setCellStyle(dataStyle);
	           cell12.setCellValue(new XSSFRichTextString(qty));
	           
			}//while
			
			//Set Auto Fit Width
		   Sheet sheet2 = xssfWorkbook.getSheetAt(0);
		   sheet2.setColumnWidth(0, 1500); 
	       sheet2.setColumnWidth(1, 4500);
	       sheet2.setColumnWidth(2, 3500);
	       sheet2.setColumnWidth(3, 5500);
	       
	       sheet2.setColumnWidth(4, 4500);
	       sheet2.setColumnWidth(5, 4500);
	       
	       sheet2.setColumnWidth(6, 4500);
	       sheet2.setColumnWidth(7, 3500);
	       sheet2.setColumnWidth(8, 3500);
	       sheet2.setColumnWidth(9, 4500);
	       sheet2.setColumnWidth(10, 3000);
	       sheet2.setColumnWidth(11, 3500);
	       sheet2.setColumnWidth(12, 4500);
	       sheet2.setColumnWidth(13, 4500);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rst !=null){rst.close();rst=null;}
                if(ps != null){ps.close();ps=null;}
				
			} catch (Exception e) {}
		}
		return xssfWorkbook;
	}
	
	private StoreBean getOrderByStoreNo(Connection conn,String storeType,Date orderDate,String storeCode,String item) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StoreBean m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select order_no,store_code,item, qty from PENSBME_ORDER WHERE trunc(order_date) =? and store_type= ? and store_code =? and item =? \n");
			
		    //logger.debug("SQL:"+sql.toString());
		    
		   /* logger.debug("orderDate["+orderDate+"]");
		    logger.debug("storeCode["+storeCode+"]");
		    logger.debug("item["+item+"]");*/
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			ps.setString(3, storeCode);
			ps.setString(4, item);
			
			rs = ps.executeQuery();
			if(rs.next()){
				m = new StoreBean();
				m.setOrderNo(Utils.isNull(rs.getString("order_no")));
				m.setStoreCode(Utils.isNull(rs.getString("store_code")));
				m.setItem(Utils.isNull(item));
				m.setQty(Utils.isNull(rs.getString("qty")));
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
		return m;
	} 
	
	public Map<String, OrderKeyBean> getOrderNoMap(Connection conn,String storeType,Date orderDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String, OrderKeyBean> orderNoMapByStore = new HashMap<String, OrderKeyBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select order_no,bar_on_box,store_code from PENSBME_ORDER WHERE trunc(order_date) =? and store_type= ? \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
		    logger.debug("orderDate["+orderDate+"]");
		    logger.debug("storeType["+storeType+"]");
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			
			rs = ps.executeQuery();
			while(rs.next()){
				OrderKeyBean orderKey= new OrderKeyBean(rs.getString("order_no"),rs.getString("bar_on_box"));
				orderNoMapByStore.put(rs.getString("store_code"),orderKey );
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
		return orderNoMapByStore;
	} 
	
	
	private Map<String,StoreBean> getStoreBeanOrderMap(Connection conn,String storeType,Date orderDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,StoreBean> map = new HashMap<String,StoreBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select order_no,bar_on_box,store_code,item,barcode,bill_type, qty from PENSBME_ORDER WHERE trunc(order_date) =? and store_type= ?  \n");
			
		    //logger.debug("SQL:"+sql.toString());
		    
		   /* logger.debug("orderDate["+orderDate+"]");
		    logger.debug("storeCode["+storeCode+"]");
		    logger.debug("item["+item+"]");*/
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(2, storeType);
			
			rs = ps.executeQuery();
			String keyMap = "";
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setOrderNo(Utils.isNull(rs.getString("order_no")));
				m.setBarOnBox(Utils.isNull(rs.getString("bar_on_box")));
				m.setStoreCode(Utils.isNull(rs.getString("store_code")));
				m.setItem(Utils.isNull(rs.getString("item")));
				m.setQty(Utils.isNull(rs.getString("qty")));
				m.setBarcode(rs.getString("barcode"));
				m.setBillType(rs.getString("bill_type"));
				
				keyMap = m.getBarcode()+"_"+m.getStoreCode()+"_"+m.getBillType();
				map.put(keyMap, m);
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
		return map;
	} 
	
	
	public static void saveOrder(Connection conn,Order o) throws Exception{
		try{
			saveOrderModel(conn, o);
		}catch(java.sql.SQLIntegrityConstraintViolationException e){
			logger.debug("Exception Uniq Constraint Update Order ");
			logger.debug("OrderNo["+o.getOrderNo()+"]");
			logger.debug("BarOnBox["+o.getBarOnBox()+"]");
			logger.debug("StoreCode["+o.getStoreCode()+"]");
			logger.debug("barcode["+o.getBarcode()+"]");
			logger.debug("BillType["+o.getBillType()+"]");
			//e.printStackTrace();
			//Case Error Exception Uniq Constaint
			//Update 
			try{
			    updateOrder(conn,o);
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
	}
	public static void saveOrderModel(Connection conn,Order o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBME_ORDER \n");
			sql.append(" (ORDER_NO,ORDER_DATE, STORE_TYPE, \n" );
			sql.append(" STORE_CODE, GROUP_CODE, ITEM, \n" );
			sql.append(" BARCODE, EXPORTED, CREATE_DATE, \n");
			sql.append(" CREATE_USER,QTY,BILL_TYPE,VALID_FROM,VALID_TO,Whole_Price_BF,Retail_Price_BF,BAR_ON_BOX) \n");
			sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			int index =1;
			Date orderDate = Utils.parse( o.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			ps.setString(1, o.getOrderNo());
			ps.setTimestamp(2, new java.sql.Timestamp(orderDate.getTime()));
			ps.setString(3, o.getStoreType());
			ps.setString(4, o.getStoreCode());
			ps.setString(5, o.getGroupCode());
			ps.setString(6, o.getItem());
			ps.setString(7, o.getBarcode());
			ps.setString(8, o.getExported());
			ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(10, o.getCreateUser());
			ps.setBigDecimal(11, new BigDecimal(o.getQty()));
			ps.setString(12, Utils.isNull(o.getBillType()));
			ps.setString(13, Utils.isNull(o.getValidFrom()));
			ps.setString(14, Utils.isNull(o.getValidTo()));
			
			ps.setDouble(15, Utils.strToDouble(o.getWholePriceBF()));
			ps.setDouble(16, Utils.strToDouble(o.getRetailPriceBF()));
			ps.setString(17, o.getBarOnBox());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

	public static void updateOrder(Connection conn,Order o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_ORDER \n");
			sql.append(" SET QTY =? ,UPDATE_USER =? ,UPDATE_DATE =?  \n" );
			sql.append(" WHERE ORDER_NO =? AND STORE_CODE =? AND BARCODE =? AND BILL_TYPE =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			//int index =1;
			//Date orderDate = Utils.parse( o.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			ps.setBigDecimal(1, new BigDecimal(o.getQty()));
			ps.setString(2, o.getUpdateUser());
			ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(4, o.getOrderNo());
			ps.setString(5, o.getStoreCode());
			ps.setString(6, o.getBarcode());
			ps.setString(7, o.getBillType());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateOrderLotNoByStoreCode(Connection conn,Order o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_ORDER \n");
			sql.append(" SET ORDER_LOT_NO =? ,BAR_ON_BOX = ?,UPDATE_USER =? ,UPDATE_DATE =?  \n" );
			sql.append(" WHERE ORDER_NO =? AND STORE_CODE =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			//int index =1;
			//Date orderDate = Utils.parse( o.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			ps.setString(1, o.getOrderLotNo());
			ps.setString(2, o.getBarOnBox());
			ps.setString(3, o.getUpdateUser());
			ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(5, o.getOrderNo());
			ps.setString(6, o.getStoreCode());

			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	private static void updateOrderLotNoBySplitOrderNo(Connection conn,Order o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_ORDER \n");
			sql.append(" SET ORDER_LOT_NO =? ,BAR_ON_BOX =? ,UPDATE_USER =? ,UPDATE_DATE =?  \n" );
			sql.append(" WHERE ORDER_NO =? AND STORE_CODE =? AND BARCODE =? AND BILL_TYPE =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			//int index =1;
			//Date orderDate = Utils.parse( o.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			ps.setString(1, o.getOrderLotNo());
			ps.setString(2, o.getBarOnBox());
			ps.setString(3, o.getUpdateUser());
			ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(5, o.getOrderNo());
			ps.setString(6, o.getStoreCode());
			ps.setString(7, o.getBarcode());
			ps.setString(8, o.getBillType());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void deleteOrder(Connection conn,Order o) throws Exception{
		PreparedStatement ps = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE FROM PENSBME_ORDER \n");
			sql.append(" WHERE ORDER_NO =? AND STORE_CODE =? AND BARCODE =? AND BILL_TYPE=? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(1, o.getOrderNo());
			ps.setString(2, o.getStoreCode());
			ps.setString(3, o.getBarcode());
			ps.setString(4, o.getBillType());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public List<StoreBean> getStoreList(Connection conn,String storeType,String region,String billType,String storeCodeWhereIn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<StoreBean> storeList = new ArrayList<StoreBean>();
		ImportDAO importDAO = new ImportDAO();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select a.* from( \n");
			sql.append(" select m.* \n");
			sql.append(",(case when (m.sequence is null or m.sequence ='') then 0 else to_number(m.sequence) end ) as seq \n");
		    sql.append("  from PENSBME_MST_REFERENCE m WHERE 1=1 and m.reference_code ='Store' \n");
			sql.append("  and m.status ='Active'  \n");
			sql.append("  and substr(m.pens_value, 0, 6) ='"+storeType+"' \n");
			//sql.append("  and m.pens_value LIKE '"+storeType+"%' \n");
			if( !Utils.isNull(region).equals("")){
			  sql.append("\n  and m.sequence = "+region);
			}
			if( !Utils.isNull(billType).equals("N")){
			  sql.append("\n and m.pens_desc4 ='"+billType+"'");
			}
			if( !Utils.isNull(storeCodeWhereIn).equals("''") ){
				 sql.append("\n  and m.pens_value in( "+storeCodeWhereIn+")");
			}
			
			sql.append(" )a \n ");;
			sql.append("order by a.seq asc \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setStoreCode(Utils.isNull(rs.getString("pens_value")));
				m.setStoreName(Utils.isNull(rs.getString("pens_desc")));
				m.setBillType(Utils.isNull(rs.getString("pens_desc4")));
				m.setValidFrom(Utils.isNull(rs.getString("pens_desc5")));
				m.setValidTo(Utils.isNull(rs.getString("pens_desc6")));
				
				if( !Utils.isNull(m.getStoreName()).equals("")){
					//m.setStoreDisp(m.getStoreName().substring(0,8)+" "+m.getStoreName().substring(9,m.getStoreName().length()));
					String disp = Utils.isNull(m.getStoreName().substring(m.getStoreName().indexOf("-")+1,m.getStoreName().length()));

					//logger.debug("disp["+disp+"]");
					if(storeType.equals("020047")){//Lotus
						String storeTypeName = importDAO.getStoreTypeName(conn, storeType).getPensDesc();
						if("นครศรีธรรมราช".equalsIgnoreCase(disp)){
							//logger.debug("disp["+disp+"]:"+"นครศรีธรรมราช");
							m.setStoreDisp(storeTypeName+" "+disp.substring(0,6)+" "+disp.substring(7,disp.length())+appendCustType(billType));
							m.setStoreDispShort(disp.substring(0,6)+" "+disp.substring(7,disp.length()));
						}else{
						   m.setStoreDisp(storeTypeName+" "+disp+appendCustType(billType));
						   m.setStoreDispShort(disp+billType);
						}
					}else if(storeType.equals("020049")) {//BigC
						String storeTypeName = importDAO.getStoreTypeName(conn, storeType).getPensDesc();
						m.setStoreDisp(storeTypeName+" "+disp+appendCustType(billType));
						m.setStoreDispShort(disp);
					}else{
						m.setStoreDisp(m.getStoreName()+" "+appendCustType(billType));
						m.setStoreDispShort(disp);
					}
				}
				m.setQty("0");
				storeList.add(m);
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
		return storeList;
	} 
	
	public static Map<String,String> initStoreTypeMap(String storeType) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
		try{
			StringBuffer sql = new StringBuffer("");
			conn = DBConnection.getInstance().getConnection();
			sql.append(" SELECT * FROM PENSBI.PENSBME_MST_REFERENCE WHERE reference_code ='Customer' and pens_value ='"+storeType+"'  \n");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				STORE_TYPE_MAP.put(rs.getString("pens_value"), rs.getString("pens_desc"));
			}
			return STORE_TYPE_MAP;
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	
	} 
	
	private static String appendCustType(String custType){
		if("G".equals(Utils.isNull(custType))){
			return " (G)";
		}
		return "";
	}

}
