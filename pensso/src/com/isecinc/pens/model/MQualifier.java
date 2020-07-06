package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Qualifier;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

/**
 * MQualifier Class
 * 
 * @author Atiz.b
 * @version $Id: MQualifier.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MQualifier extends I_Model<Qualifier> {

	private static final long serialVersionUID = 5856401903008785498L;

	public static String TABLE_NAME = "m_qualifier";
	public static String COLUMN_ID = "Qualifier_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Qualifier find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Qualifier.class);
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
	public Qualifier[] search(String whereCause) throws Exception {
		List<Qualifier> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Qualifier.class);
		if (pos.size() == 0) return null;
		Qualifier[] array = new Qualifier[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	public boolean canUseModifierLineId(String custGroup,int modifierLineId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		boolean canUseModifierLineId =true;
		try{
			conn = DBConnection.getInstance().getConnection();
			String  sql ="\n select * from m_qualifier where qualifier_context ='CUSTOMER_GROUP' ";
					sql+="\n and qualifier_type='Customer Group'";
					sql+="\n and qualifier_value='"+custGroup+"'";
					sql+="\n and modifier_Line_Id="+modifierLineId+"";
					
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				if( Utils.isNull(rst.getString("operator")).equals("NOT =")){
					canUseModifierLineId = false;
				}
			}
			return canUseModifierLineId;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
	}
}
