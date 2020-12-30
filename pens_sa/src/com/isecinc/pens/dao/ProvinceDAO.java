package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Province;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

/**
 * ProvinceDAO Class
 * 
 * @author atiz.b
 * @version $Id: MProvince.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ProvinceDAO  {

	public static Logger logger = Logger.getLogger("PENS");
	
	public List<Province> lookUp(int territory) {
		return lookUp(null,territory, "");
	}

	/**
	 * Look Up
	 */
	public List<Province> lookUp(User user,int territory,String notInProvinceId)  {
		List<Province> pos = new ArrayList<Province>();
		logger.debug("territory:"+territory);
		Connection conn = null;
		PreparedStatement ps=  null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append(" select * from pensso.m_province \n");
			sql.append("  where province_id in (\n");
			sql.append("    select m.province_id from pensso.m_map_province m,pensso.c_reference r \n");
			sql.append("    where m.reference_id = r.reference_id \n");
			if(territory != -1){
				sql.append("  and r.value = '" + territory + "'\n");
			}
			if( !Utils.isNull(notInProvinceId).equals("")){
				sql.append("  and province_id not in(" + SQLHelper.converToTextSqlIn(notInProvinceId) + ") \n");
			}
			sql.append("  ) \n");
			sql.append(" ORDER BY NAME ");
			
			logger.debug("sql :\n "+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				pos.add(new Province(rs));
			}
	
			
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
