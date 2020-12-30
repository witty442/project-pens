package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.District;
import com.pens.util.DBConnection;

/**
 * Model District Class
 * 
 * @author atiz.b
 * @version $Id: MDistrict.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class DistrictDAO {
	public static Logger logger = Logger.getLogger("PENS");
	
	public List<District> lookUp(String provinceId){
		List<District> pos = new ArrayList<District>();
		logger.debug("provinceId:"+provinceId);
		Connection conn = null;
		PreparedStatement ps=  null;
		ResultSet rs = null;
		String whereCause = "";
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			whereCause += "\n  select * from pensso.m_district ";
			whereCause += "\n   where province_id = "+provinceId;
			whereCause += " \n ORDER BY NAME ";
			
			ps = conn.prepareStatement(whereCause);
			rs = ps.executeQuery();
			while(rs.next()){
				pos.add(new District(rs));
			}
			logger.debug("whereClause :"+whereCause);
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
				if(ps != null){
					ps.close();
				}
				if(rs != null){
					rs.close();
				}
			}catch(Exception ee){}
		}
		return pos;
	}
	
}
