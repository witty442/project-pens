package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.PD;
import com.isecinc.pens.bean.User;

public class MPD {
	
	private static Logger logger = Logger.getLogger("PENS");
	
	public static List<PD> getPDList(User user) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<PD> p = new ArrayList<PD>();
		
		String sql = " \n SELECT pd_code,pd_desc from m_pd where sales_code = '"+user.getUserName()+"' ";
					 
		logger.debug("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			
			PD pd = new PD();
			pd.setPdCode("");
			pd.setPdDesc("ไม่เลือก");
			p.add(pd);
			
			while(rst.next()){
				pd = new PD();
				pd.setPdCode(rst.getString("pd_code"));
				pd.setPdDesc(rst.getString("pd_desc"));
				
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
