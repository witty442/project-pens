
package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MasterItemBean;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.Utils;

public class GeneralDAO {

	private static Logger logger = Logger.getLogger("PENS");
	
	public static boolean isExistBarcodeInBMELocked(Connection conn,String barcode,String mat
			,String groupCode,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        boolean r = false;
		try {
			sql.append("\n select count(*) as c FROM PENSBME_ONHAND_BME_LOCKED WHERE BARCODE ='"+Utils.isNull(barcode)+"' ");
			sql.append("\n and material_master ='"+Utils.isNull(mat)+"'");
			sql.append("\n and GROUP_ITEM ='"+Utils.isNull(groupCode)+"'");
			sql.append("\n and pens_item ='"+Utils.isNull(pensItem)+"'");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c")>0) r=true;
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return r;
	}
	
	public static boolean isExistBarcodeInBMELockedFri(Connection conn,String barcode,String mat
			,String groupCode,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        boolean r = false;
		try {
			sql.append("\n select count(*) as c FROM PENSBME_ONHAND_BME_LOCKED_FRI "
					+ "WHERE BARCODE ='"+Utils.isNull(barcode)+"' ");
			sql.append("\n and material_master ='"+Utils.isNull(mat)+"'");
			sql.append("\n and GROUP_ITEM ='"+Utils.isNull(groupCode)+"'");
			sql.append("\n and pens_item ='"+Utils.isNull(pensItem)+"'");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c")>0) r=true;
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return r;
	}
	public static String getMstCustomerNameOracle( String customerCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        String r = "";
        Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			sql.append("\n select customer_desc FROM PENSBI.XXPENS_BI_MST_CUSTOMER WHERE customer_code ='"+customerCode+"' ");
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				r = Utils.isNull(rst.getString("customer_desc"));
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return r;
	}
	public  String getBranchName(String branchId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String branchName = null;
		Connection conn = null;
		try {
			if(Utils.isNull(branchId).equalsIgnoreCase("ALL")){
				return "ALL Branch";
			}
			sql.append("\n select branch_id,branch_name FROM pensbme_wacoal_store_mapping WHERE 1=1 ");
			sql.append("\n and branch_id = '"+branchId+"' \n");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				branchName = Utils.isNull(rst.getString("branch_name"));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			    conn.close();
			} catch (Exception e) {}
		}
		return branchName;
	}
	 public static List<PopupForm> searchPickJob(PopupForm c,String status) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n  SELECT DISTINCT JOB_ID,NAME,STORE_CODE,STORE_NO,SUB_INV,WAREHOUSE");
				sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Warehouse' and M.pens_value = j.WAREHOUSE) as WAREHOUSE_DESC ");
				sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.store_code) as STORE_NAME ");
				sql.append("\n FROM PENSBI.PENSBME_PICK_JOB j WHERE 1=1 ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and JOB_ID = "+c.getCodeSearch()+" \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and NAME LIKE '%"+c.getDescSearch()+"%' \n");
				}
				if( !Utils.isNull(status).equals("")){
					sql.append(" and STATUS ='"+status+"' \n");
				}
				
				sql.append(" and STATUS NOT IN('"+PickConstants.STATUS_MOVE+"','"+PickConstants.STATUS_CANCEL+"') \n");
				
				sql.append("\n  ORDER BY JOB_ID desc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("JOB_ID"));
					item.setDesc(rst.getString("NAME"));
					item.setStoreCode(Utils.isNull(rst.getString("store_code")));
					item.setStoreName(Utils.isNull(rst.getString("store_name")));
					item.setStoreNo(Utils.isNull(rst.getString("store_no")));
					item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
					item.setWareHouse(Utils.isNull(rst.getString("warehouse")));
					item.setWareHouseDesc(Utils.isNull(rst.getString("warehouse_desc")));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchStockIssue(PopupForm c,String status,String mode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n  SELECT warehouse,cust_group,customer_no");
				sql.append("\n  ,(select M.pens_desc from PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.customer_no) as customer_name ");
				sql.append("\n ,issue_req_no,issue_req_date,requestor,remark ");
				sql.append("\n ,(select nvl(sum(req_qty),0)  from pensbme_stock_issue_item d where d.issue_req_no=j.issue_req_no) total_req_qty");
				sql.append("\n FROM PENSBME_STOCK_ISSUE j WHERE 1=1 ");
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and j.issue_req_no = '"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and warehouse ='"+c.getDescSearch()+"' ");
				}
				if( !"view".equals(mode)){
				   sql.append("\n and STATUS IN('"+PickConstants.STATUS_POST+"','"+PickConstants.STATUS_BEF+"') ");
				}
				sql.append("\n  ORDER BY ISSUE_REQ_NO asc");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("issue_req_no"));
					item.setDesc(rst.getString("issue_req_no"));
					item.setStoreCode(Utils.isNull(rst.getString("customer_no")));
					item.setStoreName(Utils.isNull(rst.getString("customer_name")));
					item.setWareHouse(Utils.isNull(rst.getString("warehouse")));
					item.setWareHouseDesc(PickConstants.getWareHouseDesc(item.getWareHouse()));
					item.setCustGroup(Utils.isNull(rst.getString("cust_group")));
					item.setTotalReqQty(rst.getString("total_req_qty"));
					item.setRequestor(Utils.isNull(rst.getString("requestor")));
					item.setCustGroup(Utils.isNull(rst.getString("cust_group")));
					item.setIssueReqDate(Utils.stringValue(rst.getDate("issue_req_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					item.setRemark(Utils.isNull(rst.getString("remark")));
				
					//get sum all not in current box_no
					ScanCheckBean cri = new ScanCheckBean();
					cri.setIssueReqNo(item.getCode());
					cri.setWareHouse(item.getWareHouse());
					cri.setBoxNo(Utils.isNull(c.getBoxNo()).equals("")?"0":c.getBoxNo());
					item.setTotalQty(ScanCheckDAO.getTotalQtyNotInBoxNo(conn,cri)+"");
	                //add to List
					pos.add(item);
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static Barcode searchProductByBarcode(PopupForm c) throws Exception {
		 Connection conn = null;
	    try{
	    	conn = DBConnection.getInstance().getConnection();
		    return searchProductByBarcodeModelBMELockedModel(conn,c);
	    }catch(Exception e){
    		throw e;
    	}finally{
    		if(conn != null){
    			conn.close();
    		}
    	}
	 }
	 
    public static Barcode searchProductByBarcode(PopupForm c,String storeCode) throws Exception {
    	Connection conn = null;
    	try{
    		conn = DBConnection.getInstance().getConnection();
		    if(storeCode.startsWith(Constants.STORE_TYPE_FRIDAY_CODE)){
			   return searchProductByBarcodeModelFridayModel(conn,c);
		    }
		    return searchProductByBarcodeModelBMELockedModel(conn,c);
    	}catch(Exception e){
    		throw e;
    	}finally{
    		if(conn != null){
    			conn.close();
    		}
    	}
	 }
    
    public static Barcode searchProductByBarcode(Connection conn,PopupForm c,String storeCode) throws Exception {
		 if(storeCode.startsWith(Constants.STORE_TYPE_FRIDAY_CODE)){
			 return searchProductByBarcodeModelFridayModel(conn,c);
		 }
		 return searchProductByBarcodeModelBMELockedModel(conn,c);
	 }
    
    // Qty =1 can scan ,Qty =0->connot scan
    public static Barcode searchProductByBarcodeFromStockIssue(Connection conn ,HttpServletRequest request,String barcode,String matCode,String issueReqNo,String warehouse,String boxNo,String pensItem) throws Exception {
    	Barcode b = new Barcode();
    	try{
	    	//set default value
			b.setBarcode(barcode);
			b.setMaterialMaster(matCode);
			b.setPensItem(pensItem);
			
	    	//Compare TotalQty to Session ITEM_MAP
	    	int totalQtyByBarcodeAndPensItem = getTotalQtyProductByBarcodeAndPensItemModelStockIssue(conn,barcode,matCode,issueReqNo,warehouse,boxNo,pensItem);
	    	int totalQtyByBarcodeAndPensItemSession = 0;

    		String keyMap = barcode+pensItem;
    		Map<String, String> ITEM_MAP = (Map)request.getSession().getAttribute("ITEM_MAP");
    		totalQtyByBarcodeAndPensItemSession = Utils.convertStrToInt(ITEM_MAP.get(keyMap));
    		
    	   	logger.debug("totalQtyByBarcodeAndPensItem:"+totalQtyByBarcodeAndPensItem);
        	logger.debug("totalQtyByBarcodeAndPensItemSession:"+totalQtyByBarcodeAndPensItemSession);
        	
    		if(totalQtyByBarcodeAndPensItem >= totalQtyByBarcodeAndPensItemSession){
    			//Normal case 
    			return searchProductByBarcodeModelStockIssue(conn,barcode,matCode,issueReqNo,warehouse,boxNo,pensItem);
    		}else{
    			b.setQty(0);
    		    return b;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	b.setQty(0);
    	return b;
	 }
    
    public static Barcode getPensItemByBarcodeModelStockIssueIsQtyNotZero(Connection conn,String barcode,String matCode,String issueReqNo,String warehouse,String boxNo,Map<String, String> ITEM_MAP) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int remainQty = 0;
		Barcode bResult = null;
		String pensItem = "";
		String barcodeStr = "";
		String keyMap = "";
		try {
			sql.append("\n select i.* FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i WHERE 1=1 ");
			sql.append(" and i.issue_req_no = h.issue_req_no \n");
			sql.append(" and i.issue_req_no = '"+issueReqNo+"' \n");
			sql.append(" and h.warehouse = '"+warehouse+"' \n");
			
			if( !Utils.isNull(barcode).equals("")){
				sql.append(" and i.barcode = '"+barcode+"' \n");
			}
			if( !Utils.isNull(matCode).equals("")){
				sql.append("\n and i.material_master ='"+matCode+"'\n ");
			}
			sql.append("order by i.pens_item asc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
				pensItem = rst.getString("pens_item");
				barcodeStr = rst.getString("barcode");
				
				remainQty = getTotalQtyProductByBarcodeAndPensItemModelStockIssue(conn, barcode, matCode, issueReqNo, warehouse, boxNo, pensItem);
				
				keyMap = barcodeStr+pensItem;
				
				if(ITEM_MAP != null && ITEM_MAP.get(keyMap) != null){
					int qtyInSessionByBarcodePensItem = Utils.convertStrToInt(ITEM_MAP.get(keyMap));
					remainQty = remainQty - qtyInSessionByBarcodePensItem;
					
					logger.debug("before ["+pensItem+"]remainQtyDB["+remainQty+"]remainQtySession["+qtyInSessionByBarcodePensItem+"]");
				}
				logger.debug("result pens_item["+pensItem+"] remain qty:"+remainQty);
				if(remainQty != 0){
					bResult = new Barcode();
					bResult.setPensItem(pensItem);
					bResult.setQty(remainQty);//Total remain qty(barcode+pensItem)
					break;
				}
			
			}//while

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			if(rst != null) rst.close();
			if(stmt != null) stmt.close();	
		}
		return bResult;
	}
    
    public static int getTotalQtyProductByBarcodeAndPensItemModelStockIssue(Connection conn,String barcode,String matCode,String issueReqNo,String warehouse,String boxNo,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalQty = 0;
		try {
			sql.append("\n select sum(req_qty)as req_qty from( ");
				sql.append("\n select sum(req_qty) as req_qty " +
						   "\n FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i WHERE 1=1 ");
				sql.append("\n and i.issue_req_no = h.issue_req_no ");
				sql.append("\n and i.issue_req_no = '"+issueReqNo+"' ");
				sql.append("\n and h.warehouse = '"+warehouse+"' ");
				sql.append("\n and i.pens_item = '"+pensItem+"' ");
				
				if( !Utils.isNull(barcode).equals("")){
					sql.append("\n and barcode = '"+barcode+"'");
				}
				if( !Utils.isNull(matCode).equals("")){
					 sql.append("\n and material_master ='"+matCode+"'");
				}
				sql.append("\n UNION ALL");
				
				sql.append("\n select -1*count(*) as req_qty " +
						   "\n FROM PENSBME_SCAN_CHECKOUT h, PENSBME_SCAN_CHECKOUT_ITEM i WHERE 1=1 ");
				sql.append("\n and i.issue_req_no = h.issue_req_no ");
				sql.append("\n and i.issue_req_no = '"+issueReqNo+"' ");
				sql.append("\n and h.warehouse = '"+warehouse+"' ");
				sql.append("\n and i.pens_item = '"+pensItem+"' ");
				
				//logger.debug("boxNo:"+boxNo);
				if( !Utils.isNull(boxNo).equals("")){
				   sql.append("\n and i.box_no <> '"+boxNo+"' ");
				}
				if( !Utils.isNull(barcode).equals("")){
					sql.append("\n and i.barcode = '"+barcode+"' ");
				}
				if( !Utils.isNull(matCode).equals("")){
					sql.append("\n and i.material_master ='"+matCode+"'");
				}
			sql.append("\n ) ");
			
			//logger.debug("sql:"+sql);
		
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				totalQty = rst.getInt("req_qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			if(rst != null) rst.close();
			if(stmt != null) stmt.close();		
		}
		return totalQty;
	}
    
    public static Barcode searchProductByBarcodeModelStockIssue(Connection conn,String barcode,String matCode,String issueReqNo,String warehouse,String boxNo,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n select i.* FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i WHERE 1=1 ");
			sql.append(" and i.issue_req_no = h.issue_req_no \n");
			sql.append(" and i.issue_req_no = '"+issueReqNo+"' \n");
			sql.append(" and h.warehouse = '"+warehouse+"' \n");
			if( !Utils.isNull(pensItem).equals("")){
			   sql.append(" and i.pens_item = '"+pensItem+"' \n");
			}
			if( !Utils.isNull(barcode).equals("")){
				sql.append(" and i.barcode = '"+barcode+"' \n");
			}
			if( !Utils.isNull(matCode).equals("")){
				   sql.append("\n and i.material_master ='"+matCode+"'");
			}
			sql.append("order by i.pens_item asc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("barcode"));
				b.setMaterialMaster(rst.getString("material_master"));
				b.setGroupCode(rst.getString("group_code"));
				b.setPensItem(Utils.isNull(rst.getString("pens_item")));
				
				//int remainQtyNotInBoxNo  = getRemainQtyStockIssueByMatNotInBoxNo(conn,barcode,matCode,pensItem,issueReqNo,warehouse,boxNo); 
				b.setQty(1);
			
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			if(rst != null) rst.close();
			if(stmt != null) stmt.close();	
		}
		return b;
	}
    
    
    public static Barcode searchProductByBarcodeModelFridayModel(Connection conn,PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n select * FROM ");
			sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
			sql.append("\n and reference_code ='FridayItem' ");
			
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append(" and Interface_desc = '"+c.getCodeSearch()+"' \n");
			}
			if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
				   sql.append("\n and interface_value ='"+c.getMatCodeSearch()+"'");
			}
		
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("Interface_desc"));
				b.setMaterialMaster(rst.getString("interface_value"));
				b.setGroupCode(rst.getString("pens_desc2"));
				b.setPensItem(Utils.isNull(rst.getString("pens_value")));
				
				Barcode bLock = getRetailPriceCaseFridayFromBMELocked(conn,b.getGroupCode(),b.getMaterialMaster());
				if(bLock != null){
				  b.setWholePriceBF(bLock.getWholePriceBF());
				  b.setRetailPriceBF(bLock.getRetailPriceBF());
				}else{
				  b.setWholePriceBF("");
				  b.setRetailPriceBF("");	
				}
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return b;
	}
    public static Barcode searchProductByMat(String mat) throws Exception {
    	Connection conn = null;
    	try{
    		conn = new DBCPConnectionProvider().getConnection(conn);
    		return searchProductByMatModel(conn,mat);
    	}catch(Exception e){
    		throw e;
    	}finally{
    		if(conn !=null){
    			conn.close();
    		}
    	}
    }
    public static Barcode searchProductByMat(Connection conn,String mat) throws Exception {
    	return searchProductByMatModel(conn,mat);
    }
    
    public static Barcode searchProductByMatModel(Connection conn,String mat) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n select * from PENSBME_MST_REFERENCE WHERE 1=1 ");
			sql.append("\n and reference_code ='LotusItem' ");
			sql.append("\n and interface_value = '"+mat+"' ");
			sql.append("\n order by Interface_desc desc ");
		
			logger.debug("sql:"+sql);
			
			//conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("Interface_desc"));
				b.setMaterialMaster(rst.getString("interface_value"));
				b.setGroupCode(rst.getString("pens_desc2"));
				b.setPensItem(Utils.isNull(rst.getString("pens_value")));
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return b;
	}
    public static Barcode searchProductByMat(Connection conn,String storeType,String mat) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n select * from PENSBME_MST_REFERENCE WHERE 1=1 ");
			sql.append("\n and reference_code ='"+storeType+"' ");
			sql.append("\n and interface_value = '"+mat+"' ");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("Interface_desc"));
				b.setMaterialMaster(rst.getString("interface_value"));
				b.setGroupCode(rst.getString("pens_desc2"));
				b.setPensItem(Utils.isNull(rst.getString("pens_value")));
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return b;
	}
    public static Barcode getRetailPriceCaseFridayFromBMELocked(Connection conn,String groupCode,String mat) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
			sql.append("\n  from PENSBME_ONHAND_BME_LOCKED_FRI M   ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and (M.group_item ='"+groupCode+"' or MATERIAL_MASTER ='"+mat+"')");
			sql.append("\n  ORDER BY  M.RETAIL_PRICE_BF DESC ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("barcode"));
				b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
				b.setGroupCode(rst.getString("group_item"));
				b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
				b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
				b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return b;
	}
    
	 public static Barcode searchProductByBarcodeModelBMELockedModel(Connection conn,PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Barcode b = null;
			try {
				sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
				sql.append("\n  from PENSBME_ONHAND_BME_LOCKED M   ");
				sql.append("\n  where 1=1 ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.BARCODE ='"+c.getCodeSearch()+"'");
				}
				if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
					   sql.append("\n and M.MATERIAL_MASTER ='"+c.getMatCodeSearch()+"'");
				}
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					b = new Barcode();
					
					if( !Utils.isNull(rst.getString("group_item")).equals("") 
						&& !Utils.isNull(rst.getString("PENS_ITEM")).equals("") 
						&& !Utils.isNull(rst.getString("MATERIAL_MASTER")).equals("") ){
						
						b.setBarcode(rst.getString("barcode"));
						b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
						b.setGroupCode(rst.getString("group_item"));
						b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
						b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
						b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
					}else{
						sql = new StringBuilder();
						sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
						sql.append("\n  from PENSBME_ONHAND_BME M   ");
						sql.append("\n  where 1=1 ");
						if( !Utils.isNull(c.getCodeSearch()).equals("")){
							   sql.append("\n and M.BARCODE ='"+c.getCodeSearch()+"'");
						}
						if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
							   sql.append("\n and M.MATERIAL_MASTER ='"+c.getMatCodeSearch()+"'");
						}
						logger.debug("sql:"+sql);
						
						stmt = conn.createStatement();
						rst = stmt.executeQuery(sql.toString());
						if(rst.next()){
							b.setBarcode(rst.getString("barcode"));
							b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
							b.setGroupCode(rst.getString("group_item"));
							b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
							b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
							b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
						}
					}
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return b;
	}
	 
	 public static Barcode searchProductByPensItemModelBMELocked(Connection conn,String pensItem) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Barcode b = null;
			try {
				sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
				sql.append("\n  from pensbi.PENSBME_ONHAND_BME_LOCKED M   ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and M.PENS_ITEM ='"+pensItem+"'");
                sql.append("\n  order by BARCODE desc");
                
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					b = new Barcode();
					b.setBarcode(rst.getString("barcode"));
					b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
					b.setGroupCode(rst.getString("group_item"));
					b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
					b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
					b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				
				} catch (Exception e) {}
			}
			return b;
	}
	 
	 public static Barcode searchProductByGroupCodeModelBMELocked(Connection conn,String groupCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Barcode b = null;
			try {
				sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
				sql.append("\n  from pensbi.PENSBME_ONHAND_BME_LOCKED M   ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and M.GROUP_ITEM ='"+groupCode+"'");
                sql.append("\n  order by BARCODE desc");
             
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					b = new Barcode();
					b.setBarcode(rst.getString("barcode"));
					b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
					b.setGroupCode(rst.getString("group_item"));
					b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
					b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
					b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
		return b;
	}
	 
	
	 
	 public static Barcode searchProductByBarcodeMTT(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode b = null;
		try {
			sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF" +
					 " , to_char(round(retail_price_bf * 1.07,2),'FM999G9999D00') retail_invat  ");
			sql.append("\n  from pensbi.PENSBME_ONHAND_BME_LOCKED M   ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				   sql.append("\n and M.BARCODE ='"+c.getCodeSearch()+"'");
			}
			if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
				   sql.append("\n and M.MATERIAL_MASTER ='"+c.getMatCodeSearch()+"'");
			}
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("barcode"));
				b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
				b.setGroupCode(rst.getString("group_item"));
				b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
				b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
				b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_invat"), Utils.format_current_2_disgit));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return b;
	}
	 
	 public static List<PopupForm> searchCustGroup(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			sql.delete(0, sql.length());
			sql.append("\n select pens_value , Interface_desc  ");
			sql.append("\n FROM ");
			sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Idwacoal' ");
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append(" and Interface_desc LIKE '%"+c.getDescSearch()+"%' \n");
			}
			sql.append("\n  ORDER BY Interface_value asc \n");
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("pens_value"));
				item.setDesc(rst.getString("Interface_desc"));
				
				pos.add(item);
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
	}
	 
	 public static List<PopupForm> searchCustGroupByWareHouse(PopupForm c,String wareHouse) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			int no = 0;
			try {
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Customer' ");
			    sql.append("\n and ( pens_desc2 in( '"+wareHouse+"') or pens_desc2 is null) ");
				sql.append("\n  ORDER BY Interface_value asc ");
				
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_value"));
					item.setDesc(rst.getString("pens_desc"));
					pos.add(item);
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
	 }
	 
	 public static Map<String,String> initWarehouseDesc() {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			Map<String, String> wareHouseMap = new HashMap<String, String>();
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Warehouse' ");
				
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					wareHouseMap.put(rst.getString("pens_value"), rst.getString("pens_desc"));	
				}//while

			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return wareHouseMap;
		}
	 
	 public static String searchCustGroupDesc(String custCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String custDesc = "";
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			sql.delete(0, sql.length());
			sql.append("\n select pens_value , Interface_desc  ");
			sql.append("\n FROM ");
			sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Idwacoal' ");
			sql.append(" and pens_value = '"+custCode+"' \n");
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				custDesc = Utils.isNull(rst.getString("Interface_desc"));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return custDesc;
	}
	 
	 public static List<PopupForm> searchCustGroupMTT(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , interface_desc  FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and pens_value in ('020056' , '100001','020049','020047' ) ");
				sql.append("\n and reference_code ='Idwacoal' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and Interface_desc LIKE '%"+c.getDescSearch()+"%' \n");
				}
				sql.append("\n  ORDER BY Interface_value asc \n");
				
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_value"));
					item.setDesc(rst.getString("interface_desc"));
					
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchCustGroupByCustomer(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , pens_desc  FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='Customer' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and Interface_desc LIKE '%"+c.getDescSearch()+"%' \n");
				}
				sql.append("\n  ORDER BY Interface_value asc \n");
				
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_value"));
					item.setDesc(rst.getString("pens_desc"));
					
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchCustGroupInSaleOutPage(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , interface_desc  FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and pens_value in ('100001','020049','020047' ) ");
				sql.append("\n and reference_code ='Idwacoal' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and Interface_desc LIKE '%"+c.getDescSearch()+"%' \n");
				}
				sql.append("\n  ORDER BY Interface_value asc \n");
				
				logger.debug("sql:"+sql);

				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				
				PopupForm item = new PopupForm();
				item.setCode("");
				item.setDesc("");
				pos.add(item);
				
				while (rst.next()) {
					item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_value"));
					item.setDesc(rst.getString("interface_desc"));
					
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<References> searchWareHouseList() throws Exception {
		 try{
			 return searchWareHouseList("","");
		 }catch(Exception e){
			throw e;
		 }
	 }
	 
	 public static List<References> searchWareHouseList(String codeSqlIn,String codeSqlNotIn) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<References> pos = new ArrayList<References>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM PENSBME_MST_REFERENCE WHERE reference_code = 'Warehouse' ");
				if( !Utils.isNull(codeSqlIn).equals("")){
					sql.append("\n AND PENS_VALUE IN ("+codeSqlIn+")");
				}
				if( !Utils.isNull(codeSqlNotIn).equals("")){
					sql.append("\n AND PENS_VALUE NOT IN ("+codeSqlNotIn+")");
				}
				sql.append("\n  ORDER BY pens_value asc");
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					References item = new References(rst.getString("pens_value"),rst.getString("pens_value")+"-"+rst.getString("pens_desc"));
					pos.add(item);
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
	}
	 
	 public static String getStoreName(String storeCode) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getStoreNameModel(conn,storeCode);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 if(conn != null){
				 conn.close();
			 }
		 }
	 }
	 
	 public static String getStoreNameModel(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String storeName ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Store' ");
				sql.append("\n AND pens_value ='"+storeCode+"' \n");
				sql.append("\n \n");
				
				//logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					storeName = Utils.isNull(rst.getString("pens_desc"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return storeName;
		}
	 
	 public static String getJobName(String jobId,String status) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getJobNameModel(conn,jobId,status);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 if(conn != null){
				 conn.close();
			 }
		 } 
	 }
	 public static String getJobNameModel(Connection conn,String jobId,String status) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String jobName ="";
			try {
				sql.append("\n select name FROM ");
				sql.append("\n PENSBI.PENSBME_PICK_JOB WHERE 1=1 ");
				sql.append("\n AND job_id ="+jobId);
				if( !Utils.isNull(status).equals("")){
					sql.append("\n AND status ='"+jobId+"'");
				}
				sql.append("\n \n");
				
				//logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					jobName = Utils.isNull(rst.getString("name"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return jobName;
		}
	 
	 public static String getCustNoOracleModel(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String storeName ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select interface_value  ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Store' ");
				sql.append("\n AND pens_value ='"+storeCode+"' \n");
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					storeName = Utils.isNull(rst.getString("interface_value"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return storeName;
		}
	 public static String getSubInvModel(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String storeName ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select interface_desc  FROM ");
				sql.append("\n PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'SubInv' ");
				sql.append("\n AND pens_value ='"+storeCode+"' \n");
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					storeName = Utils.isNull(rst.getString("interface_desc"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return storeName;
		}
	 public static StoreBean getStoreBeanModel(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StoreBean s = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n select *  ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Store' ");
				sql.append("\n AND pens_value ='"+storeCode+"' \n");
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					s = new StoreBean();
					s.setStoreCode(Utils.isNull(rst.getString("pens_value")));
					s.setStoreName(Utils.isNull(rst.getString("pens_desc")));
					s.setStoreNo(Utils.isNull(rst.getString("interface_value")));
					//CustGroup
					//020049-1
					s.setCustGroup(s.getStoreCode().substring(0,6));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return s;
		}
	 
	 
	 public static String getCustNoOracleMTT(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String custNo ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select interface_value   ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Store' ");
				sql.append("\n AND pens_value ='"+storeCode+"' \n");
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					custNo = Utils.isNull(rst.getString("interface_value"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return custNo;
		}
	 
	 
	 public static List<Master> getCustGroupList(String custGroup) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<Master> dataList = new ArrayList<Master>();
			Master m = null;
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				sql.delete(0, sql.length());
				sql.append("\n select *   ");
				sql.append("\n FROM PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Customer' ");
				if( !Utils.isNull(custGroup).equals("")){
				  sql.append("\n AND pens_value ='"+custGroup+"' \n");
				}
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					m = new Master();
					m.setPensValue(rst.getString("pens_value"));
					m.setPensDesc(rst.getString("pens_desc"));
					
					dataList.add(m);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return dataList;
		}
	 
	 public static List<Master> getCustGroupListCaseAdd(String custGroup) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<Master> dataList = new ArrayList<Master>();
			Master m = null;
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				sql.delete(0, sql.length());
				sql.append("\n select *   ");
				sql.append("\n FROM PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Customer' ");
				if( !Utils.isNull(custGroup).equals("")){
				  sql.append("\n AND pens_value ='"+custGroup+"' \n");
				}
				 sql.append("\n AND pens_value not in( \n");
				 sql.append("\n   select group_store from PENSBME_LOCK_ITEM  \n");
				 sql.append("\n   where (unlock_date is null or unlock_date > sysdate) ");
				 sql.append("\n   and store_no ='allStore'");
				 sql.append("\n  ) \n");
				 
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					m = new Master();
					m.setPensValue(rst.getString("pens_value"));
					m.setPensDesc(rst.getString("pens_desc"));
					
					dataList.add(m);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return dataList;
		}
	 public static Master getMasterIdwacoal(Connection conn ,String type ,String custGroup) throws Exception{
			PreparedStatement ps =null;
			ResultSet rs = null;
			Master m = null;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" select *  from PENSBME_MST_REFERENCE WHERE Reference_code ='"+type+"' and pens_value ='"+custGroup+"' \n");
				
			    logger.debug("SQL:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					m = new Master();
					m.setReferenceCode(rs.getString("reference_code"));
					m.setPensValue(rs.getString("pens_value"));
					m.setPensDesc(Utils.isNull(rs.getString("pens_desc")));
					m.setInterfaceDesc(Utils.isNull(rs.getString("interface_desc")));
					m.setInterfaceValue(Utils.isNull(rs.getString("interface_value")));
					m.setPensDesc2(Utils.isNull(rs.getString("pens_desc2")));
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
	 
	 public static String searchPensItemByGroupCode(Connection conn,String groupCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String pensItem = "";
			try {
				sql.append("\n select pens_value FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='LotusItem' ");
				sql.append("\n and pens_desc2 = '"+groupCode+"' \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					pensItem = Utils.isNull(rst.getString("pens_value"));
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				
				} catch (Exception e) {}
			}
			return pensItem;
		}
	 
	 public static MasterItemBean getMasterItemByPensItem(Connection conn,String pensItem) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			MasterItemBean master = null;
			try {
				sql.append("\n select pens_value FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='LotusItem' ");
				sql.append("\n and pens_value = '"+pensItem+"' \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					master = new MasterItemBean();
					master.setPensItem(Utils.isNull(rst.getString("pens_value")));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return master;
		}
	 
	 public static List<References> getProductTypeListInterfaceICC() throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<References> statusList= new ArrayList<References>();
			Connection conn = null;
			try {
				sql.append("\n select distinct product FROM ");
				sql.append("\n PENSBME_CONFIG_INTERFACE ");
				sql.append("\n order by product ");
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					statusList.add(new References(rst.getString("product"), rst.getString("product")));
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				    conn.close();
				} catch (Exception e) {}
			}
			return statusList;
		}
	 
	 public static String validAsOfDate_EndDateStockLotus(String asOfDateStr,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String message = "";
			Date asOfDate = null;
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				asOfDate = Utils.parse(asOfDateStr, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				sql.append("\n select MAX(ENDING_DATE) as ENDING_DATE FROM PENSBME_ENDDATE_STOCK WHERE 1=1 ");
				sql.append("\n and STORE_CODE = '"+storeCode+"' \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					Date endingDate = rst.getDate("ENDING_DATE");
					logger.debug("asOfDate:"+asOfDate);
					logger.debug("endingDate:"+endingDate);
					if(rst.getDate("ENDING_DATE") != null){
						if(asOfDate.after(endingDate)){
							logger.debug("asOfDate.after(endingDate):"+asOfDate.after(endingDate));
						}else{
							message ="END_STOCK_LOTUS_DATE_MUST_MORE_THAN_ENDING_DATE";
						}
					}else{
						message ="END_STOCK_LOTUS_NOT_FOUND";
					}
				}else{
					message ="END_STOCK_LOTUS_NOT_FOUND";
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				    conn.close();
				} catch (Exception e) {}
			}
			return message;
		}
	 
	 public static List<Master> getStoreList(String groupStore) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Master m = null;
			List<Master> items = new ArrayList<Master>();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				sql.append("\n select pens_value ,pens_desc FROM ");
				sql.append("\n PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='Store' ");
				sql.append("\n and pens_value like '"+groupStore+"%' \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
			    while (rst.next()) {
					m = new Master();
					m.setPensValue(Utils.isNull(rst.getString("pens_value")));
					m.setPensDesc(Utils.isNull(rst.getString("pens_desc")));
					
					items.add(m);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				    conn.close();
				} catch (Exception e) {}
			}
			return items;
		}
}
