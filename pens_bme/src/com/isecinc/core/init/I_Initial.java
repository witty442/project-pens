package com.isecinc.core.init;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Initial Class
 * 
 * @author Atiz.b
 * @version $Id: I_Initial.java,v 1.0 21/07/2010 00:00:00 atiz.b Exp $
 * 
 */
public abstract class I_Initial {

	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");

	/** Default Initial */
	public abstract void init();

	/** Default Initial with Connection */
	public abstract void init(Connection conn);
}
