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
				sql.append("\n select M.* , BL.WHOLE_PRICE_BF, BL.RETAIL_PRICE_BF");
				sql.append("\n FROM ( ");
				sql.append("\n   SELECT DISTINCT MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
				sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21') ");
				//condition
				if( !Utils.isNull(c.getPensItem()).equals("")){
					sql.append("\n  AND MP.PENS_VALUE ='"+Utils.isNull(c.getPensItem())+"'");
				}
				if( !Utils.isNull(c.getMaterialMaster()).equals("")){
					sql.append("\n  AND MP.INTERFACE_VALUE ='"+Utils.isNull(c.getMaterialMaster())+"'");
				}
				sql.append("\n ) M ,PENSBI.PENSBME_ONHAND_BME_LOCKED BL");
				sql.append("\n WHERE M.barcode = BL.barcode ");
				sql.append("\n AND M.MATERIAL_MASTER = BL.MATERIAL_MASTER ");
				sql.append("\n AND M.PENS_ITEM = BL.PENS_ITEM ");
				
			}else if(Utils.isNull(c.getCustGroup()).equalsIgnoreCase(PickConstants.STORE_TYPE_FRIDAY_CODE)){
				sql.append("\n select M.* , BL.WHOLE_PRICE_BF, BL.RETAIL_PRICE_BF");
				sql.append("\n FROM ( ");
				sql.append("\n   SELECT DISTINCT MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_FRIDAY_ITEM+"')");
				//condition
				if( !Utils.isNull(c.getPensItem()).equals("")){
					sql.append("\n  AND MP.PENS_VALUE ='"+Utils.isNull(c.getPensItem())+"'");
				}
				if( !Utils.isNull(c.getMaterialMaster()).equals("")){
					sql.append("\n  AND MP.INTERFACE_VALUE ='"+Utils.isNull(c.getMaterialMaster())+"'");
				}
				sql.append("\n ) M ,PENSBI.PENSBME_ONHAND_BME_LOCKED_FRI BL");
				sql.append("\n WHERE M.barcode = BL.barcode ");
				sql.append("\n AND M.MATERIAL_MASTER = BL.MATERIAL_MASTER ");
				sql.append("\n AND M.PENS_ITEM = BL.PENS_ITEM ");
			}else {
				//default all to lotus
				sql.append("\n select M.* , BL.WHOLE_PRICE_BF, BL.RETAIL_PRICE_BF");
				sql.append("\n FROM ( ");
				sql.append("\n    SELECT DISTINCT MP.PENS_VALUE as PENS_ITEM ");
				sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
				sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
				sql.append("\n   ,MP.PENS_DESC2 as GROUP_CODE ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ");
				sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
				//condition
				if( !Utils.isNull(c.getPensItem()).equals("")){
					sql.append("\n  AND MP.PENS_VALUE ='"+Utils.isNull(c.getPensItem())+"'");
				}
				if( !Utils.isNull(c.getMaterialMaster()).equals("")){
					sql.append("\n  AND MP.INTERFACE_VALUE ='"+Utils.isNull(c.getMaterialMaster())+"'");
				}
				sql.append("\n ) M ,PENSBI.PENSBME_ONHAND_BME_LOCKED BL");
				sql.append("\n WHERE M.barcode = BL.barcode ");
				sql.append("\n AND M.MATERIAL_MASTER = BL.MATERIAL_MASTER ");
				sql.append("\n AND M.PENS_ITEM = BL.PENS_ITEM ");
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
				item.setBarcode(rst.getString("barcode"));
			    item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("whole_price_bf")));
			    item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf")));
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
