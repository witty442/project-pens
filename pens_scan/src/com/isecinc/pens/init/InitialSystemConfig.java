package com.isecinc.pens.init;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.List;

import com.isecinc.core.Database;
import com.isecinc.core.init.I_Initial;
import com.isecinc.pens.bean.SystemConfig;

/**
 * Initial Sysconfig
 * 
 * @author Atiz.b
 * @version $Id: InitialSystemConfig.java,v 1.0 7/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InitialSystemConfig extends I_Initial {

	private static Hashtable<String, SystemConfig> configs = new Hashtable<String, SystemConfig>();

	public void init() {

	}

	/**
	 * Init
	 */
	public void init(Connection conn) {
		try {
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public static Hashtable<String, SystemConfig> getConfigs() {
		return configs;
	}

}
