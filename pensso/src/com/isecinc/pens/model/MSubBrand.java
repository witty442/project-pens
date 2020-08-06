package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.ConvertNullUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ProductCategory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnectionApps;
import com.pens.util.Utils;

/**
 * MProductCategory Class
 * 
 * @author Aneak.t
 * @version $Id: MProductCategory.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MSubBrand  {

	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	
	public List<References> lookUpBrandListNew(User u) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
		    return lookUpBrandListNew(conn,u,false);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public List<References> lookUpBrandListNew(Connection conn,User u,boolean isCustHaveProductSpecial) throws Exception {
		List<References> pos = new ArrayList<References>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer("");
		try {
			sql.append("\n select A.* from (");
			sql.append("\n select DISTINCT TRIM(substr( name,0, INSTR(name,'-')-1 )) as brand  ,seg_value1 as brand_code ");
			//sql.append("\n from "+TABLE_NAME+" where NAME NOT IN('Default','ว่าง')");
			sql.append("\n AND ISACTIVE = 'Y'"); 
			sql.append("\n AND PRODUCT_CATEGORY_ID IN (SELECT DISTINCT PRODUCT_CATEGORY_ID FROM M_PRODUCT WHERE ISACTIVE = 'Y') ");
			sql.append("\n AND seg_value1 <> '000' ");
			sql.append("\n AND seg_value1 NOT IN (SELECT c.CODE FROM M_CATALOG c WHERE c.ISEXCLUDE ='Y') ");
			if(u != null){  
			   sql.append("\n AND PRODUCT_CATEGORY_ID NOT IN ");
			   sql.append("\n (SELECT  p2.PRODUCT_CATEGORY_ID  ");
			   sql.append("\n  FROM M_PRODUCT p1 , M_PRODUCT_CATEGORY p2 , M_PRODUCT_UNUSED p3  ");
			   sql.append("\n  WHERE p1.code = p3.code  and p3.type ='"+u.getRole().getKey()+"'");
			   sql.append("\n  AND p1.PRODUCT_CATEGORY_ID = p2.PRODUCT_CATEGORY_ID  )  ");
			// product Special
			   if(isCustHaveProductSpecial){
				   sql.append("\n AND PRODUCT_CATEGORY_ID in(");
				   sql.append("\n   select product_category_id from m_product where code in(");
				   sql.append("\n     select code from m_product_center ");
				   sql.append("\n    )  ");
				   sql.append("\n ) ");
			   }
			}
			sql.append("\n )A ");
			sql.append("\n WHERE A.brand <> '' and A.brand is not null ");
			logger.info("sql:\n"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				References r = new References(rst.getString("brand_code"),rst.getString("brand"),rst.getString("brand"));
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
			
		}
		return pos;
	}
	
	public List<References> lookUpSubBrandAllList(User u) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
		    return lookUpSubBrandAllList(conn,u);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	
	public List<References> lookUpSubBrandAllList(Connection conn,User u) throws Exception {
		List<References> pos = new ArrayList<References>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer("");
		try {
			sql.append("\n select AA.* from (");
			sql.append("\n  SELECT distinct sb.subbrand_no ,sb.subbrand_desc ");
			sql.append("\n  ,nvl((select cat.seq from pensso.m_catalog cat where sb.subbrand_no = cat.code),9999) as seq");
			sql.append("\n  FROM PENSBI.XXPENS_BI_MST_SUBBRAND sb ");
		    sql.append("\n  ,PENSSO.M_PRODUCT p ,PENSSO.M_PRODUCT_CATEGORY pdc");
		    sql.append("\n  WHERE p.product_id = sb.inventory_item_id"); 
		    sql.append("\n  AND p.product_category_id = pdc.product_category_id"); 
		    //FOR BUD ONLY
			sql.append("\n  AND pdc.seg_value1 in('381','382','383') "); 
			//Except DefaultValue No display
		    sql.append("\n  AND sb.subbrand_no NOT IN (");
		    sql.append("\n    SELECT c.CODE FROM PENSSO.M_CATALOG c WHERE c.ISEXCLUDE ='Y'");
		    sql.append("\n  ) ") ;
		    sql.append("\n  AND p.code NOT IN ( ");
		    sql.append("\n    SELECT  p3.code ");
		    sql.append("\n    FROM PENSSO.M_PRODUCT_UNUSED p3  ");
		    sql.append("\n    WHERE  p3.type ='"+u.getRole().getKey()+"'");
		    sql.append("\n   ) ");
		    sql.append("\n )AA ");
		    sql.append("\n ORDER BY AA.subbrand_no asc ");
			logger.debug("sql:\n"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				References r = new References(rst.getString("subbrand_no"),rst.getString("subbrand_no"),rst.getString("subbrand_desc"));
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
			
		}
		return pos;
	}

	public int lookUpSubBrandTotalRec(User u) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
		    return lookUpSubBrandTotalRec(conn,u,false);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public int lookUpSubBrandTotalRec(Connection conn,User u,boolean isCustHaveProductSpecial) throws Exception {
		int totalRec = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer("");
		try {
			sql.append("\n select count(*) as c from (");
			sql.append("\n  SELECT distinct sb.subbrand_no ,sb.subbrand_desc ");
			sql.append("\n  ,nvl((select cat.seq from pensso.m_catalog cat where sb.subbrand_no = cat.code),9999) as seq");
			sql.append("\n  FROM PENSBI.XXPENS_BI_MST_SUBBRAND sb ");
		    sql.append("\n  ,PENSSO.M_PRODUCT p ,PENSSO.M_PRODUCT_CATEGORY pdc");
		    sql.append("\n  WHERE p.product_id = sb.inventory_item_id"); 
		    sql.append("\n  AND p.product_category_id = pdc.product_category_id"); 
		    //FOR BUD ONLY
			sql.append("\n  AND pdc.seg_value1 in('381','382','383') "); 
			//Except DefaultValue No display
		    sql.append("\n  AND sb.subbrand_no NOT IN (");
		    sql.append("\n    SELECT c.CODE FROM PENSSO.M_CATALOG c WHERE c.ISEXCLUDE ='Y'");
		    sql.append("\n  ) ") ;
		    sql.append("\n  AND p.code NOT IN ( ");
		    sql.append("\n    SELECT  p3.code ");
		    sql.append("\n    FROM PENSSO.M_PRODUCT_UNUSED p3  ");
		    sql.append("\n    WHERE  p3.type ='"+u.getRole().getKey()+"'");
		    sql.append("\n   ) ");
		    sql.append("\n )AA ");
		    
			logger.info("sql:\n"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				totalRec = rst.getInt("c");
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
			
		}
		return totalRec;
	}
	
	public static int NO_OF_DISPLAY_COLUMNS = 5;
	public static int NO_OF_DISPLAY_ROWS = 3;
	public static int NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE = NO_OF_DISPLAY_COLUMNS * NO_OF_DISPLAY_ROWS ;
	
	public List<References> lookUpSubBrandListByPage(Connection conn ,int pageId,User u,boolean isCustHaveProductSpecial) throws Exception {
		List<References> pos = new ArrayList<References>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer("");
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select AA.* from (");
			sql.append("\n  SELECT distinct sb.subbrand_no ,sb.subbrand_desc ");
			sql.append("\n  ,nvl((select cat.seq from pensso.m_catalog cat where sb.subbrand_no = cat.code),9999) as seq");
			sql.append("\n  FROM PENSBI.XXPENS_BI_MST_SUBBRAND sb ");
		    sql.append("\n  ,PENSSO.M_PRODUCT p ,PENSSO.M_PRODUCT_CATEGORY pdc");
		    sql.append("\n  WHERE p.product_id = sb.inventory_item_id"); 
		    sql.append("\n  AND p.product_category_id = pdc.product_category_id"); 
		    //FOR BUD ONLY
			sql.append("\n  AND pdc.seg_value1 in('381','382','383') "); 
			//Except DefaultValue No display
		    sql.append("\n  AND sb.subbrand_no NOT IN (");
		    sql.append("\n    SELECT c.CODE FROM PENSSO.M_CATALOG c WHERE c.ISEXCLUDE ='Y'");
		    sql.append("\n  ) ") ;
		    sql.append("\n  AND p.code NOT IN ( ");
		    sql.append("\n    SELECT  p3.code ");
		    sql.append("\n    FROM PENSSO.M_PRODUCT_UNUSED p3  ");
		    sql.append("\n    WHERE  p3.type ='"+u.getRole().getKey()+"'");
		    sql.append("\n   ) ");
		    sql.append("\n )AA ");
		    sql.append("\n ORDER BY AA.seq, AA.subbrand_no ");
		    sql.append("\n   )A ");
		    sql.append("\n    WHERE rownum < (("+pageId+" * "+NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE+") + 1 )  ");
		    sql.append("\n )M  ");
		    sql.append("\n  WHERE r__ >= ((("+pageId+"-1) * "+NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE+") + 1)  ");
		    	
			logger.debug("sql:\n"+sql.toString());

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			    References r = new References(rst.getString("subbrand_no"),rst.getString("subbrand_no"),Utils.isNull(rst.getString("subbrand_desc")));
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
			
			StringBuffer sql = new StringBuffer("\n SELECT distinct pdc.seg_value1 as brand_code , TRIM(substr( name,0, INSTR(name,'-')-1 )) as brand_name ");
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
	
	public List<References> lookUpBrandListCaseMoveOrderByBrand(User u,String brand) throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			
			StringBuffer sql = new StringBuffer("\n SELECT distinct pdc.seg_value1 as brand_code , TRIM(substr( name,0, INSTR(name,'-')-1 )) as brand_name ");
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
					   .append("\n  AND pdc.seg_value1 ='"+brand+"'")
					   .append("\n ORDER BY COALESCE(cat.SEQ,9999), pdc.seg_value1 ");
	
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
	 * For Stock
	 * @param pageId
	 * @return
	 * @throws Exception
	 */
	public List<References> lookUpBrandListCaseStock(int pageId,User u) throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		try {
			int startFromRow = pageId*NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE;
			
			StringBuffer sql = new StringBuffer("\n SELECT distinct pdc.seg_value1 as brand_code , TRIM(substr( name,0, INSTR(name,'-')-1 )) as brand_name ");
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
	public List<References> lookUpBrandListCaseRequisitionProduct(int pageId,User u) throws Exception {
		List<References> pos = new ArrayList<References>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		try {
			int startFromRow = pageId*NO_OF_PRODUCT_DISPLAY_IN_ONE_PAGE;
			
			StringBuffer sql = new StringBuffer("\n SELECT distinct pdc.seg_value1 as brand_code , TRIM(substr( name,0, INSTR(name,'-')-1 )) as brand_name ");
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
