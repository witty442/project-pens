package com.isecinc.pens.model;

import java.sql.Connection;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.SystemConfig;

/**
 * I_Model Class
 * 
 * @author Atiz.b
 * @version $Id: I_Model.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MSysConfig extends I_Model<SystemConfig> {

	private static final long serialVersionUID = -6511820955495428770L;

	public static String TABLE_NAME = "c_sysconfig";
	public static String COLUMN_ID = "SYSCONFIG_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "VALUE", "UPDATED_BY" };

	/**
	 * Save
	 * 
	 * @param user
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(SystemConfig config, int activeUserID, Connection conn) throws Exception {
		Object[] values = { config.getId(), config.getValue(), activeUserID };
		return super.save(TABLE_NAME, columns, values, config.getId(), conn);
	}

}
