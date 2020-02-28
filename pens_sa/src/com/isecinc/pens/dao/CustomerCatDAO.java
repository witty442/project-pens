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

public class CustomerCatDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	/** Get SalesrepChannelList By User filter TT**/
	public static List<PopupBean> searchCustomerCatListModel(Connection conn,String custCatNo,User user,String roleNameChk){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct C.cust_cat_no,C.cust_cat_desc from PENSBI.XXPENS_BI_MST_CUST_CAT C  ");
			sql.append("\n  where 1=1 and c.cust_cat_desc is not null");
			if( !Utils.isNull(custCatNo).equals("")){
			  sql.append("\n  and C.cust_cat_no ='"+custCatNo+"'");
			}
		    /** ROLE_NAME_CHK =ALL  (eg .ROLE_CR_STOCK )**/
			String roleNameForCheck = "";
			if("ROLE_CR_STOCK".equalsIgnoreCase(roleNameChk)){
				roleNameForCheck = user.getRoleCRStock();
			}
	        if(roleNameForCheck.indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(user.getUserName())){
				//check user login is Sales (S404)
	        	SalesrepBean  salesrepBean = SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName().toUpperCase());
				if( salesrepBean != null && !Utils.isNull(salesrepBean.getSalesrepFullName()).equals("")){
					
				}
	        }
			sql.append("\n  ORDER BY C.cust_cat_no asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustCatNo(Utils.isNull(rst.getString("cust_cat_desc")));
				item.setCustCatDesc(Utils.isNull(rst.getString("cust_cat_desc")));
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
