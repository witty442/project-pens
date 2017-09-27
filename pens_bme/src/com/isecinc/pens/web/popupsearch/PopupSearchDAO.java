package com.isecinc.pens.web.popupsearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class PopupSearchDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static List<PopupSearchForm> searchStoreCodeBMEList(PopupSearchForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupSearchForm> pos = new ArrayList<PopupSearchForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				String groupStore = Utils.isNull(c.getCriteriaMap().get("groupStore"));
				
				sql.append("\n SELECT M.pens_value as store_code,M.pens_desc as store_name "
						+ "FROM PENSBME_MST_REFERENCE M "
						+ "WHERE M.reference_code ='Store'");
				
				if( !Utils.isNull(groupStore).equals("")){
				   sql.append("\n and M.pens_value like '"+groupStore+"%' ");
				}
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.pens_value ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and B.pens_desc LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY M.pens_value asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupSearchForm item = new PopupSearchForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("store_code"));
					item.setDesc(rst.getString("store_name"));
					pos.add(item);
					
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
			return pos;
		}
	
}
