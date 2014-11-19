package com.isecinc.pens.init;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.List;

import com.isecinc.core.bean.References;
import com.isecinc.core.init.I_Initial;

/**
 * Initial References
 * 
 * @author Atiz.b
 * @version $Id: InitialReferences.java,v 1.0 28/09/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InitialReferences extends I_Initial {


	public static final String STORE_TYPE = "StoreType";

	private static Hashtable<String, List<References>> referenes = new Hashtable<String, List<References>>();

	public void init() {}

	/**
	 * init with conn
	 */
	public void init(Connection conn) {
		try {
			
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}
	

	public static Hashtable<String, List<References>> getReferenes() {
		return referenes;
	}
}
