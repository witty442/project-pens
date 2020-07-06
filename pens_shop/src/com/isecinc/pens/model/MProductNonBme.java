package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 * Unit Of Measure
 * 
 * @author Aneak.t
 * @version $Id: MUOM.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MProductNonBme  {

	public static String TABLE_NAME = "m_product_non_bme";
	public static String COLUMN_ID = "product_code";
	
	private static final long serialVersionUID = 2037639420365632469L;

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */

	public String isProductNonBme(String productCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Connection conn = null;
		String isProductNonBme = "N";
		try{
			String sql ="\n select count(*) as c from m_product_non_bme where product_code ='"+productCode+"'" ;
			//logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				if(rst.getInt("c") >0){
				   isProductNonBme = "Y";
				}
			}
			return isProductNonBme;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
		        conn.close();
				stmt.close();
			} catch (Exception e2) {}
		}
	}


	
}
