package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CustomerBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class CustomerDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static CustomerBean findCustomer(CustomerBean bean) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			CustomerBean resultBean = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT distinct C.account_number as customer_code ,C.party_name as customer_desc " );
				sql.append("\n from apps.xxpens_ar_cust_sales_all M,apps.xxpens_ar_customer_all_v C");
				sql.append("\n ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
				sql.append("\n where M.cust_account_id = C.cust_account_id ");
				sql.append("\n and M.primary_salesrep_id = Z.salesrep_id ");
				
				//sql.append("\n and M.code like 'S%' ");//Credit Sales Only
				//sql.append("\n and Z.zone in('0','1','2','3','4') ");//fileter credit and van only
				sql.append("\n and C.account_number ='"+bean.getCustomerCode()+"'");
				sql.append("\n  ORDER BY C.account_number asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					resultBean = new CustomerBean();
					resultBean.setCustomerCode(rst.getString("customer_code"));
					resultBean.setCustomerName(rst.getString("customer_desc"));
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
			return resultBean;
		}
}
