package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import util.Constants;
import util.DBCPConnectionProvider;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class GeneralDAO {

	private static Logger logger = Logger.getLogger("PENS");
	
	 public static List<PopupForm> searchPickJob(PopupForm c,String status) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT DISTINCT JOB_ID,NAME,STORE_CODE,STORE_NO,SUB_INV,WAREHOUSE");
				sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Warehouse' and M.pens_value = j.WAREHOUSE) as WAREHOUSE_DESC ");
				
				sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.store_code) as STORE_NAME ");
				
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_PICK_JOB j WHERE 1=1 ");
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
				sql.delete(0, sql.length());
				sql.append("\n  SELECT warehouse,cust_group,customer_no");
				sql.append("\n  ,(select M.pens_desc from PENSBME_MST_REFERENCE M ");
				sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.customer_no) as customer_name ");
				sql.append("\n ,issue_req_no,issue_req_date,requestor,remark ");
				sql.append("\n ,(select nvl(sum(req_qty),0)  from pensbme_stock_issue_item d where d.issue_req_no=j.issue_req_no) total_req_qty");
				sql.append("\n FROM PENSBME_STOCK_ISSUE j WHERE 1=1 ");
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and j.issue_req_no = '"+c.getCodeSearch()+"' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and warehouse ='"+c.getDescSearch()+"' \n");
				}
				if( !Utils.isNull(status).equals("")){
					sql.append(" and STATUS ='"+status+"' \n");
				}
				//Case view get data status = I
				if( !"view".equals(mode))
				   sql.append(" and STATUS IN('"+PickConstants.STATUS_POST+"','"+PickConstants.STATUS_BEF+"') \n");
				
				sql.append("\n  ORDER BY ISSUE_REQ_NO asc \n");
				
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
					cri.setBoxNo("".equals(c.getBoxNo())?"0":c.getBoxNo());
					item.setTotalQty(ScanCheckDAO.getTotalQtyNotInBoxNo(conn,cri)+"");
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
		 
		 return searchProductByBarcodeModelBMELocked(c);
	 }
	 
    public static Barcode searchProductByBarcode(PopupForm c,String storeCode) throws Exception {
		 if(storeCode.startsWith(Constants.STORE_TYPE_FRIDAY_CODE)){
			 return searchProductByBarcodeModelFriday(c);
		 }
		 return searchProductByBarcodeModelBMELocked(c);
	 }
    
    public static Barcode searchProductByBarcodeFromStockIssue(PopupForm c,String issueReqNo,String warehouse,String boxNo) throws Exception {
		
		 return searchProductByBarcodeModelStockIssue(c,issueReqNo,warehouse,boxNo);
	 }
    
    public static Barcode searchProductByBarcodeModelStockIssue(PopupForm c,String issueReqNo,String warehouse,String boxNo) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode b = null;
		try {
			sql.append("\n select i.* FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i WHERE 1=1 ");
			sql.append(" and i.issue_req_no = h.issue_req_no \n");
			sql.append(" and i.issue_req_no = '"+issueReqNo+"' \n");
			sql.append(" and h.warehouse = '"+warehouse+"' \n");
			
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append(" and i.barcode = '"+c.getCodeSearch()+"' \n");
			}
			if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
				   sql.append("\n and i.material_master ='"+c.getMatCodeSearch()+"'");
			}
		
			logger.debug("sql:"+sql);
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("barcode"));
				b.setMaterialMaster(rst.getString("material_master"));
				b.setGroupCode(rst.getString("group_code"));
				b.setPensItem(Utils.isNull(rst.getString("pens_item")));
				
				//Get qty no in current box
				b.setQty(getRemainQtyStockIssueByMatNotInBoxNo(c,issueReqNo,warehouse,boxNo));
			
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
    
    public static int  getRemainQtyStockIssueByMatNotInBoxNo(PopupForm c,String issueReqNo,String warehouse,String boxNo) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
	    int qty = 0;
		try {
			sql.append("\n select sum(req_qty)as req_qty from( ");
				sql.append("\n select sum(req_qty) as req_qty " +
						   "\n FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i WHERE 1=1 ");
				sql.append("\n and i.issue_req_no = h.issue_req_no ");
				sql.append("\n and i.issue_req_no = '"+issueReqNo+"' ");
				sql.append("\n and h.warehouse = '"+warehouse+"' ");
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and barcode = '"+c.getCodeSearch()+"'");
				}
				if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
					 sql.append("\n and material_master ='"+c.getMatCodeSearch()+"'");
				}
				
				sql.append("\n UNION ALL");
				
				sql.append("\n select -1*count(*) as req_qty " +
						   "\n FROM PENSBME_SCAN_CHECKOUT h, PENSBME_SCAN_CHECKOUT_ITEM i WHERE 1=1 ");
				sql.append("\n and i.issue_req_no = h.issue_req_no ");
				sql.append("\n and i.issue_req_no = '"+issueReqNo+"' ");
				sql.append("\n and h.warehouse = '"+warehouse+"' ");
				sql.append("\n and i.box_no <> '"+boxNo+"' ");
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and i.barcode = '"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
					sql.append("\n and i.material_master ='"+c.getMatCodeSearch()+"'");
				}
			sql.append("\n ) ");
		
			logger.debug("sql:"+sql);
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				qty = rst.getInt("req_qty");
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
		return qty;
	}
    
    public static Barcode searchProductByBarcodeModelFriday(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
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
			
			//conn = DBConnection.getInstance().getConnection();
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("Interface_desc"));
				b.setMaterialMaster(rst.getString("interface_value"));
				b.setGroupCode(rst.getString("pens_desc2"));
				b.setPensItem(Utils.isNull(rst.getString("pens_value")));
				
				Barcode bLock = getRetailPriceByPensItem(conn,b.getPensItem());
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
				conn.close();
			} catch (Exception e) {}
		}
		return b;
	}
	 
    public static Barcode searchProductByMat(String mat) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode b = null;
		try {
			sql.append("\n select * from PENSBME_MST_REFERENCE WHERE 1=1 ");
			sql.append("\n and reference_code ='LotusItem' ");
			sql.append("\n and interface_value = '"+mat+"' ");
			sql.append("\n order by Interface_desc desc ");
		
			logger.debug("sql:"+sql);
			
			//conn = DBConnection.getInstance().getConnection();
			conn = new DBCPConnectionProvider().getConnection(conn);
			
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
				conn.close();
			} catch (Exception e) {}
		}
		return b;
	}
    
    public static Barcode getRetailPriceByPensItem(Connection conn,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
			sql.append("\n  from PENSBME_ONHAND_BME_LOCKED M   ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and M.PENS_ITEM ='"+pensItem+"'");
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
    
	 public static Barcode searchProductByBarcodeModelBMELocked(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			Barcode b = null;
			try {

				sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
				sql.append("\n  from PENSBME_ONHAND_BME M   ");
				sql.append("\n  where 1=1 ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.BARCODE ='"+c.getCodeSearch()+"'");
				}
				if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
					   sql.append("\n and M.MATERIAL_MASTER ='"+c.getMatCodeSearch()+"'");
				}
				sql.append("\n  UNION ALL ");
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
				
				//conn = DBConnection.getInstance().getConnection();
				conn = new DBCPConnectionProvider().getConnection(conn);
				
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
					conn.close();
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
	 
	 public static Barcode searchProductByPensItemModelByStep(Connection conn,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder step1 = new StringBuilder();
		StringBuilder step2 = new StringBuilder();
		Barcode b = null;
		try {
			//SELECT pens_desc2  GROUP_CODE FROM  PENSBI.PENSBME_MST_REFERENCE where reference_code = 'LotusItem' and pens_desc3 = :PENS Item
			//Step 1
			step1.append("\n  select pens_desc2 as GROUP_CODE ");
			step1.append("\n  from PENSBME_MST_REFERENCE M  ");
			step1.append("\n  where  reference_code = 'LotusItem'");
			step1.append("\n  and pens_desc3 ='"+pensItem+"'");
         
			logger.debug("step1:"+step1);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(step1.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setGroupCode(rst.getString("group_code"));
				b.setPensItem(pensItem);
			}else{
				//step 2
				step2.append("\n  select pens_desc2 as GROUP_CODE ");
				step2.append("\n  from PENSBME_MST_REFERENCE M  ");
				step2.append("\n  where  reference_code = 'LotusItem'");
				step2.append("\n  and pens_value ='"+pensItem+"'");
				logger.debug("step2:"+step2);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(step2.toString());
				if (rst.next()) {
					b = new Barcode();
					b.setGroupCode(rst.getString("group_code"));
					b.setPensItem(pensItem);
				}
			}

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
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Customer' ");
			    sql.append(" and ( pens_desc2 in( '"+wareHouse+"') or pens_desc2 is null) \n");
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
			 return searchWareHouseList("");
		 }catch(Exception e){
			throw e;
		 }
	 }
	 
	 public static List<References> searchWareHouseList(String codeSqlIn) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<References> pos = new ArrayList<References>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1  and reference_code = 'Warehouse' ");
				if( !Utils.isNull(codeSqlIn).equals("")){
					sql.append("\n AND PENS_VALUE IN ("+codeSqlIn+")");
				}
				sql.append("\n  ORDER BY pens_desc asc \n");
				
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
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
				
				logger.debug("sql:"+sql);

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
}
