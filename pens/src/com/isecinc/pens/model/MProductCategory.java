package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ProductCategory;
import com.isecinc.pens.bean.User;

/**
 * MProductCategory Class
 * 
 * @author Aneak.t
 * @version $Id: MProductCategory.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MProductCategory extends I_Model<ProductCategory> {

	private static final long serialVersionUID = -6654613129712945809L;

	public static String TABLE_NAME = "m_product_category";
	public static String COLUMN_ID = "PRODUCT_CATEGORY_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProductCategory find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ProductCategory.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public ProductCategory[] search(String whereCause) throws Exception {
		List<ProductCategory> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductCategory.class);
		if (pos.size() == 0) return null;
		ProductCategory[] array = new ProductCategory[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Look Up
	 */
	public List<ProductCategory> lookUp() {
		List<ProductCategory> pos = new ArrayList<ProductCategory>();
		try {
			String whereCause = "  AND ISACTIVE = 'Y' ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductCategory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look Up
	 * 
	 * @param segment
	 * @param psegmentId
	 * @return
	 * @throws Exception
	 */
	public List<ProductCategory> lookUp(int segment, int psegmentId) throws Exception {
		List<ProductCategory> pos = new ArrayList<ProductCategory>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = "SELECT SEG_ID" + segment + ", SEG_VALUE" + segment + " FROM " + MProductCategory.TABLE_NAME;
			sql += " WHERE 1=1 ";
			sql += "  AND ISACTIVE = 'Y' ";
			if (psegmentId != 0) sql += "  AND SEG_ID" + (segment - 1) + "=" + psegmentId;
			sql += "  AND SEG_ID" + segment + " IS NOT NULL";
			sql += " GROUP BY SEG_VALUE" + segment;
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			ProductCategory pc;
			while (rst.next()) {
				pc = new ProductCategory();
				pc.setId(rst.getInt("SEG_ID" + segment));
				pc.setName(ConvertNullUtil.convertToString(rst.getString("SEG_VALUE" + segment)));
				pos.add(pc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
	
	
	public List<References> lookUpBrandList(User u) throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			StringBuffer sql = new StringBuffer("select DISTINCT TRIM(SUBSTRING_INDEX(name,'-',1)) as brand from "+TABLE_NAME+" where NAME NOT IN('Default','ว่าง')");
			if(u != null){  
			   sql.append("\n AND PRODUCT_CATEGORY_ID NOT IN ");
			   sql.append("\n (SELECT  p2.PRODUCT_CATEGORY_ID  ");
			   sql.append("\n  FROM M_PRODUCT p1 , M_PRODUCT_CATEGORY p2 , M_PRODUCT_UNUSED p3  ");
			   sql.append("\n  WHERE p1.code = p3.code  and p3.type ='"+u.getRole().getKey()+"'");
			   sql.append("\n  AND p1.PRODUCT_CATEGORY_ID = p2.PRODUCT_CATEGORY_ID  )  ");
			}
			
			logger.debug("sql:\n"+sql.toString());
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				References r = new References(rst.getString("brand"),rst.getString("brand"),rst.getString("brand"));
				pos.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}

	public static int NO_OF_DISPLAY_COLUMNS = 4;
	public static int NO_OF_DISPLAY_ROWS = 3;
	public static int NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE = NO_OF_DISPLAY_COLUMNS * NO_OF_DISPLAY_ROWS ;
	
	public List<References> lookUpBrandList(int pageId,User u) throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		try {
			int startFromRow = pageId*NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE;
			
			StringBuffer sql = new StringBuffer("\n SELECT distinct pdc.seg_value1 as brand_code , TRIM(SUBSTRING_INDEX(name,'-',1)) as brand_name ");
					sql.append("\n FROM M_PRODUCT_CATEGORY pdc ")
					   .append("\n LEFT JOIN M_CATALOG cat ON cat.CODE =pdc.seg_value1 ")
					   .append("\n WHERE pdc.ISACTIVE = 'Y' ")
					   .append("\n AND pdc.PRODUCT_CATEGORY_ID IN (SELECT DISTINCT PRODUCT_CATEGORY_ID FROM M_PRODUCT WHERE ISACTIVE = 'Y') ")
					   .append("\n AND pdc.seg_value1 <> '000' ") //Except DefaultValue
					   .append("\n AND pdc.seg_value1 NOT IN (SELECT c.CODE FROM M_CATALOG c WHERE c.ISEXCLUDE ='Y') ") //Except DefaultValue
					   
					   .append("\n AND pdc.PRODUCT_CATEGORY_ID NOT IN ")
					   .append("\n (SELECT  p2.PRODUCT_CATEGORY_ID  ")
					   .append("\n FROM M_PRODUCT p1 , M_PRODUCT_CATEGORY p2 , M_PRODUCT_UNUSED p3  ")
					   .append("\n  WHERE p1.code = p3.code  and p3.type ='"+u.getRole().getKey()+"'")
					   .append("\n  AND p1.PRODUCT_CATEGORY_ID = p2.PRODUCT_CATEGORY_ID  )  ")
					
					   .append("\n ORDER BY COALESCE(cat.SEQ,9999), pdc.seg_value1 ")
					   .append("\n LIMIT "+ startFromRow+ ","+NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE );
					
			logger.debug("sql:\n"+sql.toString());
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				References r = new References(rst.getString("brand_code"),rst.getString("brand_code"),rst.getString("brand_name"));
				pos.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
	
	/**
	 * For MoveOrder
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public List<References> lookUpBrandListCaseMoveOrder(int pageId,User u) throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		try {
			int startFromRow = pageId*NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE;
			
			StringBuffer sql = new StringBuffer("\n SELECT distinct pdc.seg_value1 as brand_code , TRIM(SUBSTRING_INDEX(name,'-',1)) as brand_name ");
					sql.append("\n FROM M_PRODUCT_CATEGORY pdc ")
					   .append("\n LEFT JOIN M_CATALOG cat ON cat.CODE =pdc.seg_value1 ")
					   .append("\n WHERE pdc.ISACTIVE = 'Y' ")
					   .append("\n AND pdc.PRODUCT_CATEGORY_ID IN (SELECT DISTINCT PRODUCT_CATEGORY_ID FROM M_PRODUCT WHERE ISACTIVE = 'Y') ")
					   .append("\n AND pdc.seg_value1 <> '000' ") //Except DefaultValue
					   .append("\n AND pdc.seg_value1 NOT IN (SELECT c.CODE FROM M_CATALOG c WHERE c.ISEXCLUDE ='Y') ") //Except DefaultValue
					   
					   .append("\n AND pdc.PRODUCT_CATEGORY_ID NOT IN ")
					   .append("\n (SELECT  p2.PRODUCT_CATEGORY_ID  ")
					   .append("\n FROM M_PRODUCT p1 , M_PRODUCT_CATEGORY p2 , M_PRODUCT_UNUSED p3  ")
					   .append("\n  WHERE p1.code = p3.code  and p3.type ='"+u.getRole().getKey()+"'")
					   .append("\n  AND p1.PRODUCT_CATEGORY_ID = p2.PRODUCT_CATEGORY_ID  )  ")
					   
					   .append("\n ORDER BY COALESCE(cat.SEQ,9999), pdc.seg_value1 ")
					   .append("\n LIMIT "+ startFromRow+ ","+NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE );
					
			logger.debug("sql:\n"+sql.toString());
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				References r = new References(rst.getString("brand_code"),rst.getString("brand_code"),rst.getString("brand_name"));
				pos.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
}
