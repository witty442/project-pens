package com.isecinc.pens.web.popup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class PopupDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static List<PopupForm> searchItemBarcodeList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchItemBarcodeList");
				
				sql.append("\n SELECT * from m_barcode M \n");
				sql.append("\n where product_code in \n");
				sql.append("\n ( select code from m_product p,m_product_price pr \n");
				sql.append("\n  where p.product_id =pr.product_id and p.isactive ='Y' ) \n");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.product_code ='"+c.getCodeSearch()+"' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and M.material_master LIKE '%"+c.getDescSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getBarcodeSearch()).equals("")){
					sql.append("\n and M.barcode ='"+c.getBarcodeSearch()+"' \n");
				}
				
				sql.append("\n  ORDER BY M.product_code asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("product_code"));
					item.setDesc(rst.getString("material_master"));
					item.setCode2(rst.getString("barcode"));
					pos.add(item);
					
					//logger.debug("productCode:"+rst.getString("product_code"));
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
