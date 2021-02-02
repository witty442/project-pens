package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PD;
import com.isecinc.pens.bean.RecFor;
import com.isecinc.pens.bean.User;
import com.pens.util.DBCPConnectionProvider;

public class MRecFor {
	
	private static Logger logger = Logger.getLogger("PENS");
	
	public static List<RecFor> getReasonList() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<RecFor> p = new ArrayList<RecFor>();
		
		String sql = " \n SELECT lookup_code,meaning,description from m_recfor ";
					 
		logger.debug("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);

			while(rst.next()){
				RecFor pd = new RecFor();
				pd.setLookupCode(rst.getString("lookup_code"));
				pd.setMeaning(rst.getString("meaning"));
				pd.setDescription(rst.getString("description"));
				p.add(pd);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return p;
	}

}
