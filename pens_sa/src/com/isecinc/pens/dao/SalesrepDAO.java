package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class SalesrepDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	public static SalesrepBean getSalesrepBeanById(Connection conn,String salesrepId){
		SalesrepBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT * from xxpens_salesreps_v M  ");
			sql.append("\n  where  M.salesrep_id ="+salesrepId+"");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				bean = new SalesrepBean();
				bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				bean.setCode(Utils.isNull(rst.getString("code")));
				bean.setSalesrepFullName(Utils.isNull(rst.getString("salesrep_full_name")));
				bean.setRegion(Utils.isNull(rst.getString("region")));
				bean.setRegionName(Utils.isNull(rst.getString("region_name")));
				bean.setSalesChannel(Utils.isNull(rst.getString("sales_channel")));
				bean.setSalesChannelName(Utils.isNull(rst.getString("sales_channel_name")));
			}//if
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return bean;
	}
	public static SalesrepBean getSalesrepBeanByCode(String salesrepCode) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return getSalesrepBeanByCode(conn,salesrepCode);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public static SalesrepBean getSalesrepBeanByCode(Connection conn,String salesrepCode){
		SalesrepBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT * from apps.xxpens_salesreps_v M  ");
			sql.append("\n  where  M.code ='"+salesrepCode+"'");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				bean = new SalesrepBean();
				bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				bean.setCode(Utils.isNull(rst.getString("code")));
				bean.setSalesrepFullName(Utils.isNull(rst.getString("salesrep_full_name")));
				bean.setRegion(Utils.isNull(rst.getString("region")));
				bean.setRegionName(Utils.isNull(rst.getString("region_name")));
				bean.setSalesChannel(Utils.isNull(rst.getString("sales_channel")));
				bean.setSalesChannelName(Utils.isNull(rst.getString("sales_channel_name")));
			}//if
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return bean;
	}
	
	
	/** Get SalesrepList By User filter TT**/
	public static List<PopupBean> searchSalesrepListAll(String pageName,String salesChannelNo,String custCatNo
			,String salesZone,User user,String roleNameChk) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchSalesrepListAll(conn,pageName,salesChannelNo, custCatNo,salesZone,user,roleNameChk);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	/** Get SalesrepList By User filter TT**/
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String pageName,String salesChannelNo,String custCatNo
			,String salesZone,User user,String roleNameChk){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		boolean isSetCustMapSalesTT = false;
		try{
			sql.append("\n  SELECT distinct S.salesrep_code,S.salesrep_id from PENSBI.XXPENS_BI_MST_SALESREP S ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(custCatNo).equals("")){
				if( Utils.isNull(custCatNo).equalsIgnoreCase("CREDIT SALES")){
					sql.append("\n  and S.salesrep_code like 'S%' ");
					sql.append("\n  and S.salesrep_code not like 'SN%' ");
				}else if( Utils.isNull(custCatNo).equalsIgnoreCase("VAN SALES")){
					sql.append("\n  and S.salesrep_code like 'V%' ");
				}else{
					sql.append("\n  and S.salesrep_code not like 'SN%' ");
					sql.append("\n  and S.salesrep_code not like 'C%' ");
				}
			}else{
				sql.append("\n  and S.salesrep_code not like 'SN%' ");
				sql.append("\n  and S.salesrep_code not like 'C%' ");
			}
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n  and substr(salesrep_code,2,1)='"+Utils.isNull(salesChannelNo)+"'");
			}
			if( !Utils.isNull(salesZone).equals("")){
				sql.append("\n  and salesrep_code in(");
				sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
				sql.append("\n    where zone = "+Utils.isNull(salesZone) );
				sql.append("\n  )");
			}
		
			/** ROLE_NAME_CHK =ALL  (eg .ROLE_CR_STOCK )**/
			String roleNameForCheck = "";
			if("ROLE_CR_STOCK".equalsIgnoreCase(roleNameChk)){
				roleNameForCheck = user.getRoleCRStock();
				isSetCustMapSalesTT = GeneralDAO.isUserMapCustSalesTT(user);
			}
	        if(roleNameForCheck.indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(user.getUserName())){
	        	//filter by is  isSetCustMapSalesTT
	        	if(isSetCustMapSalesTT){
	        		sql.append("\n  and S.salesrep_code in(");
					sql.append("\n    select Z.salesrep_code from ");
					sql.append("\n     pensbi.XXPENS_BI_MST_SALES_ZONE Z");
					sql.append("\n    ,pensbi.XXPENS_BI_MST_CUST_CAT_MAP_TT CS");
					sql.append("\n    where CS.zone = Z.zone ");
					sql.append("\n    and CS.user_name = '"+user.getUserName()+"'");
					sql.append("\n  )");
	        	}else{
					//check user login is Sales (S404)
		        	SalesrepBean  salesrepBean = getSalesrepBeanByCode(conn, user.getUserName().toUpperCase());
					if( salesrepBean != null &&!Utils.isNull(salesrepBean.getSalesrepFullName()).equals("")){
						sql.append("\n  and S.salesrep_code  = '"+user.getUserName().toUpperCase()+"' ");
					}
	        	}
	        }
			sql.append("\n  ORDER BY S.salesrep_code asc ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				 
				 pos.add(bean);
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
