package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BrandBean;
import com.isecinc.pens.bean.CustomerBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class BrandDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static BrandBean findCustomer(BrandBean bean) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			BrandBean resultBean = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT M.brand_no ,M.brand_desc " );
				sql.append("\n from PENSBI.XXPENS_BI_MST_BRAND M");
				sql.append("\n where M.brand_no ='"+bean.getBrand()+"'");
				sql.append("\n ORDER BY M.brand_no asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					resultBean = new BrandBean();
					resultBean.setBrand(rst.getString("brand_no"));
					resultBean.setBrandName(rst.getString("brand_desc"));
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
