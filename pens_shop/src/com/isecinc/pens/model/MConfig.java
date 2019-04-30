package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ConfigBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class MConfig {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public ConfigBean getConfig(int userId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		ConfigBean p = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			String sql =" select c.* \n"
					+ " ,( select m.customer_id from m_customer m where m.code = c.customer_code) as customer_id \n"
					+ " from pens_shop.c_config c \n"
					+ " where user_id="+userId +"\n";
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new ConfigBean();
				p.setUserId(userId);
				p.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
				p.setCustomerId(rst.getInt("customer_id"));
				p.setPricelistId(rst.getInt("pricelist_id"));
			}
			return p;
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
			rst.close();
			stmt.close();
		}
	}
}
