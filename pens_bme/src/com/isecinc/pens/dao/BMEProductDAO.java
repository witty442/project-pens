package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class BMEProductDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static PopupBean searchBMEProduct(PopupBean c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		PopupBean item = null;;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			if(Utils.isNull(c.getCustGroup()).equalsIgnoreCase(PickConstants.STORE_TYPE_TERMINAL_CODE)){
				sql.append("\n   SELECT DISTINCT I.inventory_item_id as product_id");
				sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,XXPENS_OM_ITEM_MST_V I");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
				sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21') ");
				sql.append("\n   AND MP.pens_value =I.segment1 ");
				//condition
				if( !Utils.isNull(c.getPensItem()).equals("")){
					sql.append("\n  AND MP.PENS_VALUE ='"+Utils.isNull(c.getPensItem())+"'");
				}
				if( !Utils.isNull(c.getMaterialMaster()).equals("")){
					sql.append("\n  AND MP.INTERFACE_VALUE ='"+Utils.isNull(c.getMaterialMaster())+"'");
				}
			}else if(Utils.isNull(c.getCustGroup()).equalsIgnoreCase(PickConstants.STORE_TYPE_FRIDAY_CODE)){
				sql.append("\n   SELECT DISTINCT I.inventory_item_id as product_id");
				sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_FRIDAY_ITEM+"')");
				
			}else {
				//default all to lotus
				sql.append("\n   SELECT DISTINCT I.inventory_item_id as product_id");
				sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
			
			}
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			if (rst.next()) {
				item = new PopupBean();
				no++;
				item.setNo(no);
				item.setPensItem(rst.getString("pens_item"));
				item.setMaterialMaster(rst.getString("material_master"));
				item.setGroupCode(rst.getString("group_code"));
			
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
