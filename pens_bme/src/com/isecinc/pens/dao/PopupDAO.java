package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.Utils;

public class PopupDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static List<PopupForm> searchSalesrepSalesList(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			
			sql.append("\n SELECT distinct code ,salesrep_full_name ,Z.zone ,Z.zone_name" );
			sql.append("\n from apps.xxpens_salesreps_v M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n where M.salesrep_id =Z.salesrep_id ");
			sql.append("\n and z.zone in('0','1','2','3','4') ");
			
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append("\n and M.code ='"+c.getCodeSearch()+"' ");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append("\n and M.salesrep_full_name LIKE '%"+c.getDescSearch()+"%' ");
			}
			sql.append("\n  ORDER BY M.code asc ");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("code"));
				item.setDesc(rst.getString("salesrep_full_name"));
				item.setDesc2(rst.getString("zone"));
				item.setDesc3(rst.getString("zone_name"));
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
	
	 public static List<PopupForm> searchPensItemByGroupCode(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct pens_desc2 ");
				sql.append("\n ,NVL(( select max(whole_price_bf) from PENSBME_ONHAND_BME_LOCKED B where B.group_item =M.pens_desc2),0) as price ");
				sql.append("\n ,(SELECT MIN(pens_value) FROM PENSBME_MST_REFERENCE W WHERE W.pens_desc2 =M.pens_desc2 AND W.reference_code ='LotusItem' ) as pens_value ");
				sql.append("\n from PENSBME_MST_REFERENCE M ");
				sql.append("\n where 1=1 and reference_code ='LotusItem' ");
			
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_desc2 LIKE '%"+c.getCodeSearch()+"%' ");
				}
				
				sql.append("\n  ORDER BY pens_desc2 asc ");
				
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
					item.setDesc(rst.getString("pens_value"));
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
	 
	 public static List<PopupForm> searchSAEmp(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT emp_id,name ,surname ,branch, group_store");
				sql.append("\n from SA_EMPLOYEE M ");
				sql.append("\n where 1=1  ");
			
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and M.emp_id = '"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and M.name LIKE '%"+c.getDescSearch()+"%' ");
				}
				
				sql.append("\n  ORDER BY emp_id asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("emp_id"));
					item.setName(rst.getString("name"));
					item.setSurname(rst.getString("surname"));
					item.setBranch(rst.getString("branch"));
					item.setGroupStore(Utils.isNull(rst.getString("group_store")));
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
	 
	 public static List<PopupForm> searchSACheckStockDate(String empId,String type) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct count_stock_date from SA_REWARD_TRAN M");
				sql.append("\n where 1=1  ");
				sql.append("\n and M.emp_id = '"+empId+"' ");
				sql.append("\n and M.type = '"+type+"' ");
				sql.append("\n and (damage_use_flag is null or damage_use_flag ='')  ");
				sql.append("\n and M.count_stock_date is not null");
				sql.append("\n ORDER BY count_stock_date asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCheckStockDate(Utils.stringValue(rst.getDate("count_stock_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
