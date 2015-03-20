package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Barcode;
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
	 
	 
	 public static Barcode searchProductByBarcode(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			Barcode b = null;
			try {

				sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
				sql.append("\n  from pensbi.PENSBME_ONHAND_BME M   ");
				sql.append("\n  where 1=1 ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.BARCODE ='"+c.getCodeSearch()+"'");
				}
				if( !Utils.isNull(c.getMatCodeSearch()).equals("")){
					   sql.append("\n and M.MATERIAL_MASTER ='"+c.getMatCodeSearch()+"'");
				}
				sql.append("\n  UNION ALL ");
				sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
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
}
