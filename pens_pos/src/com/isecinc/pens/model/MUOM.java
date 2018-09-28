package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 * Unit Of Measure
 * 
 * @author Aneak.t
 * @version $Id: MUOM.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MUOM extends I_Model<UOM> {

	public static String TABLE_NAME = "m_uom";
	public static String COLUMN_ID = "UOM_ID";
	
	private static final long serialVersionUID = 2037639420365632469L;

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public UOM find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, UOM.class);
	}
	public UOM findOpt(String id) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Connection conn = null;
		UOM p = null;
		try{
			String sql ="\n select * from m_uom where uom_id ='"+id+"'" ;
			//logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new UOM(rst);
			}
			return p;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
		        conn.close();
				stmt.close();
			} catch (Exception e2) {}
		}
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
	public UOM[] search(String whereCause) throws Exception {
		List<UOM> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, UOM.class);
		if (pos.size() == 0) return null;
		UOM[] array = new UOM[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	/**
	 * Look Up
	 */
	public List<UOM> lookUp() {
		List<UOM> pos = new ArrayList<UOM>();
		try {
			String whereCause = "  AND ISACTIVE = 'Y' ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, UOM.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
