package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Transport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.Utils;

public class MTransport {
	private static Logger logger = Logger.getLogger("PENS"); 
	
	public static Transport searchTransport(PopupForm mCriteria, User user)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Transport m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		List<Transport> dataList = new ArrayList<Transport>();
		Map<String, Transport> regionMap = new HashMap<String, Transport>();
		Map<String,Transport>  provinceMap = new HashMap<String, Transport>();
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);

		/*	sql.append("\n  SELECT m.region,m.region_id,m.province_id ,m.district_id");
			sql.append("\n  ,(select p.name from pensso.m_transport_province p where p.province_id= m.province_id) as province_name");
			sql.append("\n  ,(select p.name from pensso.m_transport_district p where p.district_id= m.district_id) as amphur");
			sql.append("\n  ");*/
			
			sql.append("\n  select REGION,PROVINCE,AMPHUR");
			sql.append("\n  from  PENSSO.M_TRANSPORT m ");
			sql.append("\n  where 1=1");
			//for test 
			//sql.append("\n  and m.region ='กรุงเทพมหานคร'");
			//sql.append("\n  and m.region ='เหนือ'");
			sql.append("\n  ORDER BY m.region,m.province,m.amphur  \n");

			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				m = new Transport();
				m.setRegion(Utils.isNull(rst.getString("region")));
				m.setProvince(Utils.isNull(rst.getString("province")));
				m.setAmphur(Utils.isNull(rst.getString("amphur")));
				
				dataList.add(m);
				
				regionMap.put(m.getRegion(), m);
				provinceMap.put(m.getProvince(), m);

			}// while
			
			m = new Transport();
			m.setItemsList(dataList);
			m.setRegionList(new ArrayList<Transport>(regionMap.values()));
			m.setProvinceList(new ArrayList<Transport>(provinceMap.values()));
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
		return m;
	}
}
