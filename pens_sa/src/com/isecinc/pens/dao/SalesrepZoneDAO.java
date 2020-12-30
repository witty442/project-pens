package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesZoneBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class SalesrepZoneDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	 public static String getSalesZoneDesc(String salesZone) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnectionApps();
			 return getSalesZoneDesc(conn, salesZone);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 if(conn != null){
				 conn.close();
			 }
		 }
	 }
	 public static String getSalesZoneDesc(Connection conn ,String salesZone) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String salesZoneDesc = "";
			try {
				if(Utils.isNull(salesZone).equals("")){
					return "";
				}
				sql.append("\n select ZONE_NAME ");
				sql.append("\n FROM PENSBI.XXPENS_BI_MST_SALES_ZONE  ");
				sql.append("\n WHERE zone = '"+salesZone+"' \n");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
			    if (rst.next()) {
			    	salesZoneDesc =Utils.isNull(rst.getString("ZONE_NAME"));
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return salesZoneDesc;
		}
	 public static SalesZoneBean getSalesZone(String salesZone) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnectionApps();
			 return getSalesZone(conn, salesZone);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 if(conn != null){
				 conn.close();
			 }
		 }
	 }
	 public static SalesZoneBean getSalesZone(Connection conn ,String salesZone) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			SalesZoneBean bean = null;
			try {
				if(Utils.isNull(salesZone).equals("")){
					return null;
				}
				sql.append("\n select ZONE_NAME ");
				sql.append("\n FROM PENSBI.XXPENS_BI_MST_SALES_ZONE  ");
				sql.append("\n WHERE zone = '"+salesZone+"' \n");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
			    if (rst.next()) {
			    	bean =  new SalesZoneBean();
			    	bean.setZone(Utils.isNull(rst.getString("zone")));
			    	bean.setZoneName(Utils.isNull(rst.getString("zone_name")));
			    	bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
			    	bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_code")));
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
		}
	 
	/** Get SalesrepZOneList By User filter TT**/
	public static List<PopupBean> searchSalesrepZoneListModel(Connection conn,User user,String roleNameChk){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isSetCustMapSalesTT = false;
		try{
			sql.append("\n  SELECT distinct S.zone,S.zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and zone in('0','1','2','3','4') ");
			
	        /** ROLE_NAME_CHK =ALL  (eg .ROLE_CR_STOCK )**/
			String roleNameForCheck = "";
			if("ROLE_CR_STOCK".equalsIgnoreCase(roleNameChk)){
				roleNameForCheck = user.getRoleCRStock();
				isSetCustMapSalesTT = GeneralDAO.isUserMapCustSalesTT(user);
			}
	        if(roleNameForCheck.indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(user.getUserName())){
	        	//filter by is  isSetCustMapSalesTT
	        	if(isSetCustMapSalesTT){
	        		sql.append("\n  and S.zone in(");
					sql.append("\n    select Z.zone from ");
					sql.append("\n     pensbi.XXPENS_BI_MST_SALES_ZONE Z");
					sql.append("\n    ,pensbi.XXPENS_BI_MST_CUST_CAT_MAP_TT CS");
					sql.append("\n    where CS.zone = Z.zone ");
					sql.append("\n    and CS.user_name = '"+user.getUserName()+"'");
					sql.append("\n  )");
	        	}else{
		        	//check user login is Sales (S404)
		        	SalesrepBean  salesrepBean = SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName().toUpperCase());
					if( salesrepBean != null && !Utils.isNull(salesrepBean.getSalesrepFullName()).equals("")){
						sql.append("\n  and S.salesrep_code  = '"+user.getUserName().toUpperCase()+"' ");
					}
	        	}
	        }
			sql.append("\n  ORDER BY S.zone asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesZone(Utils.isNull(rst.getString("zone")));
				item.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
}
