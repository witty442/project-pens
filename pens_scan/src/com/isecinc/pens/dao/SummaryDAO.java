package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.Constants;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class SummaryDAO {

	private static Logger logger = Logger.getLogger("PENS");
	
	
	  public List<Master> searchMasterList(String referenceCode,String orderBy) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<Master> pos = new ArrayList<Master>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				
				/*if("store".equalsIgnoreCase(referenceCode)){
					sql.append("\n select A.* from( ");
					sql.append("\n   SELECT a.* , " );
					sql.append("\n   ( CASE WHEN a.pens_value ='Dummy' THEN 0 ");
					sql.append("\n     ELSE  CAST(REPLACE(a.pens_value,'-','0') as INTEGER) END) as store_no_order ");
					sql.append("\n   from PENSBME_MST_REFERENCE a "); 
					sql.append("\n   where 1=1 and a.reference_code ='Store'  ");
					sql.append("\n)A ");
					sql.append("\n ORDER BY  A.store_no_order ");
				}else{*/
				  sql.append("\n  SELECT * from PENSBME_MST_REFERENCE \n");
				  sql.append("\n  where 1=1 and reference_code ='"+referenceCode+"' \n");
				//}
				
				sql.append("\n  ORDER BY "+orderBy+" asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					Master item = new Master();
					item.setReferenceCode(rst.getString("reference_code"));
					item.setInterfaceValue(rst.getString("interface_value"));
					item.setInterfaceDesc(rst.getString("interface_desc"));
					item.setPensValue(rst.getString("pens_value"));
					item.setPensDesc(rst.getString("pens_desc"));
					item.setPensDesc2(rst.getString("pens_desc2"));
					item.setPensDesc3(rst.getString("pens_desc3"));
					item.setCreateUser(rst.getString("create_user"));
					item.setSequence(rst.getString("sequence"));
					item.setStatus(rst.getString("status"));
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
	  
	  public static List<PopupForm> searchCustomerMaster(PopupForm c,String storeType,String operation) throws Exception {
		  return searchCustomerMaster(c, storeType, "", operation);
	  }
	  
	  public static List<PopupForm> searchCustomerMaster(PopupForm c,String storeType,String storeGroup,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* , \n");
				sql.append("\n  (select M1.interface_desc from PENSBME_MST_REFERENCE M1 ");
				sql.append("\n   where M1.reference_code = 'SubInv' and M1.pens_value =M.pens_value) as sub_inv ");
				
				sql.append("\n  from PENSBME_MST_REFERENCE M");
				
				sql.append("\n  where 1=1 and reference_code ='Store' ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				
				//StoreGroup
				if( !Utils.isNull(storeGroup).equals("")){
					sql.append(" and pens_value LIKE '"+storeGroup+"%' \n");
				}
				
				if( !Utils.isNull(storeType).equalsIgnoreCase("")){
					if(storeType.equalsIgnoreCase("lotus")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_LOTUS_CODE+"%' \n");
					}else if(storeType.equalsIgnoreCase("bigc")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_BIGC_CODE+"%' \n");
					}else if(storeType.equalsIgnoreCase("tops")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_TOPS_CODE+"%' \n");
					}else if(storeType.equalsIgnoreCase("MTT")){
						sql.append(" and ( pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_1+"%' \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_2+"%' ) \n");
					}
				}
				
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("pens_value")));
					item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					item.setStoreNo(Utils.isNull(rst.getString("interface_value")));
					item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
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
	  
	  public List<PopupForm> searchGroupMaster(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct pens_desc2 from PENSBME_MST_REFERENCE \n");
				sql.append("\n  where 1=1 and reference_code ='LotusItem' \n");
			
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_desc2 LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and pens_desc2 LIKE '%"+c.getDescSearch()+"%' \n");
				}
				
				sql.append("\n  ORDER BY pens_desc2 asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_desc2"));
					item.setDesc(rst.getString("pens_desc2"));
					
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
	  
	  public Date searchInitDateMTT(String custNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct trunc(Count_stk_date) as Count_stk_date from PENSBME_MTT_INIT_STK \n");
				sql.append("\n  where 1=1 and Cust_no ='"+custNo+"' \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()) {
			       initDate = rst.getDate("Count_stk_date");
					
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
			return initDate;
		}
	  
	  public List<PopupForm> searchProductFromORCL(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT ");
				sql.append("\n INVENTORY_ITEM_ID, ");
				sql.append("\n INVENTORY_ITEM_CODE, ");
				sql.append("\n INVENTORY_ITEM_DESC ");
				
				sql.append("\n FROM ");
				sql.append("\n XXPENS_BI_MST_ITEM ");
				sql.append("\n WHERE INVENTORY_ITEM_DESC LIKE 'ME%' ");

				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and INVENTORY_ITEM_CODE LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and INVENTORY_ITEM_DESC LIKE '%"+c.getDescSearch()+"%' \n");
				}
				
				sql.append("\n  ORDER BY INVENTORY_ITEM_CODE asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("INVENTORY_ITEM_CODE"));
					item.setDesc(rst.getString("INVENTORY_ITEM_DESC"));
					
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
	  
	  public static List<PopupForm> searchProductFromBMEMaster(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n select distinct A.item, A.group_item, nvl(OH.whole_price_bf,0) as price from( ");
				sql.append("\n  select distinct M.pens_value as item ,M.pens_desc2 as group_item ");
				sql.append("\n  from PENSBME_MST_REFERENCE M   ");
				sql.append("\n  where M.reference_code = 'LotusItem' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.pens_value LIKE '%"+c.getCodeSearch()+"%'");
				}
				
				sql.append("\n  union  ");
				
				sql.append("\n  select distinct M.pens_desc3 as item ,M.pens_desc2 as group_item ");
				sql.append("\n  from PENSBME_MST_REFERENCE M   ");
				sql.append("\n  where M.reference_code = 'LotusItem' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.pens_desc3 LIKE '%"+c.getCodeSearch()+"%'");
				}
				sql.append("\n )A LEFT OUTER JOIN ");
				sql.append("\n ( ");
				sql.append("\n  select group_item,whole_price_bf   ");
				sql.append("\n  from PENSBME_ONHAND_BME ");
				sql.append("\n ) OH ON A.group_item = OH.group_item ");
				sql.append("\n order by A.item asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("item"));
					item.setDesc(rst.getString("group_item"));
					item.setPrice(rst.getString("price"));
					
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
	  
	 
}
