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

public class SalesrepChannelDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * getSalesChannelName
	 * @param salesChannelNo
	 * @return
	 * @throws Exception
	 */
	public static String getSalesChannelName(String salesChannelNo) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getSalesChannelName(conn,salesChannelNo);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	public static String getSalesChannelName(Connection conn,String salesChannelNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			if(Utils.isNull(salesChannelNo).equals("")){
				return "";
			}
			sql.append("\n  SELECT sales_channel_desc from PENSBI.XXPENS_BI_MST_SALES_CHANNEL  ");
			sql.append("\n  where  sales_channel_no ='"+salesChannelNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("sales_channel_desc"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	/** Get SalesrepChannelList By User filter TT**/
	public static List<PopupBean> searchSalesrepChannelListModel(Connection conn,User user,String roleNameChk){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.sales_channel_no ,S.sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and sales_channel_no in('0','1','2','3','4') ");
			
	        /** ROLE_NAME_CHK =ALL  (eg .ROLE_CR_STOCK )**/
			String roleNameForCheck = "";
			if("ROLE_CR_STOCK".equalsIgnoreCase(roleNameChk)){
				roleNameForCheck = user.getRoleCRStock();
			}
	        if(roleNameForCheck.indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(user.getUserName())){
				//check user login is Sales (S404)
	        	SalesrepBean  salesrepBean = SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName().toUpperCase());
				
	        	if( salesrepBean != null && !Utils.isNull(salesrepBean.getSalesrepFullName()).equals("")){
					sql.append("\n  and sales_channel_no  = '"+user.getUserName().substring(1,2)+"' ");
				}
	        }
			
			sql.append("\n  ORDER BY S.sales_channel_no asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesChannelNo(Utils.isNull(rst.getString("sales_channel_no")));
				item.setSalesChannelDesc(Utils.isNull(rst.getString("sales_channel_desc")));
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
