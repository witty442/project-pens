package com.isecinc.pens.web.popupsearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;

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
					sql.append("\n and M.pens_desc LIKE '%"+c.getDescSearch()+"%' ");
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
	 
	 public static List<PopupSearchForm> searchStyleMappingLotusList(PopupSearchForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupSearchForm> pos = new ArrayList<PopupSearchForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT * FROM PENSBME_STYLE_MAPPING M WHERE 1=1");
				
				if("FIND_GroupCode_IN_StyleMappingLotus".equalsIgnoreCase(c.getPageName()) ){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and M.material_master LIKE '"+c.getCodeSearch()+"%'");
					}
					sql.append("\n  ORDER BY M.material_master asc ");
					
				}else if("FIND_StyleNo_IN_StyleMappingLotus".equalsIgnoreCase(c.getPageName()) ){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and M.style ='"+c.getCodeSearch()+"' ");
					}
					sql.append("\n  ORDER BY M.style asc ");
				}else if("FIND_PensItem_IN_StyleMappingLotus".equalsIgnoreCase(c.getPageName()) ){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and M.pens_item ='"+c.getCodeSearch()+"' ");
					}
					sql.append("\n  ORDER BY M.pens_item asc ");
				}

				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupSearchForm item = new PopupSearchForm();
					no++;
					item.setNo(no);
					if("FIND_GroupCode_IN_StyleMappingLotus".equalsIgnoreCase(c.getPageName()) ){
					   item.setCode(rst.getString("material_master"));
					}else if("FIND_StyleNo_IN_StyleMappingLotus".equalsIgnoreCase(c.getPageName()) ){
					   item.setCode(rst.getString("style"));
					}else if("FIND_PensItem_IN_StyleMappingLotus".equalsIgnoreCase(c.getPageName()) ){
					   item.setCode(rst.getString("pens_item"));
					}
					
					item.setGroupCode(rst.getString("material_master"));
					item.setStyleNo(rst.getString("style"));
					item.setPensItem(rst.getString("pens_item"));
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
/**
 * 	
 * @param c
 * @param typeSearch :GroupCode:PensItem:Style
 * @return
 * @throws Exception
 */
	 public static List<PopupSearchForm> searchMstReference(PopupSearchForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupSearchForm> pos = new ArrayList<PopupSearchForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				//Exp :MAYA
				String groupStoreName = Utils.isNull(c.getCriteriaMap().get("groupStoreName"));

				if("FIND_GroupCode".equalsIgnoreCase(c.getPageName())){
					sql.append("\n SELECT distinct pens_desc2 FROM PENSBI.PENSBME_MST_REFERENCE M WHERE 1=1");
					sql.append("\n and M.reference_code ='LotusItem' ");
					if( !Utils.isNull(groupStoreName).equals("")){
						 sql.append("\n and M.pens_desc6 ='"+groupStoreName+"' ");
					}
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
				      sql.append("\n and M.pens_desc2 ='"+c.getCodeSearch()+"' ");
					}
					sql.append("\n  ORDER BY M.pens_desc2 asc ");
				}else if("FIND_PensItem".equalsIgnoreCase(c.getPageName())){
					sql.append("\n SELECT * FROM PENSBI.PENSBME_MST_REFERENCE M WHERE 1=1");
					sql.append("\n and M.reference_code ='LotusItem' ");
					if( !Utils.isNull(groupStoreName).equals("")){
						 sql.append("\n and M.pens_desc6 ='"+groupStoreName+"' ");
					}
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
				      sql.append("\n and M.pens_value ='"+c.getCodeSearch()+"' ");
					}
					sql.append("\n  ORDER BY M.pens_value asc ");
				}else if("FIND_Style".equalsIgnoreCase(c.getPageName())){
					sql.append("\n SELECT * FROM PENSBI.PENSBME_MST_REFERENCE M WHERE 1=1");
					sql.append("\n and M.reference_code ='LotusItem' ");
					if( !Utils.isNull(groupStoreName).equals("")){
						 sql.append("\n and M.pens_desc6 ='"+groupStoreName+"' ");
					}
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
				      sql.append("\n and M.interface_value ='"+c.getCodeSearch()+"' ");
					}
					sql.append("\n  ORDER BY M.pens_value asc ");
				}
				
			
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupSearchForm item = new PopupSearchForm();
					no++;
					item.setNo(no);
					if("FIND_GroupCode".equalsIgnoreCase(c.getPageName()) ){
					   item.setCode(rst.getString("pens_desc2"));
					   item.setGroupCode(rst.getString("pens_desc2"));
					}else if("FIND_PensItem".equalsIgnoreCase(c.getPageName()) ){
					   item.setGroupCode(rst.getString("pens_desc2"));
					   item.setStyleNo(rst.getString("interface_value"));
					   item.setPensItem(rst.getString("pens_value"));
					   item.setCode(rst.getString("pens_value"));
					}else if("FIND_Style".equalsIgnoreCase(c.getPageName()) ){
					   item.setCode(rst.getString("interface_value"));
						item.setGroupCode(rst.getString("pens_desc2"));
						item.setStyleNo(rst.getString("interface_value"));
						item.setPensItem(rst.getString("pens_value"));
					}
					
				
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
