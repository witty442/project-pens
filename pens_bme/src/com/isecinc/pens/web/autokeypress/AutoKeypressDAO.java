package com.isecinc.pens.web.autokeypress;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.Utils;

public class AutoKeypressDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static PopupForm searchSalesrepSalesDetail(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		PopupForm item = null;;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			sql.append("\n SELECT distinct code ,salesrep_full_name ,Z.zone ,Z.zone_name" );
			sql.append("\n from apps.xxpens_salesreps_v M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n where M.salesrep_id =Z.salesrep_id ");
			sql.append("\n and z.zone in('0','1','2','3','4') ");
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append("\n and M.code ='"+c.getCodeSearch()+"' ");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append("\n and M.salesrep_full_name LIKE '%"+c.getDescSearch()+"%' ");
			}
			sql.append("\n  ORDER BY M.code asc ");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			if (rst.next()) {
				item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("code"));
				item.setDesc(rst.getString("salesrep_full_name"));
				item.setDesc2(rst.getString("zone"));
				item.setDesc3(rst.getString("zone_name"));
			
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
		return item;
	}
	
}
